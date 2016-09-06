package com.webmne.salestracker.helper;

/**
 * Created by sagartahelyani on 11-08-2016.
 */
public class AppConstants {

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

    // http://ws-srv-php/projects/drupal/amgsales2/services/tier_list.php?format=json
    public static final String TierList = "tier_list.php?format=json";

    // http://ws-srv-php/projects/drupal/amgsales2/services/branch.php?format=json
    public static final String BranchList = "branch.php?format=json";

}
