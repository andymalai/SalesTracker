package com.webmne.salestracker.visitplan.dialogs;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
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
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.DialogMappingCalendarViewBinding;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
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

    public MappingDialog(Context context, ArrayList<FetchMappingData> mappingDataModelList) {
        super(new MaterialDialog.Builder(context));
        this.context = context;
        if (mappingDataModelList == null) {
            this.mappingDataModelList = new ArrayList<>();
        } else {
            this.mappingDataModelList = mappingDataModelList;
        }
        init();
    }

    private void init() {
        Builder builder = new Builder(context);
        dialogMappingCalendarViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_mapping_calendar_view, null, false);
        materialDialog = builder.customView(dialogMappingCalendarViewBinding.getRoot(), true)
                .typeface(Functions.getBoldFont(context), Functions.getRegularFont(context))
                .title("Mapping")
                .cancelable(true)
                .build();
        materialDialog.show();

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
                boolean canSubmit = false;

                ArrayList<FetchMappingData> newMappingData = new ArrayList<FetchMappingData>();

                for (int i = 0; i < dialogMappingCalendarViewBinding.linearAddMappingItems.getChildCount(); i++) {

                    TfEditText edtMapping = (TfEditText) (dialogMappingCalendarViewBinding.linearAddMappingItems.getChildAt(i)).findViewById(R.id.edtMapping);
                    TfEditText edtMappingVisit = (TfEditText) (dialogMappingCalendarViewBinding.linearAddMappingItems.getChildAt(i)).findViewById(R.id.edtMappingVisit);

                    if (TextUtils.isEmpty(Functions.toStr(edtMapping)) || TextUtils.isEmpty(Functions.toStr(edtMappingVisit))) {
                        SimpleToast.error(context, context.getString(R.string.enter_all));
                        canSubmit = false;

                    } else {
                        if (isUpdate) {
                            FetchMappingData mappingData = new FetchMappingData();
                            mappingData.Mapping = Functions.toStr(edtMapping);
                            mappingData.MappingVisit = Functions.toStr(edtMappingVisit);
                            mappingData.MappingId = mappingDataModelList.get(i).MappingId;
                            newMappingData.add(mappingData);

                        } else {
                            FetchMappingData mappingData = new FetchMappingData();
                            mappingData.Mapping = Functions.toStr(edtMapping);
                            mappingData.MappingVisit = Functions.toStr(edtMappingVisit);
                            mappingData.MappingId = null;
                            newMappingData.add(mappingData);
                        }
                        canSubmit = true;
                    }
                }

                if (canSubmit) {

                    Log.e("dialog_map_req", MyApplication.getGson().toJson(newMappingData));

                    if (onMappingDataSubmitListener != null)
                        onMappingDataSubmitListener.onSubmit(newMappingData);
                }
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
