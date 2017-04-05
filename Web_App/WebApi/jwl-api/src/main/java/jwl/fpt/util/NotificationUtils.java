package jwl.fpt.util;


import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BookEntity;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.service.IUserService;
import jwl.fpt.service.IWishBookService;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thiendn on 22/02/2017.
 */
public class NotificationUtils {
    private static String url = "https://fcm.googleapis.com/fcm/send";

    public static void callNotification(String userId, String googleToken){


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "key=" + Constant.APP_TOKEN);
        JSONObject body = new JSONObject();
        body.put("body", true);
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

    public static void pushNotificationDeadline(String googleToken, String messageBody) throws UnsupportedEncodingException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "key=" + Constant.APP_TOKEN);
        JSONObject body = new JSONObject();

        body.put("body", URLEncoder.encode(messageBody, "UTF-8"));
        body.put("title", "Remaining day");
        JSONObject entity = new JSONObject();
        entity.put("to", googleToken);
        entity.put("data", body);
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

    public static void pushNotificationWishList(String googleToken, String bookTitle) throws UnsupportedEncodingException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "key=" + Constant.APP_TOKEN);
        JSONObject body = new JSONObject();
        String messageBody = "Tựa sách " + bookTitle + " bạn theo dõi hiện đã sẵn có tại thư viện.";
        body.put("body", URLEncoder.encode(messageBody, "UTF-8"));
        body.put("title", "Remaining day");
        JSONObject entity = new JSONObject();
        entity.put("to", googleToken);
        entity.put("data", body);
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

    public static void callNotificationFail(String userId, String googleToken){
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "key=" + Constant.APP_TOKEN);
        JSONObject body = new JSONObject();
        body.put("body", false);
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

    public static void pushNotiRefreshBorrowedBook(String googleToken){
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "key=" + Constant.APP_TOKEN);
        JSONObject body = new JSONObject();
        body.put("title", "Refresh");
        JSONObject entity = new JSONObject();
        entity.put("to", googleToken);
        entity.put("data", body);
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
