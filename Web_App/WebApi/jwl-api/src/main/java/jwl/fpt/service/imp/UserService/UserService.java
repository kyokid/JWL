package jwl.fpt.service.imp.UserService;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BorrowerTicketEntity;
import jwl.fpt.entity.ProfileEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.*;
import jwl.fpt.repository.AccountRepository;
import jwl.fpt.repository.RoleRepository;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.service.IUserService;
import jwl.fpt.util.Helper;
import static jwl.fpt.util.Constant.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IBookBorrowService bookBorrowService;

    @Override
    public RestServiceModel<List<UserDto>> getAllUser() {
        RestServiceModel<List<UserDto>> result = new RestServiceModel<>();
        List<AccountEntity> accountEntities = accountRepository.findAllUsers();
        if (accountEntities == null || accountEntities.isEmpty()) {
            result.setSuccessData(
                    null,
                    "There is no user in the system...yet. Please add some.");
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
    public RestServiceModel<List<UserDto>> getAllBorrowers() {
        RestServiceModel<List<UserDto>> result = new RestServiceModel<>();
        List<AccountEntity> accountEntities = accountRepository.findAllBorrowers();
        if (accountEntities == null || accountEntities.isEmpty()) {
            result.setSuccessData(
                    null,
                    "There is no borrower in the system...yet. Please add some.");
            return result;
        }

        List<UserDto> userDtos = new ArrayList<>();

        for (AccountEntity accountEntity :
                accountEntities) {
            UserDto dto = modelMapper.map(accountEntity, UserDto.class);
            userDtos.add(dto);
        }

        result.setSuccessData(userDtos, "Found " + userDtos.size() + " borrower(s).");

        return result;
    }

    @Override
    @Transactional
    public RestServiceModel<UserDto> createUser(UserDto userDto) {
        RestServiceModel<UserDto> result = UserServiceValidator
                .validateNewUser(userDto, accountRepository, roleRepository);
        if (result != null) {
            return result;
        }
        result = new RestServiceModel<>();

        AccountEntity accountEntity = modelMapper.map(userDto, AccountEntity.class);
        accountEntity.setActivated(true);
        accountEntity.setInLibrary(false);
        accountEntity.getProfile().setUserId(userDto.getUserId());
        accountEntity = accountRepository.save(accountEntity);
        if (accountEntity == null) {
            result.setFailData(null, "DB error!!!");
            return result;
        }

        userDto = modelMapper.map(accountEntity, UserDto.class);
        result.setSuccessData(userDto, "saved!");
        return result;
    }

    @Override
    public RestServiceModel<AccountDto> login(AccountDto accountDto) {
        RestServiceModel<AccountDto> result = UserServiceValidator.checkNullAccountDto(accountDto);
        if (result != null) {
            return result;
        }

        result = new RestServiceModel<>();
        String userId = accountDto.getUserId();
        String password = accountDto.getPassword();
        AccountEntity entity = accountRepository.login(userId, password);
        if (entity == null) {
            result.setFailData(null, "Sai userID hoặc mật khẩu.");
            return result;
        }

        AccountDto resultData = modelMapper.map(entity, AccountDto.class);
        resultData.setPassword("");
        result.setSuccessData(resultData, "Login successfully!");

        return result;
    }

    @Override
    public RestServiceModel<AccountDto> loginByStaff(AccountDto accountDto) {
        RestServiceModel<AccountDto> result = UserServiceValidator.checkNullAccountDto(accountDto);
        if (result != null) {
            return result;
        }

        result = new RestServiceModel<>();
        String userId = accountDto.getUserId();
        String password = accountDto.getPassword();
        AccountEntity entity = accountRepository.loginByStaff(userId, password);
        if (entity == null) {
            result.setFailData(null, "Invalid userID or password.");
            return result;
        }

        AccountDto resultData = modelMapper.map(entity, AccountDto.class);
        resultData.setPassword("");
        result.setSuccessData(resultData, "Login successfully!");

        return result;
    }

    @Override
    public AccountDto findByUsername(String userId) {
        AccountEntity entity = accountRepository.findByUserId(userId);

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

        result.setSuccessData(userDtos, "Found " + userDtos.size() + " account(s).");

        return result;
    }

    @Override
    public RestServiceModel<List<UserDto>> findBorrowersByUserIdLike(String searchTerm) {
        RestServiceModel<List<UserDto>> result = new RestServiceModel<>();
        List<AccountEntity> accountEntities = accountRepository
                .findBorrowersByUserIdLike('%' + searchTerm + '%');
        if (accountEntities == null || accountEntities.isEmpty()) {
            result.setSuccessData(
                    null,
                    "We could not find any borrowers with userID like '" + searchTerm + "'");
            return result;
        }

        List<UserDto> userDtos = new ArrayList<>();

        for (AccountEntity accountEntity :
                accountEntities) {
            UserDto dto = modelMapper.map(accountEntity, UserDto.class);
            userDtos.add(dto);
        }

        result.setSuccessData(userDtos, "Found " + userDtos.size() + " borrower(s).");

        return result;
    }

    @Override
    public ProfileDto findProfileByUserId(String userId) {
        ProfileEntity profileEntity = accountRepository.findProfileByUserId(userId);
        if (profileEntity == null) return null;
        ProfileDto profileDTO = modelMapper.map(profileEntity, ProfileDto.class);

        return profileDTO;
    }

    @Override
    public void updateGoogleToken(String googleToken, String userId) {
        accountRepository.updateGoogleToken(googleToken, userId);
    }

    @Override
    public RestServiceModel<AccountDetailDto> updateTotalBalance(AccountDto accountDto) {
        // TODO: Add necessary validations.
        RestServiceModel<AccountDetailDto> result = new RestServiceModel<>();
        String userId = accountDto.getUserId();
        AccountEntity accountEntity = accountRepository.findByUserId(userId);
        int currentTotalBalance = accountEntity.getTotalBalance();
        int newTotalBalance = accountDto.getTotalBalance();
        accountEntity.setTotalBalance(newTotalBalance);
        accountRepository.saveAndFlush(accountEntity);
        // update usableBalance in user's current borrow cart
        bookBorrowService.updateUsableBalanceInBorrowCartOf(userId, newTotalBalance - currentTotalBalance);

        AccountDetailDto accountDetailDto = new AccountDetailDto();
        accountDetailDto.setTotalBalance(accountEntity.getTotalBalance());
        accountDetailDto.setUsableBalance(bookBorrowService.calculateUsableBalanceFromDb(userId));
        result.setSuccessData(accountDetailDto, "Total Balance updated.");
        return result;
    }

    @Override
    public RestServiceModel<Boolean> setIsActivate(String userId, boolean isActivate) {
        RestServiceModel<Boolean> result = new RestServiceModel<>();
        int resultSQL = accountRepository.setActivate(userId, isActivate);
        if (resultSQL > 0){
            String textMessage = isActivate ? "Borrower " + userId + " has been activated!"
                    : "Borrower " + userId + " has been deactivated!" ;
            result.setTextMessage(textMessage);
            //current state of isActivate after call api.
            result.setData(isActivate);
            result.setSucceed(true);
        }else {
            result.setData(false);
            result.setSucceed(false);
            result.setTextMessage("Unexpected error! Can not update account " + userId );
        }
        return result;
    }

    @Override
    public AccountDetailDto getAccountDetail(String userId) {
        // TODO: Add necessary validations.
        AccountEntity accountEntity = accountRepository.findByUserId(userId);
        AccountDetailDto accountDetailDto = modelMapper.map(accountEntity, AccountDetailDto.class);
        accountDetailDto.setUsableBalance(bookBorrowService.calculateUsableBalanceFromDb(userId));
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = accountDetailDto.getBorrowedBookCopies();
        BorrowedBookCopyDto.setBookStatusForListDtos(borrowedBookCopyDtos);
        return accountDetailDto;
    }

    @Override
    public Boolean getStatus(String userId) {
        if (userId == null) {
            return false;
        }
        return accountRepository.getStatus(userId);
    }

    @Override
    public Boolean getActivate(String userId) {
        if (userId == null){
            return false;
        }
        return accountRepository.getActivate(userId);
    }

    public void autoCheckOutUser() {
        accountRepository.updateInLibrary();
    }
}
