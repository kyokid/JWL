package jwl.fpt.service;

import jwl.fpt.entity.TblUserEntity;

import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
public interface IUserService {
    List<TblUserEntity> getAllUser();

    TblUserEntity findByUsernameAndPassword(String username, String password);

    List<TblUserEntity> findByUsernameLike(String q);

}