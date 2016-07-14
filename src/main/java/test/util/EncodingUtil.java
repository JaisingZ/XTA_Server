package test.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by Jaising on 2016/7/14.
 */
public class EncodingUtil {

    public static String strISO2UTF(String str_utf) {
        String str_iso = null;
        try {
            str_iso = new String(str_utf.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str_iso;
    }
}
