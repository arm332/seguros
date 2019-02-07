package com.arm332.seguros2;

import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.lang.ref.WeakReference;
import java.util.List;

public class SyncTask extends AsyncTask<Void, Void, String> {
    private static final String APP_NAME = "Seguros";
    private WeakReference<SyncActivity> mActivity;
    private GoogleAccountCredential mCredential;

    public SyncTask(SyncActivity activity, GoogleAccountCredential credential) {
        this.mActivity = new WeakReference<>(activity);
        this.mCredential = credential;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        // HttpTransport transport = AndroidHttp.newCompatibleTransport(); // Deprecated
        // HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport(); // JKS not found
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Drive driveService = new Drive.Builder(httpTransport, jsonFactory, mCredential)
                .setApplicationName(APP_NAME)
                .build();

        try {
            FileList result = driveService.files().list()
                    .setQ("mimeType='application/vnd.google-apps.spreadsheet'")
                    .setSpaces("drive")
                    .execute();

            List<File> files = result.getFiles();

            for (File file : files) {
                System.out.printf("Found file: %s (%s)\n", file.getName(), file.getId());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        SyncActivity activity = mActivity.get();

        if (activity != null) {
            // TODO
        }
    }
}
