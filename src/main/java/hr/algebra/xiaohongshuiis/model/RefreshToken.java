package hr.algebra.xiaohongshuiis.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser userInfo;

    public RefreshToken() {}

    public RefreshToken(String token, Instant expiryDate, AppUser userInfo) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.userInfo = userInfo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Instant getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }

    public AppUser getUserInfo() { return userInfo; }
    public void setUserInfo(AppUser userInfo) { this.userInfo = userInfo; }
}