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

package org.qubership.atp.ei.node.services;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MetricsExportImportService {

    public static final String TOTAL_TIME_OF_EXPORT_PROCESSES = "total.time.export.processes";
    public static final String TOTAL_TIME_OF_IMPORT_PROCESSES = "total.time.import.processes";
    public static final String ACTIVE_EXPORT_PROCESS_COUNT = "active.export.process.count";
    public static final String ACTIVE_IMPORT_PROCESS_COUNT = "active.import.process.count";
    private static final String PROJECT_ID = "projectId";
    private static final String PROCESS_ID = "processId";
    private Map<String, Meter.Id> activeThreadCountMap = new ConcurrentHashMap<>();
    private final MeterRegistry meterRegistry;

    @Autowired
    public MetricsExportImportService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Register metric active process.
     */
    public void registerProcess(@NonNull String projectId, String processId, String metricName) {
        Counter counter = Counter.builder(metricName)
                .tag(PROJECT_ID, projectId)
                .tag(PROCESS_ID, processId)
                .register(meterRegistry);
        counter.increment();

        activeThreadCountMap.put(processId, counter.getId());
    }

    /**
     * Register metric runtime process.
     */
    public void addTimeMetric(UUID projectId, Stopwatch timer, String metricName) {
        timer.stop();
        meterRegistry.timer(metricName, PROJECT_ID, projectId.toString())
                .record(timer.elapsed());
    }

    /**
     * UnRegister gauge atp_execution_requests_in_progress.
     */
    public void unregisterProcess(UUID processId) {
        Meter.Id id = activeThreadCountMap.get(processId.toString());
        if (id != null) {
            meterRegistry.remove(id);
            activeThreadCountMap.remove(processId.toString());
        }
    }
}
