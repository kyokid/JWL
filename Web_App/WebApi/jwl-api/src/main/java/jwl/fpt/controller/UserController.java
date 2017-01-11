package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.entity.TblUserEntity;
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
    IUserService userService;

    @RequestMapping(path = "/getAll", method = RequestMethod.GET)
    public List<TblUserEntity> getAllUser() {
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
    public RestServiceModel<TblUserEntity> login(@RequestBody TblUserEntity userBody) {
        String username = userBody.getUsername();
        String password = userBody.getPassword();
        RestServiceModel<TblUserEntity> result = new RestServiceModel<>();
        TblUserEntity user = userService.findByUsernameAndPassword(username, password);

        if (user != null) {
            result.setMessage("Login Successfully!");

            result.setSucceed(true);
            result.setData(user);
        } else {
            result.setMessage("Login Fail cmnr!");

        }
        return result;
    }

    @RequestMapping(path = "/search={q}", method = RequestMethod.GET)
    public RestServiceModel<List<TblUserEntity>> search(@PathVariable String q) {
        RestServiceModel<List<TblUserEntity>> result = new RestServiceModel<>();
        List<TblUserEntity> listUser = userService.findByUsernameLike(q);

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
