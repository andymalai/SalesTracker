package com.webmne.salestracker.helper;

/**
 * Created by sagartahelyani on 11-08-2016.
 */
public class AppConstants {

    // Common
    // public static final String BASE_URL = "http://ws-srv-php/projects/drupal/amgsales2/";
    public static final String BASE_URL = "http://demo.webmynehost.com/amgsales/";
//    public static final String ATTACHMENT_PREFIX = BASE_URL;
    public static final String ATTACHMENT_PREFIX = "http://ws-srv-php/projects/drupal/amgsales2/";
    public static final String WEB_SERVICE_URL = BASE_URL + "services/";
    public static final String ACTION_LOG_DIRECTORY = "/SalesTracker/ActionLog/";

    // User Position Constants
    public static String MARKETER = "Marketer";
    public static String HOS = "HOS";
    public static String BM = "BM";
    public static String RM = "RM";

    // Action Log Status Constants
    public static String COMPLETE = "C";
    public static String PENDING = "P";
    public static String REJECTED = "R";
    public static String PROCESSING = "PR";
    public static String ORDERDUE = "OD";
    public static String DUETODAY = "DT";
    public static String ONGOING = "OG";
    public static String APPROVE = "A";

    // Calendar Mode
    public static int DAY_VIEW = 1;
    public static int MONTH_VIEW = 2;

    // Response Code
    public static final String SUCCESS = "1";

    // login-url login.php?format=json&password=MKT_guj_001&username=MKT_guj_001&roleID=9
    public static final String LoginBase = "login.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/branch_list.php?format=json&branch=28
    public static final String BranchContact = "branch_list.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/contact_department_list.php?format=json
    public static final String DepartmentContact = "contact_department_list.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/agent_list.php?format=json&userid=676
    public static final String AgentList = "agent_list.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/department.php?format=json
    public static final String DepartmentList = WEB_SERVICE_URL + "department.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/departmentpic.php?format=json
    public static final String DepartmentPicList = WEB_SERVICE_URL + "departmentpic.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/tier_list.php?format=json
    public static final String TierList = "tier_list.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/branch.php?format=json
    public static final String BranchList = "branch.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/add_agent.php
    public static final String AddAgent = WEB_SERVICE_URL + "add_agent.php";

    // http://ws-srv-php/projects/drupal/amgsales2/services/delete_agent.php
    public static final String DeleteAgent = WEB_SERVICE_URL + "delete_agent.php";

    // http://ws-srv-php/projects/drupal/amgsales2/services/update_agent.php
    public static final String UpdateAgent = WEB_SERVICE_URL + "update_agent.php";

    // http://ws-srv-php/projects/drupal/amgsales2/services/update_profile.php
    public static final String UpdateUserProfile = WEB_SERVICE_URL + "update_profile.php";

    // http://ws-srv-php/projects/drupal/amgsales2/services/fetch_action.php?format=json&user_id=699
    public static final String ActionLogList = WEB_SERVICE_URL + "fetch_action.php?format=json&user_id=";

    // http://ws-srv-php/projects/drupal/amgsales2/services/add_action.php
    public static final String AddActionLog = WEB_SERVICE_URL + "add_action.php";

    // http://ws-srv-php/projects/drupal/amgsales2/services/display_remark.php?format=json&action_id=130
    public static final String ActionLogRemarksList = WEB_SERVICE_URL + "display_remark.php?format=json&action_id=";

    // http://ws-srv-php/projects/drupal/amgsales2/services/reopen_remark.php?format=json&reopenid=130
    public static final String ReopenRemark = WEB_SERVICE_URL + "reopen_remark.php?format=json&reopenid=";

    // http://ws-srv-php/projects/drupal/amgsales2/services/update_action.php
    public static final String UpdateActionLog = WEB_SERVICE_URL + "update_action.php";

    // http://demo.webmynehost.com/amgsales/services/fetch_agent_list.php?format=json&user_id=699
    public static final String EmployeeList = WEB_SERVICE_URL + "fetch_agent_list.php?format=json&user_id=";

    // http://demo.webmynehost.com/amgsales/services/fetch_visit_paln.php?format=json
    public static final String PlanList = WEB_SERVICE_URL + "fetch_visit_paln.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/fetch_agent_list.php?format=json&user_id=699
    public static final String AddPlan = WEB_SERVICE_URL + "add_plan.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/update_production.php?format=json
    public static final String UpdateProduction = WEB_SERVICE_URL + "update_production.php?format=json";


    // http://demo.webmynehost.com/amgsales/services/add_mapping.php?format=json
    public static final String SubmitMapping = WEB_SERVICE_URL + "add_mapping.php?format=json";

    //http://demo.webmynehost.com/amgsales/services/mapping.php?format=json
    public static final String FetchMapping = WEB_SERVICE_URL + "mapping.php?format=json";


    // FTP details Local
    public static final String FTP_HOST = "ws-srv-php";
    public static final String FTP_USER = "projects";
    public static final String FTP_PASSWORD = "projects";

}
