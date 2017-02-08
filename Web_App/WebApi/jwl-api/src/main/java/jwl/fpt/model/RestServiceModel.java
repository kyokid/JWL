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

    public static void checkResult(Object result, RestServiceModel responseObj, String[] messages) {
        if (result == null) {
            responseObj.setSucceed(false);
            responseObj.setMessage(messages[0]);
        } else {
            responseObj.setData(result);
            responseObj.setSucceed(true);
            responseObj.setMessage(messages[1]);
        }
    }
}
