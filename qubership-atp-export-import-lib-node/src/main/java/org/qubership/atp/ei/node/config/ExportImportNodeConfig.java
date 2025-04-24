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

import org.qubership.atp.ei.node.clients.ExportImportFeignClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;

@ComponentScan({"org.qubership.atp.ei.node"})
@EnableFeignClients(clients = {ExportImportFeignClient.class})
@Configuration
public class ExportImportNodeConfig {

    @Value("${atp.export.threadPool.corePoolSize:0}")
    private Integer corePoolSize;

    @Value("${atp.export.threadPool.maxPoolSize:40}")
    private Integer maxPoolSize;

    @Value("${atp.export.threadPool.queueCapacity:0}")
    private Integer queueCapacity;

    public static String DEFAULT_WORK_DIR;

    @Value("${atp.export.workdir:exportimport/node}")
    public void setDefaultWorkDir(String defaultWorkDir) {
        DEFAULT_WORK_DIR = defaultWorkDir;
    }

    /**
     * Export thread executor thread pool task executor.
     *
     * @return the thread pool task executor
     */
    @Bean("atpExportThreadExecutor")
    public ThreadPoolTaskExecutor atpExportThreadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("atp-export-");
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new CustomRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * Import thread executor thread pool task executor.
     *
     * @return the thread pool task executor
     */
    @Bean("atpImportThreadExecutor")
    public ThreadPoolTaskExecutor atpImportThreadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("atp-import-");
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new CustomRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public ExecutorServiceMetrics executorExportMetrics(
            @Qualifier("atpExportThreadExecutor") ThreadPoolTaskExecutor applicationTaskExecutor) {
        return new ExecutorServiceMetrics(applicationTaskExecutor.getThreadPoolExecutor(),
                applicationTaskExecutor.getThreadNamePrefix(), "export_threads", Tags.empty());
    }

    @Bean
    public ExecutorServiceMetrics executorImportMetrics(
            @Qualifier("atpImportThreadExecutor") ThreadPoolTaskExecutor applicationTaskExecutor) {
        return new ExecutorServiceMetrics(applicationTaskExecutor.getThreadPoolExecutor(),
                applicationTaskExecutor.getThreadNamePrefix(), "import_threads", Tags.empty());
    }
}
