package jwl.fpt.model;

/**
 * Created by HaVH on 1/9/17.
 */

import lombok.Data;
@Data
public class RestServiceModel<T> {
    private boolean Succeed;
    private String Message;
    private String Code;
    private T Data;

}
