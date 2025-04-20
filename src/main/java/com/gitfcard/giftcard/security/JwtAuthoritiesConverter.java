package com.gitfcard.giftcard.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthoritiesConverter implements Converter<, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> roles = (List<String>) jwt.getClaims().get("roles");  // Assuming the roles claim is a list of roles

        return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))  // Add ROLE_ prefix required by Spring Security
                    .collect(Collectors.toList());
    }
}

