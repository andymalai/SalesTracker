package com.webmne.salestracker.helper;

import android.content.Context;

import com.google.gson.Gson;
import com.webmne.salestracker.api.model.AgentModel;
import com.webmne.salestracker.api.model.UserProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xitij on 17-03-2015.
 */
public class PrefUtils {

    private static final String USER_PROFILE = "USER_PROFILE";
    private static final String BRANCH_ID = "BRANCH_ID";
    private static final String USER_ID = "USER_ID";
    private static final String IS_LOGGED_IN = "IS_LOGGED_IN";
    private static final String DELETE = "DELETE";

    public static void setLoggedIn(Context context, boolean isLoggedIn) {
        Prefs.with(context).save(IS_LOGGED_IN, isLoggedIn);
    }

    public static boolean getLoggedIn(Context context) {
        return Prefs.with(context).getBoolean(IS_LOGGED_IN, false);
    }

    public static void setUserProfile(Context context, UserProfile response) {
        String toJson = MyApplication.getGson().toJson(response);

        setBranchId(context, response.getBranch());
        setUserId(context, response.getUserid());

        Prefs.with(context).save(USER_PROFILE, toJson);

        setLoggedIn(context, true);
    }

    private static void setUserId(Context context, String userid) {
        Prefs.with(context).save(USER_ID, userid);
    }

    public static String getUserId(Context context) {
        return Prefs.with(context).getString(USER_ID, "");
    }

    public static void setBranchId(Context context, String branchId) {
        Prefs.with(context).save(BRANCH_ID, branchId);
    }

    public static String getBranchId(Context context) {
        return Prefs.with(context).getString(BRANCH_ID, "");
    }

    public static UserProfile getUserProfile(Context context) {
        UserProfile profile = null;
        String jsonString = Prefs.with(context).getString(USER_PROFILE, "");
        try {
            profile = MyApplication.getGson().fromJson(jsonString, UserProfile.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }

    public static List<AgentModel> getDeleteAgents(Context context) {
        List<AgentModel> deleteAgents;

        String jsonString = Prefs.with(context).getString(DELETE, "");
        if (jsonString == null) {
            return null;

        } else {
            Gson gson = new Gson();
            AgentModel[] agents = gson.fromJson(jsonString, AgentModel[].class);
            if (agents == null) {
                return null;
            } else {
                deleteAgents = Arrays.asList(agents);
                deleteAgents = new ArrayList<AgentModel>(deleteAgents);
                return deleteAgents;
            }
        }
    }

    public static void setAgent(Context context, AgentModel agentModel) {

        List<AgentModel> agents = getDeleteAgents(context);
        if (agents == null) {
            agents = new ArrayList<>();
        }
        if (agents.contains(agentModel)) {
            agents.remove(agentModel);
        } else {
            agents.add(agentModel);
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(agents);
        Prefs.with(context).save(DELETE, jsonString);
    }

    public static void deselectAll(Context context) {
        List<AgentModel> agents = getDeleteAgents(context);
        if (agents == null) {
            agents = new ArrayList<>();
        } else {
            agents.clear();
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(agents);
        Prefs.with(context).save(DELETE, jsonString);
    }
}
