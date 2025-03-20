package loremipsum.dev.taskmanagement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loremipsum.dev.taskmanagement.concretes.LogoutService;
import loremipsum.dev.taskmanagement.token.Token;
import loremipsum.dev.taskmanagement.repositories.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogoutServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutService logoutService;

    private Token token;

    @BeforeEach
    void setUp() {
        token = new Token();
        token.setToken("jwt-token");
        token.setExpired(false);
        token.setRevoked(false);
    }

    @Test
    void testLogout_Success() {
        String authHeader = "Bearer jwt-token";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenRepository.findByToken("jwt-token")).thenReturn(java.util.Optional.of(token));

        logoutService.logout(request, response, authentication);

        assertThat(token.isExpired()).isTrue();
        assertThat(token.isRevoked()).isTrue();
        verify(tokenRepository).save(token);
        verify(tokenRepository).findByToken("jwt-token");
        verify(request).getHeader("Authorization");
        verify(authentication).getName();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void testLogout_NoAuthHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);

        logoutService.logout(request, response, authentication);

        verify(tokenRepository, never()).findByToken(anyString());
        verify(tokenRepository, never()).save(any(Token.class));
        verify(request).getHeader("Authorization");
    }

    @Test
    void testLogout_InvalidAuthHeader() {
        String authHeader = "InvalidHeader jwt-token";
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        logoutService.logout(request, response, authentication);

        verify(tokenRepository, never()).findByToken(anyString());
        verify(tokenRepository, never()).save(any(Token.class));
        verify(request).getHeader("Authorization");
    }

    @Test
    void testLogout_TokenNotFound() {
        String authHeader = "Bearer jwt-token";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenRepository.findByToken("jwt-token")).thenReturn(java.util.Optional.empty());

        logoutService.logout(request, response, authentication);

        verify(tokenRepository).findByToken("jwt-token");
        verify(tokenRepository, never()).save(any(Token.class));
        verify(request).getHeader("Authorization");
    }


    @Test
    void testLogout_AuthenticationNotNull() {
        String authHeader = "Bearer jwt-token";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenRepository.findByToken("jwt-token")).thenReturn(java.util.Optional.of(token));
        when(authentication.getName()).thenReturn("testuser");

        logoutService.logout(request, response, authentication);

        assertThat(token.isExpired()).isTrue();
        assertThat(token.isRevoked()).isTrue();
        verify(tokenRepository).save(token);
        verify(tokenRepository).findByToken("jwt-token");
        verify(request).getHeader("Authorization");
        verify(authentication, times(2)).getName();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void testLogout_AuthenticationNameNull() {
        String authHeader = "Bearer jwt-token";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenRepository.findByToken("jwt-token")).thenReturn(java.util.Optional.of(token));
        when(authentication.getName()).thenReturn(null);

        logoutService.logout(request, response, authentication);

        assertThat(token.isExpired()).isTrue();
        assertThat(token.isRevoked()).isTrue();
        verify(tokenRepository).save(token);
        verify(tokenRepository).findByToken("jwt-token");
        verify(request).getHeader("Authorization");
        verify(authentication).getName();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}