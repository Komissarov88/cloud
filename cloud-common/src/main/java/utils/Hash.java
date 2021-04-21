package utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Used form hashing passwords and FileJobs on server
 */
public class Hash {

    public static String get(String msg) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("hash function fails");
        }
        digest.update(msg.getBytes());
        return DatatypeConverter.printHexBinary(digest.digest());
    }
}
