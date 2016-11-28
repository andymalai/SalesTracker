package com.webmne.salestracker.widget;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.pierry.simpletoast.SimpleToast;
import com.gun0912.tedpermission.PermissionListener;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.AddActionLogActivity;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.databinding.LayoutActionLogBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.DownloadHelper;
import com.webmne.salestracker.helper.Functions;

import java.io.File;
import java.util.ArrayList;

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
    }

    private void createDir(final ActionLogModel actionLog) {
        file = new File(Environment.getExternalStorageDirectory() + AppConstants.ACTION_LOG_DIRECTORY + "/" + actionLog.getId());

        if (file.exists()) {
            File file1 = new File(Environment.getExternalStorageDirectory() + AppConstants.ACTION_LOG_DIRECTORY + "/" + actionLog.getId() + "/" + actionLog.getAttachment());

            if (file1.exists()) {
                MimeTypeMap map = MimeTypeMap.getSingleton();
                String ext = MimeTypeMap.getFileExtensionFromUrl(file1.getName());
                String type = map.getMimeTypeFromExtension(ext);

                if (type == null)
                    type = "*/*";

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.fromFile(file1);
                    intent.setDataAndType(data, type);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to view", Toast.LENGTH_SHORT).show();
                }

            } else {

                Functions.showPrompt(context, context.getString(R.string.yes), context.getString(R.string.no), context.getString(R.string.ask_download), new Functions.onPromptListener() {
                    @Override
                    public void onClickYes(MaterialDialog dialog) {
                        String url = AppConstants.ACTION_LOG_PATH + "/" + actionLog.getAttachment();
                        Log.e("url", url);
                        String str_file_path = AppConstants.ACTION_LOG_DIRECTORY + actionLog.getId() + "/";

                        DownloadHelper downloadHelper = new DownloadHelper(context);
                        downloadHelper.startDownload(url, str_file_path, actionLog.getAttachment());
                    }

                    @Override
                    public void onClickNo(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                });

            }
        } else {
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

        if (actionLog.getApprovedDateAndBy().equals("0")) {
            binding.txtApprovedDate.setText("Yet not approved");
        } else {
            String[] approveDateSplit = actionLog.getApprovedDateAndBy().split(" By");
            String approveDate = Functions.parseDate(approveDateSplit[0], "dd MMM yyyy, hh:mm a");
            binding.txtApprovedDate.setText(String.format("%s By%s", approveDate, approveDateSplit[1]));
        }

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
                    Functions.setPermission(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            createDir(actionLog);
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                            SimpleToast.error(context, context.getString(R.string.permission_denied), context.getString(R.string.fa_error));
                        }
                    });
                }

            }
        });
    }
}
