package pixl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@SpringBootApplication(scanBasePackages = {"pixl"})
@EnableAsync
@EnableScheduling
public class Main {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }
}
