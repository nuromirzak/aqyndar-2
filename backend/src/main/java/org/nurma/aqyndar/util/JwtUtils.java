package org.nurma.aqyndar.util;

import io.jsonwebtoken.Claims;
import org.nurma.aqyndar.entity.Role;
import org.nurma.aqyndar.entity.RoleName;
import org.nurma.aqyndar.security.JwtAuthentication;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtUtils {

    private JwtUtils() {
    }

    public static JwtAuthentication generate(final Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setFirstName(claims.get("firstName", String.class));
        jwtInfoToken.setEmail(claims.getSubject());
        return jwtInfoToken;
    }

    private static Set<Role> getRoles(final Claims claims) {
        final List<String> roles = claims.get("roles", List.class);

        return roles.stream()
                .filter(role -> Arrays.stream(RoleName.values()).anyMatch(e -> e.name().equals(role)))
                .map(Role::new)
                .collect(Collectors.toSet());
    }

}
