package org.starfishrespect.myconsumption.android.util;

import android.util.Base64;

import org.springframework.http.HttpHeaders;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * Created by thibaud on 19.05.15.
 */
public class CryptoUtils {
    private static final String TAG = makeLogTag(CryptoUtils.class);

    /**
     * Return a Base64 encoded String of the hash(input) (SHA 256)
     * @param input a String to encode
     * @return a Base64 encoded String of the hash(input) (SHA 256)
     */
    public static String sha256(String input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGE(TAG, e.toString());
        }
        byte[] hash = new byte[0];
        if (digest != null) {
            try {
                hash = digest.digest(input.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                LOGE(TAG, e.toString());
            }

        }
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    /**
     * Create header for basic authentication with username and password
     * @return HttpHeaders with basic authentication
     */
    public static HttpHeaders createHeaders(final String username, final String password ){
        HttpHeaders headers =  new HttpHeaders(){
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")), Base64.NO_WRAP);
                String authHeader = "Basic " + new String( encodedAuth );
                //String authHeader = "Basic " + auth;
                set("Authorization", authHeader);
            }
        };
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");

        return headers;
    }


//  The code below implement the hash + salt but it is not supported in the current version of the app


//    /**
//     * Compute the user fields:
//     *  - password: a Base64 encoded String of hash(hash(password) + salt)
//     *  - salt: the salt used with the hash
//     * @param password the password to hash as a String (plain text)
//     * @return Return  a Base64 encoded String of hash(hash(password) + salt)
//     */
//    public static String newHashAndSalt(String password) {
//        byte[] hashPwd = sha256(password);
//        byte[] salt = getRandomSalt();
//
//        // Concatenate the salt and the hash
//        byte[] hashPwdSalt = new byte[hashPwd.length + salt.length];
//        System.arraycopy(hashPwd, 0, hashPwdSalt, 0, hashPwd.length);
//        System.arraycopy(salt, 0, hashPwdSalt, hashPwd.length, salt.length);
//
//        String hashPwdSaltString = Base64.encodeToString(hashPwdSalt, Base64.NO_WRAP);
//
//        // Hash everything
//        String hash = Base64.encodeToString(sha256(hashPwdSaltString), Base64.NO_WRAP);
//
//        EventBus.getDefault().post(new UserCreatedEvent(salt, hash));
//        return hash;
//    }
//
//    private static byte[] sha256(String input) {
//        MessageDigest digest = null;
//        try {
//            digest = MessageDigest.getInstance("SHA-256");
//        } catch (NoSuchAlgorithmException e) {
//            LOGE(TAG, e.toString());
//        }
//        byte[] hash = new byte[0];
//        if (digest != null) {
//            try {
//                hash = digest.digest(input.getBytes("UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                LOGE(TAG, e.toString());
//            }
//
//        }
//        return hash;
//    }
//
//    private static byte[] getRandomSalt() {
//        // Generate a random salt
//        final Random r = new SecureRandom();
//        byte[] salt = new byte[32];
//        r.nextBytes(salt);
//
//        return salt;
//    }
}
