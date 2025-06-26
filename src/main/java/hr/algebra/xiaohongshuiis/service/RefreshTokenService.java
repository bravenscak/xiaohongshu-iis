package hr.algebra.xiaohongshuiis.service;

import hr.algebra.xiaohongshuiis.model.RefreshToken;
import hr.algebra.xiaohongshuiis.repository.RefreshTokenRepository;
import hr.algebra.xiaohongshuiis.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public RefreshToken createRefreshToken(String username) {
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUserInfo_Name(username);

        if (existingRefreshToken.isPresent()) {
            RefreshToken token = existingRefreshToken.get();
            if (token.getExpiryDate().compareTo(Instant.now()) > 0) {
                return token;
            } else {
                refreshTokenRepository.delete(token);
            }
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserInfo(appUserRepository.findByName(username));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(600000)); // 10 minutes

        return refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.isPresent()) {
            refreshTokenRepository.delete(refreshToken.get());
        } else {
            throw new RuntimeException("Refresh Token is not in DB");
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login");
        }
        return token;
    }

    public void deleteByUsername(String username) {
        refreshTokenRepository.deleteByUserInfo_Name(username);
    }
}