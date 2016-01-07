package pixl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@SpringBootApplication(scanBasePackages = {"pixl"})
public class Main {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @org.springframework.context.annotation.Configuration
    public static class Configuration {

        @Bean(initMethod = "afterPropertiesSet", destroyMethod = "destroy")
        public ThreadPoolTaskExecutor threadPoolTaskExecutor(@Value("${config.workerThreads}") int workerThreads) {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            threadPoolTaskExecutor.setCorePoolSize(workerThreads);
            threadPoolTaskExecutor.setMaxPoolSize(workerThreads);

            return threadPoolTaskExecutor;
        }

        @Bean(initMethod = "afterPropertiesSet", destroyMethod = "destroy")
        public ThreadPoolTaskScheduler threadPoolTaskScheduler(@Value("${config.workerThreads}") int workerThreads) {
            ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
            threadPoolTaskScheduler.setPoolSize(workerThreads);

            return threadPoolTaskScheduler;
        }


    }
}
