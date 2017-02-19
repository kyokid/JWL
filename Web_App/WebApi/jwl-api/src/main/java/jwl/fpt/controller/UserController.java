package jwl.fpt.controller;

import jwl.fpt.entity.BorrowerTicketEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.ProfileDto;
import jwl.fpt.model.dto.UserDto;
import jwl.fpt.repository.AccountRepository;
import jwl.fpt.repository.BorrowerTicketRepo;
import jwl.fpt.service.IUserService;
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
    public List<UserDto> getAllUser() {
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

            result.setMessage("Login Successfully!");
            result.setSucceed(true);
            result.setData(user);
            result.setCode("200");
        } else {
            result.setMessage("Login Fail cmnr!");
        }
        return result;
    }



    @RequestMapping(path = "/users/search", method = RequestMethod.GET)
    public RestServiceModel<List<UserDto>> search(@RequestParam(value = "term") String searchTerm) {
        RestServiceModel<List<UserDto>> result = new RestServiceModel<>();
        List<UserDto> listUser = userService.findByUsernameLike(searchTerm);

        if (!listUser.isEmpty()) {
            result.setMessage("Search Successfully!");

            result.setSucceed(true);
            result.setData(listUser);
        } else {
            result.setMessage("Deo co gi");
        }
        return result;
    }

    @RequestMapping(path = "users/profile", method = RequestMethod.GET)
    public RestServiceModel<ProfileDto> checkin(@RequestParam(value = "term") String searchTerm,
                                                      @RequestParam(value = "createDate") Date createDate,
                                                @RequestParam(value = "ticketid") String ticketId) {
        RestServiceModel<ProfileDto> result = new RestServiceModel<>();
        ProfileDto profileDTO = userService.findProfileByUserId(searchTerm);
        System.out.println("request detection");
        if (profileDTO != null) {
            //Update Database
            accountRepository.setStateOfAccount(true, searchTerm);
            BorrowerTicketEntity ticket = new BorrowerTicketEntity();
            ticket.setQrId(ticketId);
            ticket.setAccount(searchTerm);
            ticket.setCreateDate(createDate);
            ticket.setScanDate(new Date(Calendar.getInstance().getTimeInMillis()));
            borrowerTicketRepo.save(ticket);
            //Set Response
            result.setMessage("Search Successfully!");
            result.setSucceed(true);
            result.setData(profileDTO);
        } else {
            result.setMessage("Search Fail!");
        }
        return result;
    }
}
