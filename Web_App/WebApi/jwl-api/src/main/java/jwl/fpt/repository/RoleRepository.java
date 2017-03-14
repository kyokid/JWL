package jwl.fpt.repository;

import jwl.fpt.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Entaard on 3/13/17.
 */
public interface RoleRepository extends JpaRepository<UserRoleEntity, Integer> {
    UserRoleEntity findById(Integer roleId);
}
