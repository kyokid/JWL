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

    RestServiceModel<List<UserDto>> getAllBorrowers();

    RestServiceModel<UserDto> createUser(UserDto newUserDto);

    RestServiceModel<List<UserDto>> findByUserIdLike(String term);

    RestServiceModel<List<UserDto>> findBorrowersByUserIdLike(String term);

    RestServiceModel<AccountDto> login(AccountDto accountDto);

    RestServiceModel<AccountDto> loginByStaff(AccountDto accountDto);

    AccountDto findByUsername(String userId);

    ProfileDto findProfileByUserId(String userId);

    AccountDetailDto getAccountDetail(String userId);

    Boolean getStatus(String userId);

    Boolean getActivate(String userId);

    // TODO: Thiendn - update return type
    void updateGoogleToken(String googleToken, String userId);

    RestServiceModel<AccountDetailDto> updateTotalBalance(AccountDto accountDto);

//    String requestKey(String userId);

//    Boolean checkin(String key, String userId);

    RestServiceModel<Boolean> setIsActivate(String userId, boolean isActivate);
}
