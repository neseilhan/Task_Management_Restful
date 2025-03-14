package loremipsum.dev.taskmanagement.repositories;

import loremipsum.dev.taskmanagement.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@EnableJpaRepositories
@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {
    @Query("select t from Token t where t.user.id = :id and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokenByUser(@Param("id") UUID userId);

    Optional<Token> findByToken(String token);
}