package com.webmne.salestracker.visitplan.dialogs;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.pierry.simpletoast.SimpleToast;
import com.google.gson.Gson;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.DeleteRecruitmentApi;
import com.webmne.salestracker.api.model.DeleteRecruitmentRequest;
import com.webmne.salestracker.api.model.DeleteRecruitmentResponse;
import com.webmne.salestracker.api.model.FetchRecruitmentData;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.DialogRecruitmentCalendarViewBinding;
import com.webmne.salestracker.visitplan.adapter.AgentStatusAdapter;
import com.webmne.salestracker.widget.TfEditText;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by priyasindkar on 06-10-2016.
 */

public class RecruitmentDialog extends MaterialDialog {
    private Context context;
    private MaterialDialog materialDialog;
    private DialogRecruitmentCalendarViewBinding dialogRecruitmentCalendarViewBinding;
    private AgentStatusAdapter adapter;
    private ArrayList<FetchRecruitmentData> recruitmentDataModelList = new ArrayList<>();
    private OnRecruitmentDataSubmitListener onRecruitmentDataSubmitListener;
    private LoadingIndicatorDialog loadingIndicatorDialog;
    private boolean isUpdate = false;

    public RecruitmentDialog(Builder builder, Context context, ArrayList<FetchRecruitmentData> recruitmentDataModelList) {
        super(builder);
        this.context = context;
        this.recruitmentDataModelList = recruitmentDataModelList;
        if (this.recruitmentDataModelList == null)
            this.recruitmentDataModelList = new ArrayList<>();
        init(builder);
    }

    private void init(final Builder builder) {
        dialogRecruitmentCalendarViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_recruitment_calendar_view, null, false);
        materialDialog = builder.customView(dialogRecruitmentCalendarViewBinding.getRoot(), true)
                .title("Recruitment")
                .cancelable(true)
                .build();

        dialogRecruitmentCalendarViewBinding.defaultAddRecruitmentItem.imgDelete.setVisibility(View.GONE);


        final ArrayList<String> agentStatusList = new ArrayList<>();
        agentStatusList.add("Blank");
        agentStatusList.add("L1");
        agentStatusList.add("L2");
        agentStatusList.add("P1");
        agentStatusList.add("P2");
        agentStatusList.add("P3");
        agentStatusList.add("R");

        adapter = new AgentStatusAdapter(context, R.layout.item_adapter, agentStatusList);
        dialogRecruitmentCalendarViewBinding.defaultAddRecruitmentItem.spinnerStatus.setAdapter(adapter);

        if (recruitmentDataModelList != null && !recruitmentDataModelList.isEmpty()) {
            isUpdate = true;
            dialogRecruitmentCalendarViewBinding.btnSubmit.setText("Update");
            dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.removeAllViews();
            for (final FetchRecruitmentData recruitmentData : recruitmentDataModelList) {
                final View view = addNewRecruitmentItem();
                ((TfEditText) view.findViewById(R.id.edtAgentName)).setText(recruitmentData.Existing);
                ((TfEditText) view.findViewById(R.id.edtRemarks)).setText(recruitmentData.TimeVisit);
                selectSpinnerItemByValue(((AppCompatSpinner) view.findViewById(R.id.spinnerStatus)), recruitmentData.ExistingVisit);
                ((ImageView) view.findViewById(R.id.imgDelete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRecruitmentItem(view, recruitmentData.RecId);
                    }
                });
            }
            dialogRecruitmentCalendarViewBinding.txtAddMore.setVisibility(View.GONE);
        } else {
            isUpdate = false;
        }


        dialogRecruitmentCalendarViewBinding.txtAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = addNewRecruitmentItem();
                ((ImageView) view.findViewById(R.id.imgDelete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.removeView(view);
                    }
                });
            }
        });

        dialogRecruitmentCalendarViewBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !isUpdate) recruitmentDataModelList = new ArrayList<FetchRecruitmentData>();
                for (int i = 0; i < dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.getChildCount(); i++) {
                    TfEditText edtAgentName = (TfEditText) dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.getChildAt(i).findViewById(R.id.edtAgentName);
                    TfEditText edtRemarks = (TfEditText) dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.getChildAt(i).findViewById(R.id.edtRemarks);
                    AppCompatSpinner agentStatusSpinner = (AppCompatSpinner) dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.getChildAt(i).findViewById(R.id.spinnerStatus);

                    FetchRecruitmentData fetchRecruitmentData = new FetchRecruitmentData();
                    fetchRecruitmentData.Existing = edtAgentName.getText().toString();
                    fetchRecruitmentData.ExistingVisit = agentStatusSpinner.getSelectedItem().toString();
                    fetchRecruitmentData.TimeVisit = edtRemarks.getText().toString();
                    if (isUpdate)
                        fetchRecruitmentData.RecId = recruitmentDataModelList.get(i).RecId;
                    recruitmentDataModelList.add(fetchRecruitmentData);
                }

                if (isUpdate)
                    onRecruitmentDataSubmitListener.onUpdate(recruitmentDataModelList);
                else
                    onRecruitmentDataSubmitListener.onSubmit(recruitmentDataModelList);
            }
        });

        dialogRecruitmentCalendarViewBinding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private View addNewRecruitmentItem() {
        final View view = materialDialog.getLayoutInflater().inflate(R.layout.add_recruitment_item, dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems, false);
        dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.addView(view);
        AppCompatSpinner agentStatusSpinner = (AppCompatSpinner) view.findViewById(R.id.spinnerStatus);
        agentStatusSpinner.setAdapter(adapter);
        return view;
    }

    public void show() {
        materialDialog.show();
    }

    public void dismiss() {
        materialDialog.dismiss();
    }

    public interface OnRecruitmentDataSubmitListener {
        void onSubmit(ArrayList<FetchRecruitmentData> recruitmentDataModelList);

        void onUpdate(ArrayList<FetchRecruitmentData> recruitmentDataModelList);
    }

    public void setOnRecruitmentDataSubmitListener(RecruitmentDialog.OnRecruitmentDataSubmitListener onRecruitmentDataSubmitListener) {
        this.onRecruitmentDataSubmitListener = onRecruitmentDataSubmitListener;
    }

    public void showProgress(String str) {
        if (loadingIndicatorDialog == null) {
            loadingIndicatorDialog = new LoadingIndicatorDialog(context, str, android.R.style.Theme_Translucent_NoTitleBar);
        }
        loadingIndicatorDialog.show();
    }

    public void dismissProgress() {
        if (loadingIndicatorDialog != null && loadingIndicatorDialog.isShowing())
            loadingIndicatorDialog.dismiss();
        dismiss();
    }

    public void selectSpinnerItemByValue(AppCompatSpinner spnr, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).toString().equals(value)) {
                spnr.setSelection(position);
                return;
            }
        }
    }

    private void deleteRecruitmentItem(final View view, String mapId) {
        showProgress(context.getString(R.string.deleting_data));

        DeleteRecruitmentApi deleteRecruitmentApi = new DeleteRecruitmentApi();
        deleteRecruitmentApi.deleteRecruitment(new DeleteRecruitmentRequest(mapId), new APIListener<DeleteRecruitmentResponse>() {
            @Override
            public void onResponse(retrofit2.Response<DeleteRecruitmentResponse> response) {
                if (loadingIndicatorDialog != null && loadingIndicatorDialog.isShowing())
                    loadingIndicatorDialog.dismiss();
                if (response.body() != null) {
                    Log.e("DELETE_RECRUITMENT_RESP", new Gson().toJson(response.body()));
                    dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.removeView(view);
                    if (dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.getChildCount() == 0) {
                        View view = addNewRecruitmentItem();
                        view.findViewById(R.id.imgDelete).setVisibility(View.GONE);
                        isUpdate = false;
                        dialogRecruitmentCalendarViewBinding.btnSubmit.setText("Submit");
                        dialogRecruitmentCalendarViewBinding.txtAddMore.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteRecruitmentResponse> call, Throwable t) {
                if (loadingIndicatorDialog != null && loadingIndicatorDialog.isShowing())
                    loadingIndicatorDialog.dismiss();
                Log.e("DELETE_RECRUITMENT_EXP", t.getMessage());
                SimpleToast.error(context, context.getString(R.string.delete_error), context.getString(R.string.fa_error));
            }
        });
    }
}
