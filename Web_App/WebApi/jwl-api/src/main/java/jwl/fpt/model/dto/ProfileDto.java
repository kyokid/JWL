package jwl.fpt.model.dto;

import jwl.fpt.entity.AccountEntity;
import lombok.Data;

import java.sql.Date;

/**
 * Created by thiendn on 07/02/2017.
 */
@Data
public class ProfileDto {
    private String userId;
    private String fullname;
    private String email;
    private String address;
    private Date dateOfBirth;
    private String phoneNo;
    private String placeOfWork;
}
