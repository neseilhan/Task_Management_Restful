package loremipsum.dev.taskmanagement;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"loremipsum.dev.taskmanagement.entities", "loremipsum.dev.taskmanagement.token"})
@EnableJpaRepositories(basePackages = {
        "loremipsum.dev.taskmanagement.repositories"
})
@ComponentScan(basePackages = {
        "loremipsum.dev.taskmanagement.services",
        "loremipsum.dev.taskmanagement.controllers",
        "loremipsum.dev.taskmanagement.dto",
        "loremipsum.dev.taskmanagement.application"
})
public class AppConfig {
}