package com.arm332.seguros2;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SyncTask extends AsyncTask<String, Void, String> {
    private static final String APPLICATION_NAME = "Seguros";
    private WeakReference<SyncActivity> mActivity;
    private GoogleAccountCredential mCredential;

    public SyncTask(SyncActivity activity, GoogleAccountCredential credential) {
        mActivity = new WeakReference<>(activity);
        mCredential = credential;
    }

    @Override
    protected String doInBackground(String... params) {
        String spreadsheetId = params[0];

        // HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport(); // JKS not found
        // HttpTransport transport = AndroidHttp.newCompatibleTransport(); // Deprecated
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Sheets service = new Sheets.Builder(httpTransport, jsonFactory, mCredential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        try {
            Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();

//                String jsonString = spreadsheet.toString();
//                Log.d(TAG, "jsonString: " + jsonString);
//                jsonString: {"properties":{"autoRecalc":"ON_CHANGE","defaultFormat":{"backgroundColor":{"blue":1.0,"green":1.0,"red":1.0},"padding":{"left":3,"right":3},"textFormat":{"bold":false,"fontFamily":"Arial","fontSize":10,"foregroundColor":{},"italic":false,"strikethrough":false,"underline":false},"verticalAlignment":"BOTTOM","wrapStrategy":"OVERFLOW_CELL"},"locale":"pt_BR","timeZone":"Etc/GMT","title":"Seguros - editar somente esse aqui Adriana"},"sheets":[{"properties":{"gridProperties":{"columnCount":32,"rowCount":929},"index":0,"sheetId":2067206578,"sheetType":"GRID","title":"SEGUROS"}},{"properties":{"gridProperties":{"columnCount":26,"rowCount":1000},"index":1,"sheetId":1153567913,"sheetType":"GRID","title":"CADASTRO DOM BOSCO"}}],"spreadsheetId":"1un8-X5z1ddAt6kxN_FBb42eCLkmVw6FakfWR4a89U3g","spreadsheetUrl":"https://docs.google.com/spreadsheets/d/1un8-X5z1ddAt6kxN_FBb42eCLkmVw6FakfWR4a89U3g/edit"}

            SpreadsheetProperties spreadsheetProperties = spreadsheet.getProperties();
//                String spreadsheetTitle = spreadsheetProperties .getTitle();
//                Log.d(TAG, "spreadsheetTitle: " + spreadsheetTitle);

            List<Sheet> sheets = spreadsheet.getSheets();

            if (sheets.size() != 0) {
                SheetProperties sheetProperties = sheets.get(0).getProperties();
                String sheetTitle = sheetProperties.getTitle();
//                    Log.d(TAG, "sheetTitle: " + sheetTitle);
//                    Log.d(TAG, "columnCount: " + sheetProperties.getGridProperties().getColumnCount()); // 32
//                    Log.d(TAG, "frozenRowCount: " + sheetProperties.getGridProperties().getFrozenRowCount()); // null
//                    Log.d(TAG, "rowCount: " + sheetProperties.getGridProperties().getRowCount()); // 929

                ValueRange valueRange = service.spreadsheets().values()
                        .get(spreadsheetId, sheetTitle)
                        .execute();

//                String jsonString = valueRange.toString();
//                Log.d("FOO", "jsonString: " + jsonString);

//                List<List<Object>> values = valueRange.getValues();
//                Log.d(TAG, "values.size: " + values.size()); // 308

//                for (List<Object> row : values) {
//                    for (Object col : row) {
//                        String val = col.toString();
//                    }
//                }

//                // Convert List<Object> to List<String> <https://stackoverflow.com/a/4581429>
//                List<String> strings = new ArrayList<>(valueRange.size());
//
//                for (Object object : list) {
//                    strings.add(String.valueOf(object));
//                }

                return valueRange.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        SyncActivity activity = mActivity.get();

        if (activity != null && !activity.isFinishing()) {
            Utils.str2file(activity, result);
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        }
    }
}
