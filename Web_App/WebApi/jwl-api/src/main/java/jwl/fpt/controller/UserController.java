package jwl.fpt.controller;

import jwl.fpt.entity.BorrowerTicketEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.AccountDetailDto;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.ProfileDto;
import jwl.fpt.model.dto.UserDto;
import jwl.fpt.repository.AccountRepository;
import jwl.fpt.repository.BorrowerTicketRepo;
import jwl.fpt.service.IUserService;
import jwl.fpt.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
@RestController
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BorrowerTicketRepo borrowerTicketRepo;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public RestServiceModel<List<UserDto>> getAllUser() {
        return userService.getAllUser();
    }

    @RequestMapping(path = "users/login", method = RequestMethod.POST)
    public RestServiceModel<AccountDto> login(@RequestBody AccountDto userBody) {
//        System.out.println("request detection");
        RestServiceModel<AccountDto> result = new RestServiceModel<>();
        String username = userBody.getUserId();
        String password = userBody.getPassword();
        AccountDto user = userService.findByUsernameAndPassword(username, password);
        if (user != null) {

            result.setTextMessage("Login Successfully!");
            result.setSucceed(true);
            result.setData(user);
            result.setCode("200");
        } else {
            result.setTextMessage("Login Fail cmnr!");
        }
        return result;
    }

    @RequestMapping(path = "/users/search", method = RequestMethod.GET)
    public RestServiceModel<List<UserDto>> search(@RequestParam(value = "term") String searchTerm) {
        return userService.findByUserIdLike(searchTerm);
    }

    @RequestMapping(path = "users/profile", method = RequestMethod.GET)
    public RestServiceModel<ProfileDto> checkin(@RequestParam(value = "term") String searchTerm,
                                                      @RequestParam(value = "createDate") Date createDate,
                                                @RequestParam(value = "ticketid") String ticketId) {
        RestServiceModel<ProfileDto> result = new RestServiceModel<>();
        ProfileDto profileDTO = userService.findProfileByUserId(searchTerm);
        System.out.println("request detection");
        if (profileDTO != null) {
            String token = userService.findByUsername(searchTerm).getGoogleToken();
            NotificationUtils.callNotification(profileDTO.getUserId(), token);
            //Update Database
            accountRepository.setStatus(true, searchTerm);
            BorrowerTicketEntity ticket = new BorrowerTicketEntity();
            ticket.setQrId(ticketId);
            ticket.setAccount(searchTerm);
            ticket.setCreateDate(createDate);
            ticket.setScanDate(new Date(Calendar.getInstance().getTimeInMillis()));
            borrowerTicketRepo.save(ticket);
            //Set Response
            result.setTextMessage("Search Successfully!");
            result.setSucceed(true);
            result.setData(profileDTO);
        } else {
            result.setTextMessage("Search Fail!");
        }
        return result;
    }

    @RequestMapping(path = "users/{id}", method = RequestMethod.GET)
    public RestServiceModel<AccountDetailDto> getDetail(@PathVariable("id") String userId) {
        // TODO: Add necessary validations.
        RestServiceModel<AccountDetailDto> result = new RestServiceModel<>();
        AccountDetailDto accountDetailDto = userService.getAccountDetail(userId);
        result.setData(accountDetailDto);
        result.setSucceed(true);
        return result;
    }

    @RequestMapping(path = "users/{id}/status", method = RequestMethod.GET)
    public RestServiceModel<Boolean> getStatus(@PathVariable("id") String userId) {
        // TODO: Add necessary validations.
        RestServiceModel<Boolean> responseObj = new RestServiceModel<>();
        Boolean result = userService.getStatus(userId);
        responseObj.setData(result);
        responseObj.setSucceed(true);
        return responseObj;
    }

    @RequestMapping(path = "/users/updateToken", method = RequestMethod.GET)
    public void updateToken(@RequestParam(value = "userId") String userId,
                            @RequestParam(value = "googleToken") String googleToken) {
        userService.updateGoogleToken(googleToken, userId);
        RestServiceModel<String> result = new RestServiceModel<>();
        result.setSucceed(true);
    }

}
