package es.mihx.huaxin.utils;

import es.mihx.huaxin.HuaxinApp;

public class Constants {

	/* LOG */
	public static final String ERROR_TAG = "::ERROR::";
	public static final String DEBUG_TAG = "::DEBUG::";
	
	public static final String PREFS_NAME = "HUAXIN_PREFS";
	public static final String PREFS_FAV_KEY = "HUAXIN_FAVORITES";
	public static final String PREFS_TOKEN_KEY = "HUAXIN_USER_TOKEN";
	
	public static final String PARAM_ID = "PARAM_ID";
	
	public static final int OK = 1;
	public static final int KO = 0;
	
	public static final int HTTP_OK = 200;
	
	public static final String BASEURL = "http://dev.mihx.es";
	public static final String VIEWITEMURL = "/view/";
	
	public static final String FROM_SPLASH = "FROM_SPLASH";
	public static final String FROM_LOGOUT = "FROM_LOGOUT";
	
	private static HuaxinApp app;

	public static HuaxinApp getApp() {
		return app;
	}

	public static void setApp(HuaxinApp myapp) {
		app = myapp;
	}
	
}
