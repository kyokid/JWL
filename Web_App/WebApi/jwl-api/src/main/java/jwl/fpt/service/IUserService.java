package jwl.fpt.service;

import jwl.fpt.model.dto.UserDto;

import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
public interface IUserService {
    List<UserDto> getAllUser();

    UserDto findByUsernameAndPassword(String username, String password);

    List<UserDto> findByUsernameLike(String q);
}