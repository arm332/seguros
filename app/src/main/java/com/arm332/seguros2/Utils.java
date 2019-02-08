package com.arm332.seguros2;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Utils {
    public static final String FILENAME = "data.csv";

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static List<List<String>> loadData(Context context) {
        List<List<String>> list = new ArrayList<>();
        FileInputStream input = null;

        try {
            input = context.openFileInput(FILENAME);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader buffer = new BufferedReader(reader);
            String line;

            while ((line = buffer.readLine()) != null) {
                list.add(Arrays.asList(line.split(",")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

//    public static String file2str(Context context) {
//        FileInputStream fis = null;
//        String result = null;
//
//        try {
//            fis = context.openFileInput(VALUE_RANGE);
//            int len = fis.available();
//            byte[] buf = new byte[len];
//            int n = fis.read(buf);
//            // fis.close();
//
//            result = new String(buf, "UTF-8");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return result;
//    }
//
//    public static void str2file(Context context, String text) {
//        FileOutputStream fos = null;
//
//        try {
//            fos = context.openFileOutput(VALUE_RANGE, Context.MODE_PRIVATE);
//            byte[] buf = text.getBytes("UTF-8");
//            fos.write(buf);
//            // fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public static String str2hex(String text) {
        String result = null;

        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] bytes = sha256.digest(text.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(bytes.length * 2);

            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xFF));
            }

            result = sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
