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
    private int bookLimit;
    // When borrower scans fail and raises the alarm, scanFailed = true
    // This field helps:
    // * next borrower to be able to init checkout if the current borrower failed
    // * checkout the current failed borrower
    private boolean scanFailed;

    public BorrowCart() {
        rfids = new HashSet<>();
        usableBalance = 0;
        bookLimit = 0;
    }
    // TODO: add expire date.
}
