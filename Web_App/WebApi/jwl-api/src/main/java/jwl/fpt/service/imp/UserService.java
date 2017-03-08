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
import jwl.fpt.util.EncryptUtils;
import jwl.fpt.util.NotificationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
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

    Calendar calendar;

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
    public String requestKey(String userId) {
        // TODO: Thiendn
        Date now = new Date(Calendar.getInstance().getTimeInMillis());
        System.out.println("Now: " + now);
        String beforeEncrypt = userId + now.toString();
        System.out.println("Before encrypt: " + beforeEncrypt);
        String finalKey = EncryptUtils.generateHash(beforeEncrypt);
        System.out.println("After encrypt: " + finalKey);
        accountRepository.updateCheckinKey(finalKey, userId);
        return finalKey;
    }

    @Override
    public Boolean checkin(String key, String userId) {
        // TODO: Thiendn: thao luan lai van de luu vao ticket.
        String keyInDB = accountRepository.getCheckinKey(userId);
        boolean result = keyInDB.equals(key);
        if (result){
            String token = findByUsername(userId).getGoogleToken();
            NotificationUtils.callNotification(userId, token);
            //Update Database
//            accountRepository.setStatus(true, userId);
//            BorrowerTicketEntity ticket = new BorrowerTicketEntity();
//            ticket.setQrId(ticketId);
//            ticket.setAccount(searchTerm);
//            ticket.setCreateDate(createDate);
//            ticket.setScanDate(new Date(Calendar.getInstance().getTimeInMillis()));
//            borrowerTicketRepo.save(ticket);
        }
        return result;
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
