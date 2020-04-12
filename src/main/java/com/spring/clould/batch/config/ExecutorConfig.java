package com.spring.clould.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConfigurationProperties(prefix = "thread.pool")
public class ExecutorConfig {
	
	private int corePoolSize;
	
	private int maxPoolSize;
	
	private int queueCapacity;
	
	private int keepAliveSeconds;
	
	@Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        taskExecutor.setCorePoolSize(corePoolSize);
        //线程池维护线程的最大数量
        taskExecutor.setMaxPoolSize(maxPoolSize);
        //线程池所使用的缓冲队列
        taskExecutor.setQueueCapacity(queueCapacity);
        //线程池维护线程所允许的空闲时间
        taskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return taskExecutor;
    }

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getQueueCapacity() {
		return queueCapacity;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	public int getKeepAliveSeconds() {
		return keepAliveSeconds;
	}

	public void setKeepAliveSeconds(int keepAliveSeconds) {
		this.keepAliveSeconds = keepAliveSeconds;
	}
	
}
