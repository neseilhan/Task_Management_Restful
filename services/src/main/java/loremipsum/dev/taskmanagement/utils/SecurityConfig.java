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
                        .requestMatchers("/home/**").hasAuthority("ROLE_" + RoleType.TEAM_MEMBER.name())
                        .requestMatchers("/home/**").hasAuthority("ROLE_" + RoleType.TEAM_LEADER.name())
                        .requestMatchers("/home/**").hasAuthority("ROLE_" + RoleType.PROJECT_MANAGER.name())

                        .requestMatchers("/projects/**").hasAuthority("ROLE_" + RoleType.PROJECT_MANAGER.name())
                        .requestMatchers("/projects/**").hasAuthority("ROLE_" + RoleType.TEAM_LEADER.name())

                        .requestMatchers("/tasks/**").hasAuthority("ROLE_" + RoleType.TEAM_MEMBER.name())
                        .requestMatchers("/tasks/**").hasAuthority("ROLE_" + RoleType.TEAM_LEADER.name())
                        .requestMatchers("/tasks/**").hasAuthority("ROLE_" + RoleType.PROJECT_MANAGER.name())

                        .requestMatchers("/attachments/**").hasAuthority("ROLE_" + RoleType.TEAM_MEMBER.name())
                        .requestMatchers("/attachments/**").hasAuthority("ROLE_" + RoleType.TEAM_LEADER.name())
                        .requestMatchers("/attachments/**").hasAuthority("ROLE_" + RoleType.PROJECT_MANAGER.name())

                        .requestMatchers("/tasks/update-name-description/**").hasAuthority("ROLE_" + RoleType.TEAM_LEADER.name())
                        .requestMatchers("/tasks/update-name-description/**").hasAuthority("ROLE_" + RoleType.PROJECT_MANAGER.name())

                        .requestMatchers("/tasks/comments/**").hasAuthority("ROLE_" + RoleType.TEAM_MEMBER.name())
                        .requestMatchers("/tasks/comments/**").hasAuthority("ROLE_" + RoleType.TEAM_LEADER.name())
                        .requestMatchers("/tasks/comments/**").hasAuthority("ROLE_" + RoleType.PROJECT_MANAGER.name())

                        .requestMatchers("/users/**").hasAuthority("ROLE_" + RoleType.TEAM_LEADER.name())
                        .requestMatchers("/users/**").hasAuthority("ROLE_" + RoleType.PROJECT_MANAGER.name())

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
