package com.webmne.salestracker.helper;

/**
 * Created by sagartahelyani on 11-08-2016.
 */
public class AppConstants {

    // Common
    // public static final String BASE_URL = "http://ws-srv-php/projects/drupal/amgsales2/";
//    public static final String BASE_URL = "http://insurance4all.com.my/salestracker/";
    public static final String BASE_URL = "http://demo.webmynehost.com/amgsales/";
    //    public static final String ATTACHMENT_PREFIX = BASE_URL;
    public static final String ATTACHMENT_PREFIX = "http://insurance4all.com.my/salestracker/";
    public static final String ACTION_LOG_ATTACHMENT_PREFIX = "http://ws-srv-php/projects/drupal/amgsales2/";
    public static final String COMMUNICATION_FILE_PATH = "sites/default/files/communication/";
    public static final String WEB_SERVICE_URL = BASE_URL + "services/";
    public static final String ACTION_LOG_DIRECTORY = "/SalesTracker/ActionLog/";
    public static final String COMMUNICATION_DIRECTORY = "/SalesTracker/Communication/";

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
    public static String OVERDUE = "OD";
    public static String DUETODAY = "DT";
    public static String ONGOING = "OG";
    public static String APPROVE = "A";

    // position int constants
    public static final int MKT_POS = 9;
    public static final int HOS_POS = 6;
    public static final int BM_POS = 5;
    public static final int RM_POS = 4;

    // Plan Item click listener
    public static final int DELETE_PLAN = 1;
    public static final int OPEN_REMARK = 2;
    public static final int UPDATE_BOX = 3;

    // Calendar Mode
    public static int DAY_VIEW = 1;
    public static int MONTH_VIEW = 2;

    // Plan status Constants
    public static String B = "B";
    public static String O = "O";
    public static String X = "X";

    // Chart Tyes
    public static final String CHART_TYPE = "type";
    public static final int BRANCH_CHART = 1;
    public static final int DEPARTMENT_CHART = 2;
    public static final int DEPARTMENT_SLA_CHART = 3;
    public static final int VISIT_PLAN_CHART = 4;

    // Response Code
    public static final String SUCCESS = "1";

    // login-url login.php?format=json&password=MKT_guj_001&username=MKT_guj_001&roleID=9
    public static final String LoginBase = "login.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/login.php?format=json&password=MKT_guj_001&username=MKT_guj_001
    public static final String Login = WEB_SERVICE_URL + "login.php?format=json";

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

    // http://demo.webmynehost.com/amgsales/services/fetch_visit_paln.php?format=json
    public static final String PlanList = WEB_SERVICE_URL + "fetch_visit_paln.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/fetch_agent_list.php?format=json&user_id=699
    public static final String AgentForPlan = WEB_SERVICE_URL + "fetch_agent_list.php?format=json&user_id=";

    // http://demo.webmynehost.com/amgsales/services/add_plan.php?format=json
    public static final String AddPlan = WEB_SERVICE_URL + "add_plan.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/update_box.php?format=json
    public static final String BOXUpdate = WEB_SERVICE_URL + "update_box.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/update_remark.php?format=json
    public static final String UpdateRemarkPlan = WEB_SERVICE_URL + "update_remark.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/delete_plan.php?format=json
    public static final String DeletePlan = WEB_SERVICE_URL + "delete_plan.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/update_production.php?format=json
    public static final String UpdateProduction = WEB_SERVICE_URL + "update_production.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/add_mapping.php?format=json
    public static final String SubmitMapping = WEB_SERVICE_URL + "add_mapping.php?format=json";

    //http://demo.webmynehost.com/amgsales/services/mapping.php?format=json
    public static final String FetchMapping = WEB_SERVICE_URL + "mapping.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/delete_mapping.php?format=json
    public static final String DeleteMapping = WEB_SERVICE_URL + "delete_mapping.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/update_mapping.php?format=json
    public static final String UpdateMapping = WEB_SERVICE_URL + "update_mapping.php?format=json";

    //http://demo.webmynehost.com/amgsales/services/recruitment.php?format=json
    public static final String FetchRecruitment = WEB_SERVICE_URL + "recruitment.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/add_mapping.php?format=json
    public static final String SubmitRecruitment = WEB_SERVICE_URL + "add_recuriment.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/delete_recruiment.php?format=json
    public static final String DeleteRecruitment = WEB_SERVICE_URL + "delete_recruiment.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/fetch_communication.php?format=json
    public static final String CommunicationList = WEB_SERVICE_URL + "fetch_communication.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/fetch_events.php?format=json
    public static final String EventList = WEB_SERVICE_URL + "fetch_events.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/region.php?format=json
    public static final String Region = WEB_SERVICE_URL + "region.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/branch.php?format=json&regionid=0
    public static final String Branch = WEB_SERVICE_URL + "branch.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/position.php?format=json
    public static final String Position = WEB_SERVICE_URL + "position.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/add_event.php?format=json
    public static final String AddEvent = WEB_SERVICE_URL + "add_event.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/update_event.php?format=json
    public static final String UpdateEvent = WEB_SERVICE_URL + "update_event.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/delete_event.php?format=json
    public static final String DeleteEvent = WEB_SERVICE_URL + "delete_event.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/fetch_emp.php?format=json&user_id=697
    public static final String EmployeeList = WEB_SERVICE_URL + "fetch_emp.php?format=json&user_id=";

    // http://demo.webmynehost.com/amgsales/services/add_emp.php?format=json
    public static final String AddEmployee = WEB_SERVICE_URL + "add_emp.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/update_emp.php?format=json
    public static final String UpdateEmployee = WEB_SERVICE_URL + "update_emp.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/delete_emp.php?format=json
    public static final String DeleteEmployee = WEB_SERVICE_URL + "delete_emp.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/aap_rej_status.php?format=json
    public static final String ApproveRejectActionLog = WEB_SERVICE_URL + "aap_rej_status.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/fetch_if_no.php?format=json&user_id=700&Date=2016-10-21
    public static final String FetchRemarks = WEB_SERVICE_URL + "fetch_if_no.php?format=json&user_id=";

    // http://demo.webmynehost.com/amgsales/services/branch_chart.php?format=json
    public static final String BranchChart = WEB_SERVICE_URL + "branch_chart.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/dept_chart.php?format=json
    public static final String DeptChart = WEB_SERVICE_URL + "dept_chart.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/dept_sla.php?format=json
    public static final String DeptSlaChart = WEB_SERVICE_URL + "dept_sla.php?format=json";

    // http://demo.webmynehost.com/amgsales/services/graph_attain.php?format=json
    public static final String PlanChart = WEB_SERVICE_URL + "graph_attain.php?format=json";

    // FTP details Local
    public static final String FTP_HOST = "insurance4all.com.my";
    //public static final String FTP_HOST = "http://demo.webmynehost.com/";
    public static final String FTP_USER = "insura11";
    public static final String FTP_PASSWORD = "yIfv50Kx78";

}
