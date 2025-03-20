package loremipsum.dev.taskmanagement;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import loremipsum.dev.taskmanagement.concretes.JwtService;
import loremipsum.dev.taskmanagement.enums.RoleType;
import loremipsum.dev.taskmanagement.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private String secretKey = "Y2hhbmdlbXlwYXNzd29yZC1vY2N1cHktdGVzdC1rZXktZXhhbXBsZQ==";
    private long jwtExpiration = 86400000;
    private long refreshExpiration = 2592000000L;
    private Key key;

    @BeforeEach
    void setUp() throws Exception {
        setPrivateField(jwtService, "secretKey", secretKey);
        setPrivateField(jwtService, "jwtExpiration", jwtExpiration);
        setPrivateField(jwtService, "refreshExpiration", refreshExpiration);
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    private void setPrivateField(Object targetObject, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, value);
    }

    @Test
    void testGenerateToken() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        assertEquals("testuser", claims.getSubject());
    }

    @Test
    void testGenerateRefreshToken() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        assertNotNull(refreshToken);

        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody();
        assertEquals("testuser", claims.getSubject());
    }

    @Test
    void testExtractUsername() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void testIsTokenValid() {
        User user = new User();
        user.setUsername("testuser");
        user.setRoles(Set.of(RoleType.TEAM_MEMBER));

        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenExpired() throws Exception {
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        Method method = JwtService.class.getDeclaredMethod("isTokenExpired", String.class);
        method.setAccessible(true);
        boolean isExpired = (boolean) method.invoke(jwtService, expiredToken);
        assertTrue(isExpired);
    }



    @Test
    void testExtractClaim() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        assertNotNull(expiration);
    }
    @Test
    void testIsTokenValid_WithExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        User user = new User();
        user.setUsername("testuser");
        user.setRoles(Set.of(RoleType.TEAM_MEMBER));
        when(userDetails.getUsername()).thenReturn("testuser");
        boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);

        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_WhenRolesAreNull() {
        User user = new User();
        user.setUsername("testuser");
        user.setRoles(null);

        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_WhenRolesAreEmpty() {
        User user = new User();
        user.setUsername("testuser");
        user.setRoles(Collections.emptySet());

        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }
    @Test
    void testIsTokenExpired_ShouldReturnTrue_WhenTokenIsExpired() throws Exception {
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        Method method = JwtService.class.getDeclaredMethod("isTokenExpired", String.class);
        method.setAccessible(true);

        boolean isExpired = (boolean) method.invoke(jwtService, expiredToken);
        assertTrue(isExpired);
    }

    @Test
    void testIsTokenValid_ShouldHandleExpiredJwtException() {
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        boolean result = jwtService.isTokenValid(expiredToken, userDetails);

        assertFalse(result);
    }

}