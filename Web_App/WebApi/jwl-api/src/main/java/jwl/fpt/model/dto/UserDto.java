package jwl.fpt.model.dto;

import lombok.Data;

/**
 * Created by Entaard on 1/29/17.
 *
 * This is to use as an example for mapping entity - dto.
 */
@Data
public class UserDto {
    private String userId;
    private String username;
    private String password;
    private boolean gender;
    private String fullname;
}
