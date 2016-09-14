package com.webmne.salestracker.api;

import com.webmne.salestracker.api.model.AddAgentRequest;
import com.webmne.salestracker.api.model.AddAgentResponse;
import com.webmne.salestracker.api.model.AgentListResponse;
import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.api.model.LoginResponse;
import com.webmne.salestracker.api.model.Response;
import com.webmne.salestracker.api.model.TierListResponse;
import com.webmne.salestracker.contacts.model.BranchContactModel;
import com.webmne.salestracker.contacts.model.DepartmentContactModel;
import com.webmne.salestracker.helper.AppConstants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by sagartahelyani on 26-08-2016.
 */
public interface AppApi {

    @GET(AppConstants.LoginBase)
    Call<LoginResponse> login(@Query("password") String password, @Query("username") String username);

    @GET(AppConstants.BranchContact)
    Call<BranchContactModel> getBranchContact(@Query("branch") String branch);

    @GET(AppConstants.DepartmentContact)
    Call<DepartmentContactModel> getDepartmentContact();

    @GET(AppConstants.AgentList)
    Call<AgentListResponse> getAgents(@Query("empid") String userid);

    @GET(AppConstants.TierList)
    Call<TierListResponse> getTier();

    @GET(AppConstants.BranchList)
    Call<BranchListResponse> getBranchList();

    @POST(AppConstants.AddAgent)
    Call<AddAgentResponse> addAgent(@Body AddAgentRequest request);

}