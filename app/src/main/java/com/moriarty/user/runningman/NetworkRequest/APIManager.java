package com.moriarty.user.runningman.NetworkRequest;

/**
 * Created by user on 17-10-11.
 */
public class APIManager {
    private final static String HOST="http://192.168.191.1";
    private final static String POST=":8089";
    public final static String BASE_URL=HOST+POST;
    public final static String CHECK_USERNAME_URL="/server/CheckUsernameServlet";
    public final static String LOGIN_URL="/server/LoginServlet";
    public final static String SIGNUP_URL="/server/SignUpServlet";
    public final static String CREATE_ACTIVITY_URL="/server/CreateActivityServlet";
    public final static String DELETE_ACTIVITY_URL="/server/DeleteActivityServlet";
    public final static String QUERY_ACTIVITY_URL="/server/QueryActivityServlet";
}
