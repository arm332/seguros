package com.arm332.seguros2;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

public class SyncTask extends AsyncTask<String, Void, String> {
    private static final String APPLICATION_NAME = "Seguros";
    private WeakReference<SyncActivity> mActivity;
    private GoogleAccountCredential mCredential;
    private String mNotFound;

    SyncTask(SyncActivity activity, GoogleAccountCredential credential) {
        mActivity = new WeakReference<>(activity);
        mCredential = credential;

        mNotFound = activity.getString(R.string.spreadsheet_not_found);
    }

    @Override
    protected String doInBackground(String... params) {
        String result = mNotFound;

        // HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport(); // JKS not found
        // HttpTransport transport = AndroidHttp.newCompatibleTransport(); // Deprecated
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        try {
            Drive driveService = new Drive.Builder(httpTransport, jsonFactory, mCredential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            FileList fileList = driveService.files().list()
                    //.setQ("mimeType='application/vnd.google-apps.spreadsheet'")
                    .setQ("name='" + params[0] + "'")
                    .setSpaces("drive")
                    .execute();

//            System.out.println(fileList.toString());

            List<File> files = fileList.getFiles();

            if (files.size() != 0) {
                File file = files.get(0);

                SyncActivity activity = mActivity.get();

                if (activity != null) {
                    Context context = activity.getApplicationContext();

                    // OutputStream outputStream = new ByteArrayOutputStream();
                    OutputStream outputStream = context.openFileOutput("data.csv",
                            Context.MODE_PRIVATE);
                    driveService.files().export(file.getId(), "text/csv")
                            .executeMediaAndDownloadTo(outputStream);

                    result = null;
                }
            }
        }
        catch (Exception e) {
            // e.printStackTrace();
            result = e.getMessage();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        SyncActivity activity = mActivity.get();

        if (activity != null) {
            activity.onSyncTaskComplete(result);
        }
    }
}
