package jwl.fpt.controller;

import jwl.fpt.entity.BorrowerTicketEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.*;
import jwl.fpt.repository.AccountRepository;
import jwl.fpt.repository.BorrowerTicketRepo;
import jwl.fpt.service.IUserService;
import jwl.fpt.util.EncryptUtils;
import jwl.fpt.util.NotificationUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
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

    // TODO: temporary function for client side's authorization
    @RequestMapping(value = "/borrowers", method = RequestMethod.GET)
    public RestServiceModel<List<UserDto>> getAllBorrowers() {
        return userService.getAllBorrowers();
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public RestServiceModel<UserDto> createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @RequestMapping(path = "user/login", method = RequestMethod.POST)
    public RestServiceModel<AccountDto> login(@RequestBody AccountDto userLogin) {
        return userService.login(userLogin);
    }

    // TODO: temporary function for client side's authorization
    @RequestMapping(path = "staff/login", method = RequestMethod.POST)
    public RestServiceModel<AccountDto> loginByStaff(@RequestBody AccountDto userLogin) {
        return userService.loginByStaff(userLogin);
    }

    @RequestMapping(path = "/users/search", method = RequestMethod.GET)
    public RestServiceModel<List<UserDto>> search(@RequestParam(value = "term") String searchTerm) {
        return userService.findByUserIdLike(searchTerm);
    }

    // TODO: temporary function for client side's authorization
    @RequestMapping(path = "/borrowers/search", method = RequestMethod.GET)
    public RestServiceModel<List<UserDto>> searchBorrowers(@RequestParam(value = "term") String searchTerm) {
        return userService.findBorrowersByUserIdLike(searchTerm);
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

    @RequestMapping(path = "/users/{id}/requestKey", method = RequestMethod.GET)
    public RestServiceModel<String> requestKey(@PathVariable("id") String userId){
        RestServiceModel<String> responseObj = new RestServiceModel<>();
//        String keyResult = userService.requestKey(userId);

        //Generate key
        Date now = new Date(Calendar.getInstance().getTimeInMillis());
        System.out.println("Now: " + now);
        String beforeEncrypt = userId + now.toString();
        System.out.println("Before encrypt: " + beforeEncrypt);
        String finalKey = EncryptUtils.generateHash(beforeEncrypt);
        System.out.println("After encrypt: " + finalKey);
        //set json result:
        //date: Calendar.getInstance().getTimeInMillis() - type sql.date
        //key
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", finalKey);
        jsonObject.put("date", now);
        //set response
        responseObj.setData(jsonObject.toString());
        responseObj.setSucceed(true);
        System.out.println("json result: " + jsonObject);
        return responseObj;
    }

    @RequestMapping(path = "/users/{id}/checkin", method = RequestMethod.GET)
    public RestServiceModel<Boolean> checkin(@PathVariable("id") String userId,
            @RequestParam("key")String privateKey){
        RestServiceModel<Boolean> responseObj = new RestServiceModel<>();

        //Generate key from userid
        Date now = new Date(Calendar.getInstance().getTimeInMillis());
        String beforeEncrypt = userId + now.toString();
        String finalKey = EncryptUtils.generateHash(beforeEncrypt);
        System.out.println("Key send from client: " + privateKey);
        System.out.println("Key generate from server: " + finalKey);
        //so sanh key hien tai voi key client send.
        boolean result = finalKey.equals(privateKey);
        if (result) {
            String token = userService.findByUsername(userId).getGoogleToken();
            NotificationUtils.callNotification(userId, token);
            accountRepository.setStatus(true, userId);
        }
        responseObj.setSucceed(true);
        responseObj.setData(result);
        return responseObj;
    }

    @RequestMapping(path = "/getSystemDate", method = RequestMethod.GET)
    public RestServiceModel<String> getSystemDate() {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        RestServiceModel<String> result = new RestServiceModel<>();
        result.setSuccessData(currentDate.toString(), null);
        return result;
    }
}
