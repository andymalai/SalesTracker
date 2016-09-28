package com.webmne.salestracker.helper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.github.pierry.simpletoast.SimpleToast;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by vatsaldesai on 20-09-2016.
 */

public class DownloadHelper {

    private DownloadManager downloadManager;
    private long downloadReference;
    private Context context;

    public DownloadHelper(Context context) {
        this.context = context;
    }

    public void startDownload(String url, String download_path, String file_name) {
        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(downloadReceiver, filter);

        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        Uri Download_Uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(false);
        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle(file_name);
        //Set a description of this download, to be displayed in notifications (if enabled)
//        request.setDescription("Android Data download using DownloadManager.");
        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(download_path, file_name);

        Log.e("tag", "download_path:-" + download_path + "  file_name:-" + file_name);

        //Set visible and shows in the notifications while in progress and after completion.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //Enqueue a new download and same the referenceId
        downloadReference = downloadManager.enqueue(request);

//        showCountries.setText("Getting data from Server, Please WAIT...");
    }

    public void stopDownload() {
        downloadManager.remove(downloadReference);
//        showCountries.setText("Download of the file cancelled...");

        context.unregisterReceiver(downloadReceiver);
    }

    public void checkStatus(Cursor cursor) {

        //column for status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        boolean isSuccess = false;
        String reasonText = "";

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusText = "Failed to download";
                isSuccess = false;
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "Download complete";
                isSuccess = true;
                reasonText = "Filename:\n" + filename;
                break;
        }

        if (isSuccess) {
            SimpleToast.ok(context, statusText);
        } else {
            SimpleToast.error(context, statusText);
        }

        context.unregisterReceiver(downloadReceiver);
    }


    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadReference);
            Cursor c = downloadManager.query(query);

            if (c.moveToFirst()) {
                checkStatus(c);
            }

        }
    };


}
