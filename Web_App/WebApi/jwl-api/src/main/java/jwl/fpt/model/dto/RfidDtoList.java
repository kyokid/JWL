package jwl.fpt.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Entaard on 1/30/17.
 */
@Data
public class RfidDtoList implements Serializable {
    private String ibeaconId;
    private List<String> rfids;
}
