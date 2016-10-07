package com.webmne.salestracker.visitplan.dialogs;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.pierry.simpletoast.SimpleToast;
import com.google.gson.Gson;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.DeleteMappingApi;
import com.webmne.salestracker.api.model.DeleteMappingRequest;
import com.webmne.salestracker.api.model.DeleteMappingResponse;
import com.webmne.salestracker.api.model.FetchMappingData;
import com.webmne.salestracker.api.model.FetchRecruitmentData;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.DialogMappingCalendarViewBinding;
import com.webmne.salestracker.widget.TfEditText;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by priyasindkar on 06-10-2016.
 */

public class MappingDialog extends MaterialDialog {
    private Context context;
    private MaterialDialog materialDialog;
    private DialogMappingCalendarViewBinding dialogMappingCalendarViewBinding;
    private ArrayList<FetchMappingData> mappingDataModelList = new ArrayList<>();
    private OnMappingDataSubmitListener onMappingDataSubmitListener;
    private LoadingIndicatorDialog loadingIndicatorDialog;
    private boolean isUpdate = false;

    public MappingDialog(Builder builder, Context context) {
        super(builder);
        this.context = context;
        init(builder);
    }

    public MappingDialog(Builder builder, Context context, ArrayList<FetchMappingData> mappingDataModelList) {
        super(builder);
        this.context = context;
        this.mappingDataModelList = mappingDataModelList;
        if (this.mappingDataModelList == null) this.mappingDataModelList = new ArrayList<>();
        init(builder);
    }

    private void init(final Builder builder) {
        dialogMappingCalendarViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_mapping_calendar_view, null, false);
        materialDialog = builder.customView(dialogMappingCalendarViewBinding.getRoot(), true)
                .title("Mapping")
                .cancelable(true)
                .build();

        dialogMappingCalendarViewBinding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dialogMappingCalendarViewBinding.linearDefaultAppMappingItem.imgDelete.setVisibility(View.GONE);

        if (mappingDataModelList != null && !mappingDataModelList.isEmpty()) {
            isUpdate = true;
            dialogMappingCalendarViewBinding.btnSubmit.setText("Update");
            dialogMappingCalendarViewBinding.linearAddMappingItems.removeAllViews();
            for (final FetchMappingData mappingData : mappingDataModelList) {
                final View view = addNewMappingItem();
                ((TfEditText) view.findViewById(R.id.edtMapping)).setText(mappingData.Mapping);
                ((TfEditText) view.findViewById(R.id.edtMappingVisit)).setText(mappingData.MappingVisit);

                ((ImageView) view.findViewById(R.id.imgDelete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMappingItem(view, mappingData.MappingId);
                    }
                });
            }
            dialogMappingCalendarViewBinding.txtAddMore.setVisibility(View.GONE);
        } else {
            isUpdate = false;
        }

        dialogMappingCalendarViewBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !isUpdate) mappingDataModelList = new ArrayList<FetchMappingData>();
                for (int i = 0; i < dialogMappingCalendarViewBinding.linearAddMappingItems.getChildCount(); i++) {
                    TfEditText edtMapping = (TfEditText) (dialogMappingCalendarViewBinding.linearAddMappingItems.getChildAt(i)).findViewById(R.id.edtMapping);
                    TfEditText edtMappingVisit = (TfEditText) (dialogMappingCalendarViewBinding.linearAddMappingItems.getChildAt(i)).findViewById(R.id.edtMappingVisit);
                    FetchMappingData mappingData = new FetchMappingData();
                    mappingData.Mapping = edtMapping.getText().toString();
                    mappingData.MappingVisit = edtMappingVisit.getText().toString();
                    if (isUpdate)
                        mappingData.MappingId = mappingDataModelList.get(i).MappingId;
                    mappingDataModelList.add(mappingData);
                }
                if (isUpdate)
                    onMappingDataSubmitListener.onUpdate(mappingDataModelList);
                else
                    onMappingDataSubmitListener.onSubmit(mappingDataModelList);
            }
        });

        dialogMappingCalendarViewBinding.txtAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = addNewMappingItem();
                ((ImageView) view.findViewById(R.id.imgDelete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogMappingCalendarViewBinding.linearAddMappingItems.removeView(view);
                    }
                });
            }
        });
    }

    private View addNewMappingItem() {
        final View view = materialDialog.getLayoutInflater().inflate(R.layout.add_mapping_item, dialogMappingCalendarViewBinding.linearAddMappingItems, false);
        dialogMappingCalendarViewBinding.linearAddMappingItems.addView(view);
        return view;
    }


    public void show() {
        materialDialog.show();
    }

    public void dismiss() {
        materialDialog.dismiss();
    }

    public interface OnMappingDataSubmitListener {
        void onSubmit(ArrayList<FetchMappingData> mappingDataModelList);

        void onUpdate(ArrayList<FetchMappingData> mappingDataModelList);
    }

    public void setOnMappingDataSubmitListener(OnMappingDataSubmitListener onMappingDataSubmitListener) {
        this.onMappingDataSubmitListener = onMappingDataSubmitListener;
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

    private void deleteMappingItem(final View view, String mapId) {
        showProgress(context.getString(R.string.deleting_data));

        DeleteMappingApi deleteMappingApi = new DeleteMappingApi();
        deleteMappingApi.deleteMapping(new DeleteMappingRequest(mapId), new APIListener<DeleteMappingResponse>() {
            @Override
            public void onResponse(retrofit2.Response<DeleteMappingResponse> response) {
                if (loadingIndicatorDialog != null && loadingIndicatorDialog.isShowing())
                    loadingIndicatorDialog.dismiss();
                if (response.body() != null) {
                    Log.e("DELETE_MAPPING_RESP", new Gson().toJson(response.body()));
                    dialogMappingCalendarViewBinding.linearAddMappingItems.removeView(view);
                    if (dialogMappingCalendarViewBinding.linearAddMappingItems.getChildCount() == 0) {
                        View view = addNewMappingItem();
                        view.findViewById(R.id.imgDelete).setVisibility(View.GONE);
                        isUpdate = false;
                        dialogMappingCalendarViewBinding.btnSubmit.setText("Submit");
                        dialogMappingCalendarViewBinding.txtAddMore.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteMappingResponse> call, Throwable t) {
                if (loadingIndicatorDialog != null && loadingIndicatorDialog.isShowing())
                    loadingIndicatorDialog.dismiss();
                Log.e("DELETE_MAPPING_EXP", t.getMessage());
                SimpleToast.error(context, context.getString(R.string.delete_error), context.getString(R.string.fa_error));
            }
        });
    }
}
