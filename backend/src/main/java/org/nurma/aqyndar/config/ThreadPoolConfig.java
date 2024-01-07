package org.nurma.aqyndar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "textToSpeechExecutor")
    public AsyncTaskExecutor textToSpeechExecutor() {
        final int corePoolSize = 4;
        final int maxPoolSize = 16;
        final int queueCapacity = 32;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("TTSExecutor-");
        executor.initialize();
        return executor;
    }
}

