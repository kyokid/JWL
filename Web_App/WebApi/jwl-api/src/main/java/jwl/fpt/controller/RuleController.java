package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BookTypeDto;
import jwl.fpt.model.dto.RuleDto;
import jwl.fpt.service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by thiendn on 08/04/2017.
 */
@RestController
public class RuleController {
    @Autowired
    IBookService bookService;
    @Value("${library.fine.cost}")
    private Integer fineCost;
    @RequestMapping(path = "/rules", method = RequestMethod.GET)
    RestServiceModel<RuleDto> getRule(){
        RestServiceModel<RuleDto> result = new RestServiceModel<>();
        List<BookTypeDto> bookTypeDtos = bookService.getBookType();
        RuleDto ruleDto = new RuleDto();
        ruleDto.setListTypeBook(bookTypeDtos);
        ruleDto.setFine_cost(fineCost);
        result.setSucceed(true);
        result.setTextMessage("Found " + bookTypeDtos.size() + " type(s) of book.");
        result.setData(ruleDto);
        return result;
    }
}
