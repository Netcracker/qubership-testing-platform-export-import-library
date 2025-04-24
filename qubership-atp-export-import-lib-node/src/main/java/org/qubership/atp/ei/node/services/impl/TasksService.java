/*
 * # Copyright 2024-2025 NetCracker Technology Corporation
 * #
 * # Licensed under the Apache License, Version 2.0 (the "License");
 * # you may not use this file except in compliance with the License.
 * # You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * # Unless required by applicable law or agreed to in writing, software
 * # distributed under the License is distributed on an "AS IS" BASIS,
 * # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * # See the License for the specific language governing permissions and
 * # limitations under the License.
 */

package org.qubership.atp.ei.node.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.qubership.atp.ei.node.CancellableExportImportTask;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TasksService {

    private static final Map<String, Future<Object>> tasks = new HashMap<>();

    public Future<Object> getTaskById(String taskId) {
        return tasks.get(taskId);
    }

    /**
     * Cancel task.
     *
     * @param taskId the task id
     */
    public void cancelTask(String taskId) {
        Future task = tasks.get(taskId);
        if (task != null) {
            task.cancel(true);
        }
    }

    /**
     * Submit task.
     *
     * @param taskId   the task id
     * @param executor the executor
     */
    public void submitTask(String taskId, CancellableExportImportTask executor,
                           ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        ListenableFuture<Object> task = threadPoolTaskExecutor.submitListenable(executor);
        task.addCallback(new CleanTaskListCallback(taskId));
        task.addCallback(new MarkTaskAsCancelled(executor));
        tasks.put(taskId, task);
    }

    private static class CleanTaskListCallback implements ListenableFutureCallback<Object> {

        private final String taskId;

        CleanTaskListCallback(String taskId) {
            this.taskId = taskId;
        }

        @Override
        public void onFailure(Throwable thr) {
            tasks.remove(taskId);
        }

        @Override
        public void onSuccess(Object o) {
            tasks.remove(taskId);
        }
    }

    private static class MarkTaskAsCancelled implements ListenableFutureCallback<Object> {

        private final CancellableExportImportTask task;

        MarkTaskAsCancelled(CancellableExportImportTask task) {
            this.task = task;
        }

        @Override
        public void onFailure(Throwable thr) {
            task.setCancelled();
        }

        @Override
        public void onSuccess(Object o) {
        }
    }

}
