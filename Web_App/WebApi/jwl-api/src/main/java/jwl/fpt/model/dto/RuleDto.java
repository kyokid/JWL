package jwl.fpt.model.dto;

import lombok.Data;

import java.sql.Date;
import java.util.List;

/**
 * Created by thiendn on 08/04/2017.
 */
@Data
public class RuleDto {
    private List<BookTypeDto> listTypeBook;
    private Integer fine_cost;
}
