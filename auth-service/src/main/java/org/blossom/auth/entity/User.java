package org.blossom.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.blossom.model.KafkaEntity;
import org.blossom.model.KafkaUserResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Blossom_User")
public class User implements UserDetails, KafkaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Blossom_User_Role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = { @JoinColumn(name = "role_id") })
    private Set<Role> roles;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "verified")
    private boolean verified;

    @Column(name = "mfa_enabled")
    private boolean mfaEnabled;

    @Column(name = "secret")
    private String secret;

    @Override
    public Set<SimpleGrantedAuthority> getAuthorities() {
        return Optional.ofNullable(roles)
                .map(rList -> rList.stream()
                        .map(r -> new SimpleGrantedAuthority(r.getName().toString()))
                        .collect(toSet()))
                .orElseGet(HashSet::new);
    }

    @Override
    public boolean isAccountNonExpired() {
        return verified;
    }

    @Override
    public boolean isAccountNonLocked() {
        return verified;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return verified;
    }

    @Override
    public boolean isEnabled() {
        return verified;
    }

    @Override
    public KafkaUserResource mapToResource() {
        return KafkaUserResource.builder()
                .id(id)
                .fullName(fullName)
                .username(username)
                .imageUrl(imageUrl)
                .build();
    }
}