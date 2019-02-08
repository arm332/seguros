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

final class Utils {
    private static final String FILENAME = "data.csv";
    static final int TITLE_COLUMN = 1;

    @SuppressWarnings("deprecation")
    static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    static String getItem(Context context, int position) {
        StringBuilder sb = new StringBuilder();
        FileInputStream input = null;

        try {
            input = context.openFileInput(FILENAME);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader buffer = new BufferedReader(reader);
            String line;

            if ((line = buffer.readLine()) != null) {
                String[] headers = line.split(",");
                int lineNumber = 1;

                while ((line = buffer.readLine()) != null) {
                    if (lineNumber == position) {
                        String[] values = line.split(",", -1);

                        for (int i = 0; i < headers.length; i++) {
                            sb.append("<p><b>");
                            sb.append(headers[i]);
                            sb.append("</b><br />");
                            sb.append(values[i]);
                            sb.append("</p>");
                        }

                        break;
                    }

                    lineNumber++;
                }
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

        return sb.toString();
    }

    static List<List<Object>> getList(Context context) {
        List<List<Object>> list = new ArrayList<>();
        FileInputStream input = null;

        try {
            input = context.openFileInput(FILENAME);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader buffer = new BufferedReader(reader);
            String line;

            // Skip first line (column headers)
            if (buffer.readLine() != null) {
                int blankLines = 0;
                int lineNumber = 1;

                while ((line = buffer.readLine()) != null) {
                    String[] values = line.split(",");

                    // Skip lines without value for second column (blank lines)
                    if (values.length > TITLE_COLUMN && values[TITLE_COLUMN].length() > 0) {
                        List<Object> item = new ArrayList<>();
                        item.add(lineNumber);
                        item.add(values[TITLE_COLUMN]);
                        list.add(item);
                        blankLines = 0;
                    }
                    else if (blankLines < 3) {
                        // Count consecutive blank lines
                        blankLines++;
                    }
                    else {
                        // Stop on 3 or more blank lines
                        break;
                    }

                    lineNumber++;
                }
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

    static String str2hex(String text) {
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
