package sqwore.deko.Tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("""
    select t from token t
    where t.user.id = :id and t.expired = false and t.revoked = false
    """)
    List<Token> findAllValidTokenByUserId(@Param("id") Long id);
    Optional<Token> findByToken(String token);
}
