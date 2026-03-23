package sqwore.deko.Tokens;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import sqwore.deko.Users.Users;

@Entity(
        name = "token"
)
@Table(
        name = "token"
)
public class Token {
    @Id
    @Column(
            name = "id",
            updatable = false
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "token",
            unique = true
    )
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tokenType",
            nullable = false
    )
    public TokenType tokenType;

    @Column(
            name = "revoked"
    )
    public boolean revoked = false;
    @Column(
            name = "expired"
    )
    public boolean expired= false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @JsonBackReference
    public Users user;

    public Token(String token, TokenType tokenType, Users user) {
        this.token = token;
        this.tokenType = tokenType;
        this.user = user;
    }
    public Token(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
