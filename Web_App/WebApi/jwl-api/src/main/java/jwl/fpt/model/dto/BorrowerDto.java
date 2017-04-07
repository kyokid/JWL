package jwl.fpt.model.dto;

import lombok.Data;

/**
 * Created by Entaard on 1/30/17.
 */
@Data
public class BorrowerDto {
    private String iBeaconId;
    private String userId;

    public BorrowerDto(String iBeaconId, String userId) {
        this.iBeaconId = iBeaconId;
        this.userId = userId;
    }

    public BorrowerDto() {
        iBeaconId = "";
        userId = "";
    }
}
