package jwl.fpt.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Entaard on 2/8/17.
 */
@Data
public class BorrowCart {
    private String userId;
    private String ibeaconId;
    private Set<String> rfids;
    private int usableBalance;

    public BorrowCart() {
        rfids = new HashSet<>();
        usableBalance = 0;
    }
    // TODO: add expire date.
}
