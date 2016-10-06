package com.webmne.salestracker.visitplan.dialogs;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.webmne.salestracker.R;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.DialogMappingCalendarViewBinding;
import com.webmne.salestracker.visitplan.model.MappingDataModel;
import com.webmne.salestracker.widget.TfEditText;

import java.util.ArrayList;

/**
 * Created by priyasindkar on 06-10-2016.
 */

public class MappingDialog extends MaterialDialog {
    private Context context;
    private MaterialDialog materialDialog;
    private DialogMappingCalendarViewBinding dialogMappingCalendarViewBinding;
    private ArrayList<MappingDataModel> mappingDataModelList = new ArrayList<>();
    private OnMappingDataSubmitListener onMappingDataSubmitListener;
    private LoadingIndicatorDialog loadingIndicatorDialog;
    private int counter = 1;

    public MappingDialog(Builder builder, Context context) {
        super(builder);
        this.context = context;
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

        dialogMappingCalendarViewBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < dialogMappingCalendarViewBinding.linearAddMappingItems.getChildCount(); i++) {
                    TfEditText edtMapping = (TfEditText) dialogMappingCalendarViewBinding.linearAddMappingItems.getChildAt(i).findViewById(R.id.edtMapping);
                    TfEditText edtMappingVisit = (TfEditText) dialogMappingCalendarViewBinding.linearAddMappingItems.getChildAt(i).findViewById(R.id.edtMappingVisit);
                    mappingDataModelList.add(new MappingDataModel(edtMapping.getText().toString(), edtMappingVisit.getText().toString()));
                }
                onMappingDataSubmitListener.onSubmit(mappingDataModelList);
            }
        });

        dialogMappingCalendarViewBinding.txtAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = materialDialog.getLayoutInflater().inflate(R.layout.add_mapping_item, dialogMappingCalendarViewBinding.linearAddMappingItems, false);
                dialogMappingCalendarViewBinding.linearAddMappingItems.addView(view);
                counter++;
                view.setTag(counter);
                ((ImageView) view.findViewById(R.id.imgDelete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogMappingCalendarViewBinding.linearAddMappingItems.removeView(view);
                    }
                });
            }
        });
    }

    public void show() {
        materialDialog.show();
    }

    public void dismiss() {
        materialDialog.dismiss();
    }

    public interface OnMappingDataSubmitListener {
        void onSubmit(ArrayList<MappingDataModel> mappingDataModelList);
    }

    public void setOnMappingDataSubmitListener(OnMappingDataSubmitListener onMappingDataSubmitListener) {
        this.onMappingDataSubmitListener = onMappingDataSubmitListener;
    }

    class OnDeleteListener implements View.OnClickListener {
        private int value;

        OnDeleteListener(int value) {
            this.value = value;
        }

        @Override
        public void onClick(View v) {
            for (int i = 0; i < dialogMappingCalendarViewBinding.linearAddMappingItems.getChildCount(); i++) {
                if (value == Integer.parseInt(dialogMappingCalendarViewBinding.linearAddMappingItems.getChildAt(0).getTag().toString())) {
                    dialogMappingCalendarViewBinding.linearAddMappingItems.removeViewAt(i);
                    break;
                }
            }
        }
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
}
