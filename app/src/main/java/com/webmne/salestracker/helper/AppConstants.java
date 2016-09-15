package com.webmne.salestracker.helper;

/**
 * Created by sagartahelyani on 11-08-2016.
 */
public class AppConstants {

    // User Position Constants
    public static String MARKETER = "Marketer";
    public static String HOS = "HOS";
    public static String BM = "BM";
    public static String RM = "RM";

    // Action Log Status Constants
    public static String COMPLETE = "C";
    public static String PENDING = "P";
    public static String REJECTED = "R";
    public static String PROCESSING = "";

    // Calendar Mode
    public static int DAY_VIEW = 0;
    public static int WEEK_VIEW = 1;
    public static int MONTH_VIEW = 2;

    // Response Code
    public static final String SUCCESS = "1";

    public static final String BASE_URL = "http://ws-srv-php/projects/drupal/amgsales2/services/";

    // login-url login.php?format=json&password=MKT_guj_001&username=MKT_guj_001&roleID=9
    public static final String LoginBase = "login.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/branch_list.php?format=json&branch=28
    public static final String BranchContact = "branch_list.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/contact_department_list.php?format=json
    public static final String DepartmentContact = "contact_department_list.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/agent_list.php?format=json&userid=676
    public static final String AgentList = "agent_list.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/department.php?format=json
    public static final String DepartmentList = BASE_URL + "department.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/departmentpic.php?format=json
    public static final String DepartmentPicList = BASE_URL + "departmentpic.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/tier_list.php?format=json
    public static final String TierList = "tier_list.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/branch.php?format=json
    public static final String BranchList = "branch.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/add_agent.php
    public static final String AddAgent = BASE_URL + "add_agent.php";

    // http://ws-srv-php/projects/drupal/amgsales2/services/delete_agent.php
    public static final String DeleteAgent = BASE_URL + "delete_agent.php";

    // http://ws-srv-php/projects/drupal/amgsales2/services/update_agent.php
    public static final String UpdateAgent = BASE_URL + "update_agent.php";

    // http://ws-srv-php/projects/drupal/amgsales2/services/update_profile.php
    public static final String UpdateUserProfile = BASE_URL + "update_profile.php";

    // http://ws-srv-php/projects/drupal/amgsales2/services/fetch_action.php?format=json&user_id=699
    public static final String ActionLogList = BASE_URL + "fetch_action.php?format=json&user_id=";

    // http://ws-srv-php/projects/drupal/amgsales2/services/add_action.php
    public static final String AddActionLog = BASE_URL + "add_action.php";

    // http://ws-srv-php/projects/drupal/amgsales2/services/display_remark.php?format=json&action_id=130
    public static final String ActionLogRemarksList = BASE_URL + "display_remark.php?format=json&action_id=";

    // FTP details Local
    public static final String FTP_HOST = "ws-srv-php";
    public static final String FTP_USER = "projects";
    public static final String FTP_PASSWORD = "projects";

}
