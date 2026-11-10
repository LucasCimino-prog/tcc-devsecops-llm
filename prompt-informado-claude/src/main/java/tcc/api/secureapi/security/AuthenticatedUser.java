package tcc.api.secureapi.security;

import tcc.api.secureapi.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Principal do Spring Security. O id aqui (vindo do token assinado) é a fonte da verdade para autorização. */
public class AuthenticatedUser implements UserDetails {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final boolean enabled;
    private final Set<SimpleGrantedAuthority> authorities;

    private AuthenticatedUser(UUID id, String email, String passwordHash, boolean enabled,
                              Set<SimpleGrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static AuthenticatedUser from(User u) {
        Set<SimpleGrantedAuthority> auths = u.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toUnmodifiableSet());
        return new AuthenticatedUser(u.getId(), u.getEmail(), u.getPasswordHash(), u.isEnabled(), auths);
    }

    public UUID getId() { return id; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return email; }
    @Override public boolean isEnabled() { return enabled; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}
