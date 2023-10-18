package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.entity.Role;
import org.nurma.aqyndar.entity.RoleName;
import org.nurma.aqyndar.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleService {
    private static final RoleName DEFAULT_ROLE = RoleName.USER;
    private static final RoleName ADMIN_ROLE = RoleName.ADMIN;
    private final RoleRepository roleRepository;
    private final Map<RoleName, Role> roleMap = new HashMap<>();

    private Role getRoleByName(final RoleName roleName) {
        initializeRoles();
        return roleMap.get(roleName);
    }

    private void initializeRoles() {
        if (roleMap.isEmpty()) {
            List<Role> allRoles = roleRepository.findAll();
            for (Role role : allRoles) {
                roleMap.put(role.getName(), role);
            }
        }
    }

    public Role getDefaultRole() {
        return getRoleByName(DEFAULT_ROLE);
    }

    public Role getAdminRole() {
        return getRoleByName(ADMIN_ROLE);
    }
}
