package com.client.manager.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakRealmRoleConverterTest {

    @Mock
    private Jwt jwt;

    private KeycloakRealmRoleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new KeycloakRealmRoleConverter();
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenRealmAccessIsMissing() {
        when(jwt.getClaim("realm_access")).thenReturn(null);

        Collection<GrantedAuthority> result = converter.convert(jwt);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jwt).getClaim("realm_access");
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenRolesAreMissing() {
        when(jwt.getClaim("realm_access"))
                .thenReturn(Map.of("other_claim", "value"));

        Collection<GrantedAuthority> result = converter.convert(jwt);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jwt).getClaim("realm_access");
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenRolesValueIsNotCollection() {
        when(jwt.getClaim("realm_access"))
                .thenReturn(Map.of("roles", "ADMIN"));

        Collection<GrantedAuthority> result = converter.convert(jwt);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jwt).getClaim("realm_access");
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenRolesCollectionIsEmpty() {
        when(jwt.getClaim("realm_access"))
                .thenReturn(Map.of("roles", List.of()));

        Collection<GrantedAuthority> result = converter.convert(jwt);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jwt).getClaim("realm_access");
    }

    @Test
    void shouldConvertRealmRolesToGrantedAuthorities() {
        when(jwt.getClaim("realm_access"))
                .thenReturn(Map.of("roles", List.of("USER", "ADMIN")));

        Collection<GrantedAuthority> result = converter.convert(jwt);

        assertEquals(
                List.of("ROLE_USER", "ROLE_ADMIN"),
                result.stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );

        verify(jwt).getClaim("realm_access");
    }

    @Test
    void shouldConvertNonStringRolesUsingToString() {
        when(jwt.getClaim("realm_access"))
                .thenReturn(Map.of("roles", List.of("USER", 123)));

        Collection<GrantedAuthority> result = converter.convert(jwt);

        assertEquals(
                List.of("ROLE_USER", "ROLE_123"),
                result.stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );

        verify(jwt).getClaim("realm_access");
    }
}