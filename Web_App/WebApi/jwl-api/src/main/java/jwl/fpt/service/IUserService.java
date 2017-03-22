package jwl.fpt.service;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.AccountDetailDto;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.ProfileDto;
import jwl.fpt.model.dto.UserDto;

import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
public interface IUserService {
    RestServiceModel<List<UserDto>> getAllUser();

    RestServiceModel<UserDto> createUser(UserDto newUserDto);

    RestServiceModel<List<UserDto>> findByUserIdLike(String term);

    RestServiceModel<AccountDto> login(AccountDto accountDto);

    AccountDto findByUsername(String userId);

    ProfileDto findProfileByUserId(String userId);

    AccountDetailDto getAccountDetail(String userId);

    Boolean getStatus(String userId);

    // TODO: Thiendn - update return type
    void updateGoogleToken(String googleToken, String userId);

//    String requestKey(String userId);

//    Boolean checkin(String key, String userId);
}
