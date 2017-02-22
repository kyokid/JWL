package jwl.fpt.service.imp;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.ProfileEntity;
import jwl.fpt.entity.TblUserEntity;
import jwl.fpt.model.dto.AccountDetailDto;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.ProfileDto;
import jwl.fpt.model.dto.UserDto;
import jwl.fpt.repository.AccountRepository;
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
    private ModelMapper modelMapper;

    @Override
    public List<UserDto> getAllUser() {
        List<AccountEntity> users = accountRepository.findAll();
        List<UserDto> results = new ArrayList<>();

        for (AccountEntity user:
                users) {
            UserDto dto = modelMapper.map(user, UserDto.class);
            results.add(dto);
        }

        return results;
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
    public List<UserDto> findByUsernameLike(String q) {
        List<TblUserEntity> entities = userRepository.findByUsernameLike('%' +q + '%');
        List<UserDto> results = new ArrayList<>();

        for (TblUserEntity entity :
                entities) {
            UserDto dto = modelMapper.map(entity, UserDto.class);
            results.add(dto);
        }

        return results;
    }

    @Override
    public ProfileDto findProfileByUserId(String userId) {
        ProfileEntity profileEntity = userRepository.findProfileByUserId(userId);
        if (profileEntity == null) return null;
        ProfileDto profileDTO = modelMapper.map(profileEntity, ProfileDto.class);

        return profileDTO;
    }

    @Override
    public AccountDetailDto getAccountDetail(String userId) {
        // TODO: Add necessary validations.
        AccountEntity accountEntity = accountRepository.findByUserId(userId);
        AccountDetailDto accountDetailDto = modelMapper.map(accountEntity, AccountDetailDto.class);
        return accountDetailDto;
    }
}
