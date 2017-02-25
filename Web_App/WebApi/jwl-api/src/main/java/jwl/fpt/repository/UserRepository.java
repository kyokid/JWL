package jwl.fpt.repository;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.ProfileEntity;
import jwl.fpt.entity.TblUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
public interface UserRepository extends JpaRepository<TblUserEntity, Integer> {
    List<TblUserEntity> findAll();

    @Query("select a from AccountEntity a where a.userId = ?1 and a.password = ?2")
    AccountEntity findByUserIdAndPassword(String username, String password);


    List<TblUserEntity> findByUsernameLike(String q);


    @Query("select p from ProfileEntity p where p.userId = ?1")
    ProfileEntity findProfileByUserId(String userId);

    @Query("select u from AccountEntity u where u.userId = ?1")
    AccountEntity findByUserId(String username);
}
