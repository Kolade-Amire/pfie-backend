package com.kay.pfie.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.kay.pfie.config.PfieProperties;
import com.kay.pfie.user.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final GoogleIdTokenService googleIdTokenService;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;
    private final PfieProperties props;

    public AuthService(
            GoogleIdTokenService googleIdTokenService,
            JwtService jwtService,
            UserRepository userRepo,
            UserProfileRepository profileRepo,
            PfieProperties props
    ) {
        this.googleIdTokenService = googleIdTokenService;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.props = props;
    }

    @Transactional
    public AuthResult loginWithGoogle(String idToken) {
        Payload p = googleIdTokenService.verify(idToken);

        String email = p.getEmail();
        String sub = p.getSubject();

        var user =
                userRepo
                        .findByAuthProviderAndProviderSubject("google", sub)
                        .orElseGet(() -> {
                            var u = new User();
                            u.setAuthProvider("google");
                            u.setProviderSubject(sub);
                            return u;
                        });

        user.setEmail(email);
        user.setDisplayName((String) p.get("name"));
        user.setAvatarUrl((String) p.get("picture"));
        user.touchUpdatedAt();
        final User savedUser = userRepo.save(user);

        profileRepo.findById(savedUser.getId()).orElseGet(() -> {
            var prof = new UserProfile();
            prof.setUser(savedUser);
            prof.setTimeZone(props.userDefaults().timeZone());
            prof.setDefaultCurrency(props.userDefaults().currency());
            return profileRepo.save(prof);
        });

        String accessToken = jwtService.issue(savedUser.getId(), savedUser.getEmail());
        return new AuthResult(accessToken, savedUser.getId(), savedUser.getEmail(), savedUser.getDisplayName(), savedUser.getAvatarUrl());

    }

    public record AuthResult(String accessToken, java.util.UUID userId, String email, String displayName, String avatarUrl) {}
}