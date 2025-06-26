package hr.algebra.xiaohongshuiis.controller;

import hr.algebra.xiaohongshuiis.dto.AuthRequestDTO;
import hr.algebra.xiaohongshuiis.dto.JwtResponseDTO;
import hr.algebra.xiaohongshuiis.dto.RefreshTokenRequestDTO;
import hr.algebra.xiaohongshuiis.model.AppUser;
import hr.algebra.xiaohongshuiis.model.RefreshToken;
import hr.algebra.xiaohongshuiis.repository.AppUserRepository;
import hr.algebra.xiaohongshuiis.service.JwtService;
import hr.algebra.xiaohongshuiis.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public JwtResponseDTO register(@RequestBody AuthRequestDTO authRequestDTO) {
        if (appUserRepository.findByName(authRequestDTO.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        AppUser newUser = new AppUser();
        newUser.setName(authRequestDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(authRequestDTO.getPassword()));
        newUser.setRole("USER");
        appUserRepository.save(newUser);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
        return new JwtResponseDTO(
                jwtService.generateToken(authRequestDTO.getUsername()),
                refreshToken.getToken()
        );
    }

    @PostMapping("/login")
    public JwtResponseDTO authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword())
        );

        if (authentication.isAuthenticated()) {
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            return new JwtResponseDTO(
                    jwtService.generateToken(authRequestDTO.getUsername()),
                    refreshToken.getToken()
            );
        } else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

    @PostMapping("/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getName());
                    return new JwtResponseDTO(accessToken, refreshTokenRequestDTO.getToken());
                })
                .orElseThrow(() -> new RuntimeException("Refresh Token is not in DB"));
    }

    @PostMapping("/logout")
    public void logout() {

    }
}