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

package org.qubership.atp.ei.node.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.qubership.atp.ei.node.exceptions.ExportException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRunsPolicy extends ThreadPoolExecutor.AbortPolicy {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor threadPoolExecutor) {
        log.info("CustomRunsPolicy: start rejectedExecution.\n"
                + "ThreadPoolExecutor = {}", threadPoolExecutor);
        try {
            super.rejectedExecution(r, threadPoolExecutor);
        } catch (Exception e) {
            String message = "Unable execute task because all threads are busy";
            log.error(message, e);
            ExportException.throwException(message, e);
        }
    }
}
