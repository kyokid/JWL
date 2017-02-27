package jwl.fpt.service;

import jwl.fpt.model.dto.AccountDetailDto;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.ProfileDto;
import jwl.fpt.model.dto.UserDto;

import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
public interface IUserService {
    List<UserDto> getAllUser();

    AccountDto findByUsernameAndPassword(String username, String password);

    AccountDto findByUsername(String userId);

    List<UserDto> findByUsernameLike(String q);

    ProfileDto findProfileByUserId(String userId);

    AccountDetailDto getAccountDetail(String userId);

    Boolean getStatus(String userId);

    void updateGoogleToken(String googleToken, String userId);
}
