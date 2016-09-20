package com.webmne.salestracker.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.databinding.LayoutActionLogBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;

import java.io.File;

/**
 * Created by sagartahelyani on 23-08-2016.
 */
public class ActionLogDetails extends LinearLayout {

    private Context context;
    private LayoutInflater inflater;
    private LayoutActionLogBinding binding;
    private View parentView;
    private File file;

    public ActionLogDetails(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            this.context = context;
            init();
        }
    }

    public ActionLogDetails(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            this.context = context;
            init();
        }
    }

    public ActionLogDetails(Context context) {
        super(context);
        if (!isInEditMode()) {
            this.context = context;
            init();
        }
    }

    private void init() {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_action_log, this, true);
        parentView = binding.getRoot();

        createDir();
    }

    private void createDir() {
        //file = context.getDir("/SalesTracker/ActionLog", Context.MODE_PRIVATE);
        file = new File("/sdcard/SalesTracker/ActionLog/");
        //file = new File(Environment.getExternalStorageDirectory().toString() + "/SalesTracker/ActionLog");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void setActionLog(final ActionLogModel actionLog) {
        binding.txtAgentName.setText(actionLog.getAgentName());
        binding.txtDescription.setText(actionLog.getDescription());
        binding.txtDateRaised.setText(Functions.parseDate(actionLog.getCreatedDatetime(), "dd MMM yyyy, hh:mm a"));
        binding.txtStatus.setText(String.format("%s", Functions.getStatus(context, actionLog.getStatus())));
        binding.txtDepartment.setText(actionLog.getDepartmentName());
        binding.txtSla.setText(String.format("%s Days", actionLog.getSla()));
        binding.txtLastUpdate.setText(String.format("%s", Functions.parseDate(actionLog.getUpdatedDatetime(), "dd MMM yyyy, hh:mm a")));

        if (TextUtils.isEmpty(actionLog.getAttachment())) {
            binding.txtAttachment.setText(String.format("%s", context.getString(R.string.no_attachment)));
            binding.txtAttachment.setTextColor(ContextCompat.getColor(context, R.color.half_black));

        } else {
            binding.txtAttachment.setText(Html.fromHtml(String.format("<u>%s</u>", actionLog.getAttachment())));
            binding.txtAttachment.setTextColor(ContextCompat.getColor(context, R.color.blue));
        }

        binding.txtAttachment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(actionLog.getAttachment())) {
                    String url = AppConstants.ATTACHMENT_PREFIX + actionLog.getAttachmentPath() + "/" + actionLog.getAttachment();
               /* MimeTypeMap myMime = MimeTypeMap.getSingleton();
                String mimeType = myMime.getMimeTypeFromExtension(Functions.fileExt(url).substring(1));

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), mimeType);
                context.startActivity(intent);*/

                    downloadFile(actionLog, url);
                }
            }
        });
    }

    private void downloadFile(ActionLogModel actionLog, String url) {
        DownloadRequest request = new DownloadRequest.Builder()
                .setName(actionLog.getAttachment())
                .setUri(url)
                .setFolder(file)
                .build();

        DownloadManager.getInstance().download(request, url, new CallBack() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onConnecting() {

            }

            @Override
            public void onConnected(long total, boolean isRangeSupport) {

            }

            @Override
            public void onProgress(long finished, long total, int progress) {

            }

            @Override
            public void onCompleted() {
                Toast.makeText(context, "Complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadPaused() {

            }

            @Override
            public void onDownloadCanceled() {

            }

            @Override
            public void onFailed(DownloadException e) {
                Toast.makeText(context, "Fail: " + e.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
