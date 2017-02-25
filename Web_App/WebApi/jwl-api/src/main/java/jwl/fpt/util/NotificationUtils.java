package jwl.fpt.util;


import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.service.IUserService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thiendn on 22/02/2017.
 */
public class NotificationUtils {
    @Autowired
    IUserService userService;
    public static void callNotification(String userId, String googleToken){

        String url = "https://fcm.googleapis.com/fcm/send";

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "key=" + Constant.APP_TOKEN);
        JSONObject body = new JSONObject();
        body.put("body", "Welcome" + userId) ;
        JSONObject entity = new JSONObject();
        entity.put("to", googleToken);
        entity.put("notification", body);
        System.out.println(entity.toString());
        try {
            post.setEntity(new StringEntity(entity.toString()));

        } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
        }

        try {
            HttpResponse response = client.execute(post);
            System.out.println(response.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
