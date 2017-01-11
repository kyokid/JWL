package jwl.fpt.repository;

import jwl.fpt.entity.TblUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
public interface UserRepository extends JpaRepository<TblUserEntity, Integer> {
    List<TblUserEntity> findAll();

    TblUserEntity findByUsernameAndPassword(String username, String password);


    List<TblUserEntity> findByUsernameLike(String q);
}
