package jwl.fpt.service.imp;

import jwl.fpt.entity.TblUserEntity;
import jwl.fpt.repository.UserRepository;
import jwl.fpt.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepository;


    @Override
    public List<TblUserEntity> getAllUser() {

        return userRepository.findAll();
    }

    @Override
    public TblUserEntity findByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    @Override
    public List<TblUserEntity> findByUsernameLike(String q) {
        return userRepository.findByUsernameLike('%' +q + '%');
    }


}
