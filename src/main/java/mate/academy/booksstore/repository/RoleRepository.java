package mate.academy.booksstore.repository;

import mate.academy.booksstore.model.Role;
import mate.academy.booksstore.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByName(RoleName roleName);
}
