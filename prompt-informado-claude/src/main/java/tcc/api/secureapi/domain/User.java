package tcc.api.secureapi.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users",
       uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
public class User {

    /** UUID aleatório: não sequencial, dificulta enumeração de recursos (defesa em profundidade contra BOLA). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 254)
    private String email;

    /** Somente o hash BCrypt. Nunca a senha em texto puro. */
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Set<Role> roles = EnumSet.noneOf(Role.class);

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected User() { /* JPA */ }

    public User(String name, String email, String passwordHash, Set<Role> roles) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = EnumSet.copyOf(roles);
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Set<Role> getRoles() { return Set.copyOf(roles); }
    public boolean isEnabled() { return enabled; }
    public Instant getCreatedAt() { return createdAt; }

    // toString propositalmente omitido: evita vazar o hash em logs.
}
