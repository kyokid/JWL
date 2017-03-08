package jwl.fpt.service.imp;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BorrowerTicketEntity;
import jwl.fpt.entity.ProfileEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.AccountDetailDto;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.ProfileDto;
import jwl.fpt.model.dto.UserDto;
import jwl.fpt.repository.AccountRepository;
import jwl.fpt.repository.BorrowerTicketRepo;
import jwl.fpt.repository.UserRepository;
import jwl.fpt.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BorrowerTicketRepo borrowerTicketRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RestServiceModel<List<UserDto>> getAllUser() {
        RestServiceModel<List<UserDto>> result = new RestServiceModel<>();
        List<AccountEntity> accountEntities = accountRepository.findAllUsers();
        if (accountEntities == null || accountEntities.isEmpty()) {
            result.setSuccessData(
                    null,
                    "There is no user in the system...yet.Please add some.");
            return result;
        }

        List<UserDto> userDtos = new ArrayList<>();

        for (AccountEntity accountEntity :
                accountEntities) {
            UserDto dto = modelMapper.map(accountEntity, UserDto.class);
            userDtos.add(dto);
        }

        result.setSuccessData(userDtos, "Found " + userDtos.size() + " user(s).");

        return result;
    }

    @Override
    public AccountDto findByUsernameAndPassword(String username, String password) {
        AccountEntity entity = userRepository.findByUserIdAndPassword(username, password);

        if (entity == null) {
            return null;
        }

        AccountDto dto = modelMapper.map(entity, AccountDto.class);

        return dto;
    }

    @Override
    public AccountDto findByUsername(String userId) {
        AccountEntity entity = userRepository.findByUserId(userId);

        if (entity == null) {
            return null;
        }

        AccountDto dto = modelMapper.map(entity, AccountDto.class);

        return dto;
    }


    @Override
    public RestServiceModel<List<UserDto>> findByUserIdLike(String searchTerm) {
        RestServiceModel<List<UserDto>> result = new RestServiceModel<>();
        List<AccountEntity> accountEntities = accountRepository.findByUserIdLike('%' + searchTerm + '%');
        if (accountEntities == null || accountEntities.isEmpty()) {
            result.setSuccessData(
                    null,
                    "We could not find any accounts with userID like '" + searchTerm + "'");
            return result;
        }

        List<UserDto> userDtos = new ArrayList<>();

        for (AccountEntity accountEntity :
                accountEntities) {
            UserDto dto = modelMapper.map(accountEntity, UserDto.class);
            userDtos.add(dto);
        }

        result.setSuccessData(userDtos, "Found " + userDtos.size() + " user(s).");

        return result;
    }

    @Override
    public ProfileDto findProfileByUserId(String userId) {
        ProfileEntity profileEntity = userRepository.findProfileByUserId(userId);
        if (profileEntity == null) return null;
        ProfileDto profileDTO = modelMapper.map(profileEntity, ProfileDto.class);

        return profileDTO;
    }

    @Override
    public void updateGoogleToken(String googleToken, String userId) {
        accountRepository.updateGoogleToken(googleToken, userId);

    }

    @Override
    public AccountDetailDto getAccountDetail(String userId) {
        // TODO: Add necessary validations.
        AccountEntity accountEntity = accountRepository.findByUserId(userId);
        AccountDetailDto accountDetailDto = modelMapper.map(accountEntity, AccountDetailDto.class);
        return accountDetailDto;
    }

    @Override
    public Boolean getStatus(String userId) {
        if (userId == null) {
            return false;
        }
        return accountRepository.getStatus(userId);
    }

    private List<BorrowerTicketEntity> createNewBorrowerTicket(){
        return null;
    }
}
