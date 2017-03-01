package jwl.fpt.model;

/**
 * Created by HaVH on 1/9/17.
 */

import lombok.Data;
@Data
public class RestServiceModel<T> {
    private boolean succeed;
    private String textMessage;     // Msg shows for borrower.
    private String soundMessage;    // Msg reads by rfid reader.
    private String code;
    private T data;

    public static void checkResult(Object data, RestServiceModel responseObj, String[] messages) {
        if (data == null) {
            responseObj.setSucceed(false);
            responseObj.setTextMessage(messages[0]);
        } else {
            responseObj.setData(data);
            responseObj.setSucceed(true);
            responseObj.setTextMessage(messages[1]);
        }
    }

    public void setSuccessData(T data, String textMessage) {
        this.setData(data, textMessage, "");
        this.succeed = true;
        this.code = "200";
    }

    public void setSuccessData(T data, String textMessage, String soundMessage) {
        this.setData(data, textMessage, soundMessage);
        this.succeed = true;
        this.code = "200";
    }

    public void setFailData(T data, String textMessage) {
        this.setData(data, textMessage, "");
        this.succeed = false;
        this.code = "400";
    }

    public void setFailData(T data, String textMessage, String soundMessage) {
        this.setData(data, textMessage, soundMessage);
        this.succeed = false;
        this.code = "400";
    }

    private void setData(T data, String textMessage, String soundMessage) {
        this.data = data;
        this.textMessage = textMessage;
        this.soundMessage = soundMessage;
    }
}
