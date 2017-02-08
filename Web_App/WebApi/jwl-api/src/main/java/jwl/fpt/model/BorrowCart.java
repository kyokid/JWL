package jwl.fpt.model;

import lombok.Data;

import java.util.Set;

/**
 * Created by Entaard on 2/8/17.
 */
@Data
public class BorrowCart {
    private String userId;
    private String ibeaconId;
    private Set<String> rfids;
    // TODO: add expire date.
}
