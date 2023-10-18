package org.nurma.aqyndar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "role")
@NoArgsConstructor
@Getter
@Setter
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name",
            unique = true,
            length = 255,
            updatable = false,
            insertable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    public Role(final String name) {
        this.name = RoleName.valueOf(name);
    }

    @Override
    public String getAuthority() {
        return name.name();
    }
}
