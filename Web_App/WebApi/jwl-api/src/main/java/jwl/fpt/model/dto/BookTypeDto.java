package jwl.fpt.model.dto;

import lombok.Data;

/**
 * Created by Entaard on 3/19/17.
 */
@Data
public class BookTypeDto {
    private Integer id;
    private String name;
    private Integer borrowLimitDays;
    private Integer daysPerExtend;
    private Integer extendTimesLimit;
}
