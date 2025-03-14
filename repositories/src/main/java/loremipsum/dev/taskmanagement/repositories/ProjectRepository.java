package loremipsum.dev.taskmanagement.repositories;

import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@EnableJpaRepositories
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByStatus(ProjectStatus status);
}