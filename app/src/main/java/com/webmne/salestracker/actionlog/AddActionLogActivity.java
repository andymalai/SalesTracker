package com.webmne.salestracker.actionlog;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.gun0912.tedpermission.PermissionListener;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.adapter.AgentAdapter;
import com.webmne.salestracker.actionlog.adapter.DepartmentAdapter;
import com.webmne.salestracker.actionlog.adapter.InChargeAdapter;
import com.webmne.salestracker.actionlog.model.Department;
import com.webmne.salestracker.actionlog.model.DepartmentListResponse;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.AgentListApi;
import com.webmne.salestracker.api.model.AgentListResponse;
import com.webmne.salestracker.api.model.AgentModel;
import com.webmne.salestracker.api.model.InCharge;
import com.webmne.salestracker.api.model.InChargeListResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityAddActionLogBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class AddActionLogActivity extends AppCompatActivity {

    ActivityAddActionLogBinding binding;

    private ArrayList<AgentModel> agentList;
    private int agentWhich = 0;

    private ArrayList<Department> deptList;
    private int deptWhich = 0;

    private int priorityWhich = 0;

    private ArrayList<InCharge> inChargeList;
    private int inchargeWhich = 0;

    private static final int FILE_SELECT_CODE = 0;
    private AgentListApi agentListApi;
    private LoadingIndicatorDialog dialog;
    private AgentAdapter agentAdapter;
    private DepartmentAdapter departmentAdapter;
    private InChargeAdapter inChargeAdapter;

    private int deptId = 0;
    private String inChargeName;
    private String agentId;
    private String priority;
    private File file;
    private boolean isFileUploaded = false;
    private String[] imagePickChoice = new String[]{"Camera", "Choose File"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_action_log);

        agentListApi = new AgentListApi();

        init();
    }

    private void init() {
        if (binding.toolbarLayout.toolbar != null)
            binding.toolbarLayout.toolbar.setTitle("");
        setSupportActionBar(binding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        binding.toolbarLayout.txtCustomTitle.setText(getString(R.string.add_action_log_title));

        if (Functions.isConnected(this)) {
            getAgents();

        } else {
            SimpleToast.error(AddActionLogActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
        }

        actionListener();
    }

    private void actionListener() {

        binding.imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edtSelectFile.setText(getString(R.string.select_file));
                binding.imgClear.setVisibility(View.GONE);
                file = null;
            }
        });

        binding.edtSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Functions.setPermission(AddActionLogActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        new MaterialDialog.Builder(AddActionLogActivity.this)
                                .title(R.string.add_file)
                                .typeface(Functions.getBoldFont(AddActionLogActivity.this), Functions.getRegularFont(AddActionLogActivity.this))
                                .items(imagePickChoice)
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                        if (which == 0) {
                                            openCamera();
                                        } else if (which == 1) {
                                            openGallery();
                                        }

                                        return true;
                                    }
                                })
                                .positiveText(R.string.btn_ok)
                                .show();

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        SimpleToast.error(AddActionLogActivity.this, getString(R.string.permission_denied), getString(R.string.fa_error));
                    }
                });

            }
        });

        binding.edtDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deptList.size() == 0) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.no_dept), getString(R.string.fa_error));

                } else {
                    new MaterialDialog.Builder(AddActionLogActivity.this)
                            .title(R.string.select_dept)
                            .typeface(Functions.getBoldFont(AddActionLogActivity.this), Functions.getRegularFont(AddActionLogActivity.this))
                            .items(deptList)
                            .itemsCallbackSingleChoice(deptWhich, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    deptWhich = which;

                                    Department department = deptList.get(which);
                                    binding.edtDepartment.setText(department.getDepartment());
                                    deptId = Integer.parseInt(department.getDepartmentID());
                                    setDepartmentInchargeAdapter();

                                    return true;
                                }
                            })
                            .positiveText(R.string.btn_ok)
                            .show();
                }
            }
        });

        binding.edtPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(AddActionLogActivity.this)
                        .title(R.string.select_priority)
                        .typeface(Functions.getBoldFont(AddActionLogActivity.this), Functions.getRegularFont(AddActionLogActivity.this))
                        .items(getResources().getStringArray(R.array.priority_array))
                        .itemsCallbackSingleChoice(priorityWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                priorityWhich = which;
                                binding.edtPriority.setText(text);
                                priority = text.toString();
                                return true;
                            }
                        })
                        .positiveText(R.string.btn_ok)
                        .show();
            }
        });

        binding.edtAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agentList.size() == 0) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.no_agents), getString(R.string.fa_error));

                } else {
                    new MaterialDialog.Builder(AddActionLogActivity.this)
                            .title(R.string.select_agent)
                            .typeface(Functions.getBoldFont(AddActionLogActivity.this), Functions.getRegularFont(AddActionLogActivity.this))
                            .items(agentList)
                            .itemsCallbackSingleChoice(agentWhich, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    agentWhich = which;

                                    AgentModel agentModel = agentList.get(which);
                                    binding.edtAgent.setText(agentModel.getName());
                                    agentId = agentModel.getAgentid();

                                    return true;
                                }
                            })
                            .positiveText(R.string.btn_ok)
                            .show();
                }
            }
        });

        binding.edtIncharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deptId == 0) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.select_dept), getString(R.string.fa_error));

                } else if (inChargeList.size() == 0) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.no_pic), getString(R.string.fa_error));

                } else {
                    new MaterialDialog.Builder(AddActionLogActivity.this)
                            .title(R.string.select_pic)
                            .typeface(Functions.getBoldFont(AddActionLogActivity.this), Functions.getRegularFont(AddActionLogActivity.this))
                            .items(inChargeList)
                            .itemsCallbackSingleChoice(inchargeWhich, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    inchargeWhich = which;

                                    InCharge inCharge = inChargeList.get(which);
                                    binding.edtIncharge.setText(inCharge.getPicName());
                                    inChargeName = inCharge.getPicName();

                                    return true;
                                }
                            })
                            .positiveText(R.string.btn_ok)
                            .show();
                }
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // priority = (String) binding.spinnerPriority.getSelectedItem();

                if (TextUtils.isEmpty(agentId)) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.select_agent), getString(R.string.fa_error));
                    return;
                }

                if (deptId == 0) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.select_dept), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(inChargeName)) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.select_pic), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(priority)) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.select_priority), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(binding.edtDescription))) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.enter_description), getString(R.string.fa_error));
                    return;
                }

                if (file == null) {
                    doAddActionLog();

                } else {
                    doUploadFile();
                }

            }
        });
    }

    private void openCamera() {

        RxImagePicker.with(this).requestImage(Sources.CAMERA)
                .flatMap(new Func1<Uri, Observable<File>>() {
                    @Override
                    public Observable<File> call(Uri uri) {
                        return RxImageConverters.uriToFile(AddActionLogActivity.this, uri, createTempFile());
                    }
                })
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file1) {
//                        personalProfileView.setRxImage(file);
                        file = file1;
                        binding.edtSelectFile.setText(file.getName());
                    }
                });

    }

    private File createTempFile() {
        return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "_image.jpeg");
    }

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*|application/pdf");
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(AddActionLogActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    private void doUploadFile() {

        new AsyncTask<Void, Void, Void>() {

            FTPClient client = new FTPClient();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgress(getString(R.string.loading));
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    client.connect(AppConstants.FTP_HOST, 21);
                    client.login(AppConstants.FTP_USER, AppConstants.FTP_PASSWORD);
                    client.setType(FTPClient.TYPE_BINARY);
                    client.changeDirectory("/public_html/salestracker/userfile/");

                    client.upload(file, new MyTransferListener());

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        client.disconnect(true);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (isFileUploaded) {
                    doAddActionLog();
                } else {
                    dismissProgress();
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.upload_failed), getString(R.string.fa_error));
                }
            }
        }.execute();

    }

    private void doAddActionLog() {
        JSONObject json = new JSONObject();
        try {
            json.put("UserId", PrefUtils.getUserId(this));
            json.put("AgentId", Integer.parseInt(agentId));
            json.put("Description", Functions.toStr(binding.edtDescription));
            json.put("Status", "");
            json.put("Priroty", priority);
            if (file == null) {
                json.put("File", "");
            } else {
                json.put("File", Functions.toStr(binding.edtSelectFile));
            }
            json.put("DepartmentId", deptId);
            json.put("PicName", inChargeName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("action_req", json.toString());

        new CallWebService(this, AppConstants.AddActionLog, CallWebService.TYPE_POST, json) {
            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response addResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                if (addResponse != null) {
                    if (addResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(AddActionLogActivity.this, getString(R.string.add_action_success));
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                    } else {
                        SimpleToast.error(AddActionLogActivity.this, addResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AddActionLogActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AddActionLogActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getAgents() {

        showProgress(getString(R.string.loading));

        agentList = new ArrayList<>();

        agentListApi.getAgents(PrefUtils.getUserId(this), new APIListener<AgentListResponse>() {
            @Override
            public void onResponse(Response<AgentListResponse> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    AgentListResponse listResponse = response.body();
                    if (listResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        agentList = listResponse.getData().getAgents();

                        /*agentAdapter = new AgentAdapter(AddActionLogActivity.this, R.layout.item_adapter, agentList);
                        binding.spinnerAgent.setAdapter(agentAdapter);
                        binding.spinnerAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                AgentModel model = (AgentModel) agentAdapter.getItem(position);
                                agentId = model.getAgentid();
                                Log.e("agentId", agentId);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });*/

                    } else {
                        SimpleToast.error(AddActionLogActivity.this, listResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void onFailure(Call<AgentListResponse> call, Throwable t) {
                dismissProgress();
                if (t instanceof TimeoutException) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.time_out), getString(R.string.fa_error));
                } else if (t instanceof UnknownHostException) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                } else {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }
        });
        setDepartmentAdapter();
    }

    private void setDepartmentAdapter() {

        showProgress(getString(R.string.loading));

        deptList = new ArrayList<>();
        //departmentAdapter = new DepartmentAdapter(AddActionLogActivity.this, R.layout.item_adapter, deptList);
        //  binding.spinnerDepartment.setAdapter(departmentAdapter);

        new CallWebService(this, AppConstants.DepartmentList, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {
                dismissProgress();

                DepartmentListResponse getDepartmentListResponse = MyApplication.getGson().fromJson(response, DepartmentListResponse.class);

                if (getDepartmentListResponse != null) {
                    if (getDepartmentListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        deptList = getDepartmentListResponse.getData().getDepartment();

                        /*departmentAdapter.setDepartmentList(getDepartmentListResponse.getData().getDepartment());

                        binding.spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Department department = (Department) departmentAdapter.getItem(position);
                                deptId = department.getDepartmentID();
                                Log.e("deptId", deptId);

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });*/

                    } else {
                        SimpleToast.error(AddActionLogActivity.this, getDepartmentListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AddActionLogActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AddActionLogActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    private void setDepartmentInchargeAdapter() {

        showProgress(getString(R.string.loading));

        inChargeList = new ArrayList<>();

        JSONObject json = new JSONObject();
        try {
            json.put("DepartmentId", deptId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("dept_ic_req", json.toString());

        new CallWebService(this, AppConstants.DepartmentPicList, CallWebService.TYPE_POST, json) {
            @Override
            public void response(String response) {
                dismissProgress();

                InChargeListResponse inChargeListResponse = MyApplication.getGson().fromJson(response, InChargeListResponse.class);

                if (inChargeListResponse != null) {

                    if (inChargeListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        inChargeList = inChargeListResponse.getData().getDepartmentPic();

                    } else {
                        SimpleToast.error(AddActionLogActivity.this, inChargeListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AddActionLogActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AddActionLogActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            // Get the path
            String path = Functions.getPath(this, uri);
            file = new File(path);
            binding.edtSelectFile.setText(file.getName());
            Log.e("path", path);
            binding.imgClear.setVisibility(View.VISIBLE);
        }
    }

    public void showProgress(String string) {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(this, string, android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private class MyTransferListener implements FTPDataTransferListener {
        @Override
        public void started() {
            Log.e("FTP", "File uploading start..");
        }

        @Override
        public void transferred(int i) {
            Log.e("FTP", "File transfered. " + i);
        }

        @Override
        public void completed() {
            Log.e("FTP", "File transfer completed.");
            isFileUploaded = true;
        }

        @Override
        public void aborted() {
            Log.e("FTP", "File transfer aborted.");
        }

        @Override
        public void failed() {
            Log.e("FTP", "File transfer failed.");
        }
    }
}
