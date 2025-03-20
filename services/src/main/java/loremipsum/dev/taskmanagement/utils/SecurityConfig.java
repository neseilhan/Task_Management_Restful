package loremipsum.dev.taskmanagement.utils;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.enums.RoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {
            "auth/**"
    };

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST_URL).permitAll()

//                        .requestMatchers("/home/**").hasAnyAuthority(
//                                "ROLE_" + RoleType.TEAM_MEMBER.name(),
//                                "ROLE_" + RoleType.TEAM_LEADER.name(),
//                                "ROLE_" + RoleType.PROJECT_MANAGER.name()
//                        )

                        .requestMatchers("/projects").hasRole("PROJECT_MANAGER")
                        .requestMatchers("/projects/create").hasRole("PROJECT_MANAGER")
                        .requestMatchers("/projects/{projectId}").hasRole("PROJECT_MANAGER")
                        .requestMatchers("/projects/{projectId}/tasks").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/projects/department/{departmentName}").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/projects/{projectId}/team").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/projects/{projectId}/status").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/projects/{projectId}/delete").hasRole("PROJECT_MANAGER")


                        .requestMatchers("/tasks").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/tasks/create").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/tasks/{taskId}").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/tasks/{taskId}/status").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/tasks/{taskId}/progress").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/tasks/{taskId}/priority").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/tasks/{taskId}/title-description").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")


                        .requestMatchers("/attachments/task/{taskId}").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/attachments/{attachmentId}").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/attachments/task/{taskId}").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER")


                        .requestMatchers("/comments/task/{taskId}").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/comments/{id}").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/comments").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER")


                        .requestMatchers("/users/{id}").hasRole("PROJECT_MANAGER")
                        .requestMatchers("/users").hasRole("PROJECT_MANAGER")
                        .requestMatchers("/users/{id}").hasRole("PROJECT_MANAGER")
                        .requestMatchers("/users/assign-to-task").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")
                        .requestMatchers("/users/assign-to-project").hasAnyRole("TEAM_LEADER", "PROJECT_MANAGER")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );

        return http.build();
    }

}
