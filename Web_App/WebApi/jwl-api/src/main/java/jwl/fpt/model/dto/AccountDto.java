package jwl.fpt.model.dto;

import lombok.Data;

/**
 * Created by Entaard on 1/29/17.
 */
@Data
public class AccountDto {
    private String userId;
    private String password;
    private String userRoleRole;
    private String profileImgUrl;
    private boolean isInLibrary;
    private boolean isActivated;
    private String googleToken;
    private String checkinKey;
}
