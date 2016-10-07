package com.webmne.salestracker.api;

import com.webmne.salestracker.api.model.AddAgentRequest;
import com.webmne.salestracker.api.model.AddAgentResponse;
import com.webmne.salestracker.api.model.AgentListResponse;
import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.api.model.DeleteMappingRequest;
import com.webmne.salestracker.api.model.DeleteMappingResponse;
import com.webmne.salestracker.api.model.DeleteRecruitmentRequest;
import com.webmne.salestracker.api.model.DeleteRecruitmentResponse;
import com.webmne.salestracker.api.model.FetchMappingRequest;
import com.webmne.salestracker.api.model.FetchMappingResponse;
import com.webmne.salestracker.api.model.FetchRecruitmentData;
import com.webmne.salestracker.api.model.FetchRecruitmentRequest;
import com.webmne.salestracker.api.model.FetchRecruitmentResponse;
import com.webmne.salestracker.api.model.LoginResponse;
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

    @POST(AppConstants.FetchMapping)
    Call<FetchMappingResponse> fetchMappingData(@Body FetchMappingRequest request);

    @POST(AppConstants.DeleteMapping)
    Call<DeleteMappingResponse> deleteMapping(@Body DeleteMappingRequest request);

    @POST(AppConstants.FetchRecruitment)
    Call<FetchRecruitmentResponse> fetchRecruitmentData(@Body FetchRecruitmentRequest request);

    @POST(AppConstants.DeleteRecruitment)
    Call<DeleteRecruitmentResponse> deleteRecruitment(@Body DeleteRecruitmentRequest request);

}