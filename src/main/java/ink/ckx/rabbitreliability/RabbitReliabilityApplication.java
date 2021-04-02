package ink.ckx.rabbitreliability;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("ink.ckx.rabbitreliability.mapper")
@SpringBootApplication
public class RabbitReliabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitReliabilityApplication.class, args);
    }

}