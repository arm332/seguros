package com.arm332.seguros2;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Utils {
    public static final String VALUE_RANGE = "value_range.json";

    public static List<List<String>> loadData(Context context) {
        List<List<String>> list = new ArrayList<>();
        FileInputStream fis = null;

        try {
            fis = context.openFileInput("data.csv");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                list.add(Arrays.asList(line.split(",")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public static String file2str(Context context) {
        FileInputStream fis = null;
        String result = null;

        try {
            fis = context.openFileInput(VALUE_RANGE);
            int len = fis.available();
            byte[] buf = new byte[len];
            int n = fis.read(buf);
            // fis.close();

            result = new String(buf, "UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static void str2file(Context context, String text) {
        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(VALUE_RANGE, Context.MODE_PRIVATE);
            byte[] buf = text.getBytes("UTF-8");
            fos.write(buf);
            // fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
