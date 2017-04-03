package jwl.fpt.util;

/**
 * Created by Entaard on 1/29/17.
 */
public class Constant {
    public static String SESSION_BORROWER = "BORROWER";
    public static String SESSION_PENDING_COPIES = "PENDING_COPIES";

    public static int SESSION_INIT_TIMEOUT = 40;
    public static int SESSION_TRANSACT_TIMEOUT = 40;
    public static final int DAY_REMAIN_DEADLINE = 3;
    public static final int DAY_OF_DEADLINE = 0;

    //google token for notification
    public static final String APP_TOKEN = "AAAAaNSpPQs:APA91bGlEsmuobSo775eGREdsUDisU1Pio3u8T8YPHrSfVSjJXHMOh_F7n5418hfKISaPbVF3bVnwSABAQukv81g5DgU0uMMmcaC_XHyU43J7FasSdBEyltHND2rRAxwJEXygjiZEKMH";
    public static final String CLIENT_TOKEN = "d_0cUQJavvU:APA91bGi83tS-ptRYoaRXdhhEmOdFp0ZQuSRmQq5XUyfs8OXkSgz9_LBe3Rv1e0zqHlaa4TbFufy2x9vRJgRXCPZ80eeZ5PxZljfkdp0SwChsgaCKs9aQDlfLsthspd054-RkthS7LQB";

    public static final class UserAttributes {
        public static final String USERID = "userId";
        public static final String IMG_URL = "img Url";
        public static final String PASSWORD = "password";
        public static final String CONFIRM_PASSWORD = "confirm password";
        public static final String USER_ROLE = "userRole";
        public static final String FULLNAME = "fullname";
        public static final String EMAIL = "email";
        public static final String ADDRESS = "address";
        public static final String BIRTHDATE = "date of birth";
        public static final String PHONE = "phone No";
        public static final String WORK = "place of work";
    }

    public static final class SoundMessages {
        public static final String OK = "beep.mp3";
        public static final String ALREADY = "beepbeep.mp3";
        public static final String ERROR = "alarm.mp3";
    }

    public static final class Role {
        public static final String ADMIN = "admin";
        public static final String LIBRARIAN = "librarian";
        public static final String BORROWER = "borrower";
    }
}
