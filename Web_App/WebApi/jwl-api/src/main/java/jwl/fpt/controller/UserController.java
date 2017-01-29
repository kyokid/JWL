package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.UserDto;
import jwl.fpt.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
@RestController
public class UserController {
    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<UserDto> getAllUser() {
        return userService.getAllUser();
    }

//    @RequestMapping(path = "/login", method = RequestMethod.POST)
//    public RestServiceModel<TblUserEntity> login(@RequestParam String username, @RequestParam String password) {
//        RestServiceModel<TblUserEntity> result = new RestServiceModel<>();
//        TblUserEntity user = userService.findByUsernameAndPassword(username, password);
//
//        if (user != null) {
//            result.setMessage("Login Successfully!");
//
//            result.setSucceed(true);
//            result.setData(user);
//        } else {
//            result.setMessage("Login Fail cmnr!");
//
//        }
//        return result;
//    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public RestServiceModel<UserDto> login(@RequestBody UserDto userBody) {
        String username = userBody.getUsername();
        String password = userBody.getPassword();
        RestServiceModel<UserDto> result = new RestServiceModel<>();
        UserDto user = userService.findByUsernameAndPassword(username, password);

        if (user != null) {
            result.setMessage("Login Successfully!");

            result.setSucceed(true);
            result.setData(user);
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
}
