package com.webmne.salestracker.communication.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.webmne.salestracker.R;
import com.webmne.salestracker.communication.Communication;
import com.webmne.salestracker.databinding.RowCommunicationListBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.DownloadHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public class CommunicationListAdapter extends RecyclerView.Adapter<CommunicationListAdapter.CommunicationViewHolder> {

    private Context context;
    private ArrayList<Communication> communicationList;
    private onClickListener onClickListener;
    private File file;

    public CommunicationListAdapter(Context context, ArrayList<Communication> communicationList, onClickListener onClickListener) {
        this.context = context;
        this.communicationList = communicationList;
        this.onClickListener = onClickListener;
    }

    @Override
    public CommunicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowCommunicationListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_communication_list, parent, false);
        return new CommunicationViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(CommunicationViewHolder holder, int position) {
        Communication communicationModel = communicationList.get(position);
        holder.setCommunicationDetail(communicationModel);
    }

    @Override
    public int getItemCount() {
        return communicationList.size();
    }

    public void setCommunicationData(ArrayList<Communication> communication) {
        this.communicationList.clear();
        this.communicationList = communication;
        notifyDataSetChanged();
    }

    class CommunicationViewHolder extends RecyclerView.ViewHolder {

        private RowCommunicationListBinding binding;

        CommunicationViewHolder(View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }

        void setCommunicationDetail(final Communication model) {

            binding.txtTitle.setText(model.getTitle());

            binding.imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createDir(model);
                }
            });
        }
    }

    private void createDir(Communication model) {
        file = new File(Environment.getExternalStorageDirectory() + AppConstants.COMMUNICATION_DIRECTORY);

        if (file.exists()) {
            File file1 = new File(Environment.getExternalStorageDirectory() + AppConstants.COMMUNICATION_DIRECTORY + "/" + model.getAttachment());

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
                Toast.makeText(context, "Downloading..", Toast.LENGTH_SHORT).show();
                String url = AppConstants.ATTACHMENT_PREFIX + AppConstants.COMMUNICATION_FILE_PATH + "/" + model.getAttachment();
                Log.e("url", url);
                String str_file_path = AppConstants.COMMUNICATION_DIRECTORY + "/";

                DownloadHelper downloadHelper = new DownloadHelper(context);
                downloadHelper.startDownload(url, str_file_path, model.getAttachment());
            }
        } else {
            file.mkdirs();
        }
    }

    public interface onClickListener {
        void onClick();
    }
}
