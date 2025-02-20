package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ScheduleApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class, args);
    }

    @Override
    public void run(String[] args) {
        System.out.println("Приложение запущено!");
    }
}
