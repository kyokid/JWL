package jwl.fpt.model.dto;

import lombok.Data;

import java.sql.Date;

/**
 * Created by Entaard on 1/29/17.
 *
 * This is to use as an example for mapping entity - dto.
 */
@Data
public class UserDto {
    private String userId;
    private String password;
    private String confirmPassword;
    private boolean isInLibrary;
    private boolean isActivated;
    private Integer totalBalance;
    // role
    private Integer userRoleId;
    private String userRoleRole;
    // profile
    private String profileFullname;
    private String profileImgUrl;
    private String profileEmail;
    private String profileAddress;
    private Date profileDateOfBirth;
    private String profilePhoneNo;
    private String profilePlaceOfWork;
}
