package org.s3m.userservice.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtTokenUtil {

    public Jwt getCurrentJwt() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        return null;
    }

    public String getCurrentUserId() {
        Jwt jwt = getCurrentJwt();
        if (jwt != null) {
            String sub = jwt.getClaimAsString("sub");
            return sub != null ? sub : jwt.getClaimAsString("user_id");
        }
        return null;
    }

    public String getCurrentUsername() {
        Jwt jwt = getCurrentJwt();
        if (jwt != null) {
            return jwt.getClaimAsString("preferred_username");
        }
        return null;
    }

    public String getCurrentUserEmail() {
        Jwt jwt = getCurrentJwt();
        if (jwt != null) {
            return jwt.getClaimAsString("email");
        }
        return null;
    }

    public UUID getCurrentUserUuid() {
        String userId = getCurrentUserId();
        if (userId != null) {
            try {
                return UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
