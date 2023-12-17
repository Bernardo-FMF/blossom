package org.blossom.auth.factory.impl;

import com.google.common.base.Suppliers;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.entity.Role;
import org.blossom.auth.entity.User;
import org.blossom.auth.enums.RoleEnum;
import org.blossom.auth.factory.interfac.IEntityFactory;
import org.blossom.auth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Component
@Log4j2
public class UserFactory implements IEntityFactory<User, RegisterDto> {
    @Autowired
    private RoleRepository roleRepository;

    private static Supplier<Role> memoizedRole;

    @PostConstruct
    void memoizeDefaultRole() {
        if (memoizedRole.get() != null) {
            return;
        }

        Optional<Role> role = roleRepository.findByName(RoleEnum.USER);

        if (role.isPresent()) {
            memoizedRole = Suppliers.memoize(role::get)::get;
        }
    }

    @Override
    public User buildEntity(RegisterDto data) {
        memoizeDefaultRole();

        if (Objects.isNull(memoizedRole.get())) {
            log.info("Memoized Role is null -> could not build User");
            return null;
        }

        return User.builder()
                .email(data.getEmail())
                .fullName(data.getFullName())
                .username(data.getUsername())
                .password(data.getPassword())
                .roles(Set.of(memoizedRole.get()))
                .active(true)
                .build();
    }
}
