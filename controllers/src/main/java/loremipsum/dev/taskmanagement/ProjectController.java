package loremipsum.dev.taskmanagement;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IProjectService;
import loremipsum.dev.taskmanagement.resultHelper.Result;
import loremipsum.dev.taskmanagement.resultHelper.ResultData;
import loremipsum.dev.taskmanagement.resultHelper.ResultHelper;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.ProjectStatus;
import loremipsum.dev.taskmanagement.request.ProjectRequest;
import loremipsum.dev.taskmanagement.response.ProjectResponse;
import loremipsum.dev.taskmanagement.response.TaskResponse;
import loremipsum.dev.taskmanagement.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest projectRequest) {
        Project createdProject = projectService.createProject(projectRequest.toProject());
        ProjectResponse projectResponse = new ProjectResponse(createdProject);
        return ResponseEntity.ok(projectResponse);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable UUID projectId, @RequestBody ProjectRequest projectRequest) {
        Project updatedProject = projectService.updateProject(projectId, projectRequest.toProject());
        ProjectResponse projectResponse = new ProjectResponse(updatedProject);
        return ResponseEntity.ok(projectResponse);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable UUID projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);
        return project.map(value -> ResponseEntity.ok(new ProjectResponse(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        List<ProjectResponse> projectResponses = projects.stream().map(ProjectResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(projectResponses);
    }
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Result> deleteProject(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok(ResultHelper.success());
    }

    @GetMapping("/department/{departmentName}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByDepartment(@PathVariable String departmentName) {
        List<Project> projects = projectService.getProjectsByDepartment(departmentName);
        List<ProjectResponse> projectResponses = projects.stream().map(ProjectResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(projectResponses);
    }


    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByProjectId(@PathVariable UUID projectId) {
        List<Task> tasks = projectService.getTasksByProjectId(projectId);
        List<TaskResponse> taskResponses = tasks.stream().map(TaskResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("/{projectId}/team")
    public ResponseEntity<List<UserResponse>> getTeamMembersByProjectId(@PathVariable UUID projectId) {
        Set<User> teamMembers = projectService.getTeamMembersByProjectId(projectId);
        List<UserResponse> userResponses = teamMembers.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/{projectId}/status")
    public ResponseEntity<ProjectStatus> getProjectStatus(@PathVariable UUID projectId) {
        ProjectStatus projectStatus = projectService.getStatus(projectId);
        return ResponseEntity.ok(projectStatus);
    }

}