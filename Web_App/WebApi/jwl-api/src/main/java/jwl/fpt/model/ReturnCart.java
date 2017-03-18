package jwl.fpt.model;

import lombok.Data;

import java.util.Set;

/**
 * Created by Entaard on 3/17/17.
 */
@Data
public class ReturnCart {
    private String userId;
    private String librarianId;
    private Set<String> rfids;

    public ReturnCart(String userId, String librarianId, Set<String> rfids) {
        this.userId = userId;
        this.librarianId = librarianId;
        this.rfids = rfids;
    }
}
