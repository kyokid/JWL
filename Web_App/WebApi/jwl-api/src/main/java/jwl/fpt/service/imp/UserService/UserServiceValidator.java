package jwl.fpt.service.imp.UserService;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.UserRoleEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.UserDto;
import jwl.fpt.repository.AccountRepository;
import jwl.fpt.repository.RoleRepository;

import static jwl.fpt.util.Constant.UserAttributes.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Entaard on 3/12/17.
 */
class UserServiceValidator {
    static RestServiceModel<UserDto> validateNewUser(UserDto userDto,
                                                     AccountRepository accountRepository,
                                                     RoleRepository roleRepository) {
        RestServiceModel<UserDto> result = new RestServiceModel<>();
        if (userDto == null) {
            result.setFailData(null, "Please input new user's information.");
            return result;
        }

        Map<String, Object> mapAttrs = new HashMap<>();
        mapAttrs.put(USERID, userDto.getUserId());
        mapAttrs.put(IMG_URL, userDto.getProfileImgUrl());
        mapAttrs.put(PASSWORD, userDto.getPassword());
        mapAttrs.put(CONFIRM_PASSWORD, userDto.getConfirmPassword());
        mapAttrs.put(USER_ROLE, userDto.getUserRoleId());
        mapAttrs.put(FULLNAME, userDto.getProfileFullname());
        mapAttrs.put(EMAIL, userDto.getProfileEmail());
        mapAttrs.put(ADDRESS, userDto.getProfileAddress());
        mapAttrs.put(BIRTHDATE, userDto.getProfileDateOfBirth());
        mapAttrs.put(PHONE, userDto.getProfilePhoneNo());
        mapAttrs.put(WORK, userDto.getProfilePlaceOfWork());

        for (Map.Entry entry : mapAttrs.entrySet()) {
            if (entry.getValue() == null) {
                result.setFailData(null, entry.getKey() + " is required!");
                return result;
            }
        }

        AccountEntity accountEntity = accountRepository.findByUserId((String) mapAttrs.get(USERID));
        if (accountEntity != null) {
            result.setFailData(null, USERID + " existed!");
            return result;
        }

        String password = (String) mapAttrs.get(PASSWORD);
        String confirmPassword = (String) mapAttrs.get(CONFIRM_PASSWORD);
        if (!password.equals(confirmPassword)) {
            result.setFailData(null, CONFIRM_PASSWORD + " must match " + PASSWORD + "!");
            return result;
        }

        UserRoleEntity userRoleEntity = roleRepository.findById((Integer) mapAttrs.get(USER_ROLE));
        if (userRoleEntity == null) {
            result.setFailData(null, USER_ROLE + " not exists!");
            return result;
        }

        // TODO: check for attr's length

        return null;
    }
}
