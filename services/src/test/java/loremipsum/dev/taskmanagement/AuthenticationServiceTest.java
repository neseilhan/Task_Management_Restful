package loremipsum.dev.taskmanagement;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loremipsum.dev.taskmanagement.concretes.AuthenticationService;
import loremipsum.dev.taskmanagement.concretes.JwtService;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.RoleType;
import loremipsum.dev.taskmanagement.repositories.TokenRepository;
import loremipsum.dev.taskmanagement.repositories.UserRepository;
import loremipsum.dev.taskmanagement.request.LoginRequest;
import loremipsum.dev.taskmanagement.request.RegisterRequest;
import loremipsum.dev.taskmanagement.response.AuthResponse;
import loremipsum.dev.taskmanagement.token.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    private AuthenticationService authenticationService;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ServletOutputStream outputStream;

    @Captor
    private ArgumentCaptor<Token> tokenCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(userRepository, tokenRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    void testRegister() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword123");

        RegisterRequest request = new RegisterRequest("john.doe", "john.doe@example.com", "password123", RoleType.TEAM_MEMBER);
        User user = new User("john.doe", "john.doe@example.com", "encodedPassword123", Collections.singleton(RoleType.TEAM_MEMBER));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthResponse response = authenticationService.register(request);
        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testAuthenticate() {
        LoginRequest request = new LoginRequest("john.doe@example.com", "password123");
        User user = new User("john.doe", "john.doe@example.com", "password123", Collections.singleton(RoleType.TEAM_MEMBER));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(authenticationManager.authenticate(any())).thenReturn(null);

        AuthResponse response = authenticationService.authenticate(request);

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        verify(userRepository).findByEmail(request.getEmail());
    }

    @Test
    void testRefreshToken() throws Exception {
        String refreshToken = "refresh-token";
        String userEmail = "john.doe@example.com";
        User user = new User("john.doe", "john.doe@example.com", "password123", Collections.singleton(RoleType.TEAM_MEMBER));

        when(jwtService.extractUsername(refreshToken)).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("new-jwt-token");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) {
                byteArrayOutputStream.write(b);
            }
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);
        authenticationService.refreshToken(request, response);
        verify(jwtService).generateToken(user);
        String responseBody = byteArrayOutputStream.toString();
        assertThat(responseBody).contains("new-jwt-token");
    }

    @Test
    void testRevokeAllUserTokens_noValidTokens() throws Exception {
        User user = new User("john.doe", "john.doe@example.com", "password123", Collections.singleton(RoleType.TEAM_MEMBER));
        when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(Collections.emptyList()); // No tokens
        Method method = AuthenticationService.class.getDeclaredMethod("revokeAllUserTokens", User.class);
        method.setAccessible(true);
        method.invoke(authenticationService, user);
        verify(tokenRepository, never()).saveAll(anyList());
    }

    @Test
    void testRevokeAllUserTokens_withValidTokens() throws Exception {
        User user = new User("john.doe", "john.doe@example.com", "password123", Collections.singleton(RoleType.TEAM_MEMBER));
        List<Token> validUserTokens = Arrays.asList(
                Token.builder().token("token1").revoked(false).expired(false).build(),
                Token.builder().token("token2").revoked(false).expired(false).build()
        );
        when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(validUserTokens);
        Method method = AuthenticationService.class.getDeclaredMethod("revokeAllUserTokens", User.class);
        method.setAccessible(true);
        method.invoke(authenticationService, user);
        validUserTokens.forEach(token -> {
            assertThat(token.isExpired()).isTrue();
            assertThat(token.isRevoked()).isTrue();
        });
        verify(tokenRepository).saveAll(validUserTokens);
    }

    @Test
    void testRefreshToken_withoutAuthorizationHeader() throws IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        authenticationService.refreshToken(request, response);

        verify(jwtService, never()).extractUsername(any());
        verify(jwtService, never()).isTokenValid(any(), any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testRefreshToken_withoutBearerPrefix() throws IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidToken");
        authenticationService.refreshToken(request, response);

        verify(jwtService, never()).extractUsername(any());
        verify(jwtService, never()).isTokenValid(any(), any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testRefreshToken_invalidHeader() throws IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        authenticationService.refreshToken(request, response);

        verify(jwtService, never()).extractUsername(any());
        verify(jwtService, never()).isTokenValid(any(), any());
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testRefreshToken_userNotFound() throws IOException {
        String validAuthHeader = "Bearer validRefreshToken";
        String userEmail = "user@example.com";
        String refreshToken = "validRefreshToken";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(validAuthHeader);
        when(jwtService.extractUsername(refreshToken)).thenReturn(userEmail);

        when(userRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.refreshToken(request, response);
        });
        assertEquals("User not found", exception.getMessage());

        verify(jwtService, never()).isTokenValid(any(), any());
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testRefreshToken_nullUserEmail() throws IOException {
        String validAuthHeader = "Bearer validRefreshToken";
        String refreshToken = "validRefreshToken";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(validAuthHeader);
        when(jwtService.extractUsername(refreshToken)).thenReturn(null);

        authenticationService.refreshToken(request, response);

        verify(jwtService, never()).isTokenValid(any(), any());
        verify(tokenRepository, never()).save(any(Token.class));
        verify(response, never()).getOutputStream();
    }

    @Test
    void testRefreshToken_invalidToken() throws IOException {
        String validAuthHeader = "Bearer invalidRefreshToken";
        String userEmail = "user@example.com";
        String refreshToken = "invalidRefreshToken";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(validAuthHeader);
        when(jwtService.extractUsername(refreshToken)).thenReturn(userEmail);

        User mockUser = new User();
        mockUser.setEmail(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(mockUser));
        when(jwtService.isTokenValid(refreshToken, mockUser)).thenReturn(false);

        authenticationService.refreshToken(request, response);
        verify(jwtService).extractUsername(refreshToken);
        verify(jwtService).isTokenValid(refreshToken, mockUser);
        verify(tokenRepository, never()).save(any(Token.class));
        verify(response, never()).getOutputStream();
    }

    @Test
    void testRegister_withRoleType() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setRoleType(RoleType.PROJECT_MANAGER);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        AuthResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void testRegister_withNullRoleType() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setRoleType(null);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        AuthResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }
}