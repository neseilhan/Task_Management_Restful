package loremipsum.dev.taskmanagement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loremipsum.dev.taskmanagement.concretes.AuthenticationService;
import loremipsum.dev.taskmanagement.concretes.LogoutService;
import loremipsum.dev.taskmanagement.request.LoginRequest;
import loremipsum.dev.taskmanagement.request.RegisterRequest;
import loremipsum.dev.taskmanagement.response.AuthResponse;
import loremipsum.dev.taskmanagement.response.RegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private LogoutService logoutService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private RegisterResponse registerResponse;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setPassword("password");

        registerResponse = new RegisterResponse();
        registerResponse.setMessage("User registered successfully");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setPassword("password");

        authResponse = new AuthResponse();
        authResponse.setAccessToken("jwt-token");
    }

    @Test
    void testRegister() {
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        ResponseEntity<RegisterResponse> response = authController.register(registerRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(registerResponse);
        verify(authenticationService).register(any(RegisterRequest.class));
    }

    @Test
    void testAuthenticate() {
        when(authenticationService.authenticate(any(LoginRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.authenticate(loginRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(authResponse);
        verify(authenticationService).authenticate(any(LoginRequest.class));
    }

    @Test
    void testLogout() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);

        doNothing().when(logoutService).logout(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Authentication.class));

        authController.logout(request, response, authentication);

        verify(logoutService).logout(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Authentication.class));
    }
}