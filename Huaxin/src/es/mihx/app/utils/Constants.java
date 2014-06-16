package es.mihx.app.utils;

import es.mihx.app.HuaxinApp;

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

	// Vending
	public static final String APIKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmSFZik4+pRUtPLT6hrLJMXhxgJD9tuEutI/7GDGPCZXQ9x/U6JP8cUGD36BTXjCDzKpNHo3oxO/ppx5erFqYFhqJVfZmdzWjco1U1YhdcDQp+c0F5a9Nq8JViFY3w11PcjDkRoKzGuYbQ3nL+dRI/dsuw9F5F+k51+XgrxVu/nX6LQ+niCG7VZ5hiepxKi5ksDFYCnrdMhGGYEgzoJmybWfiebMJWH0MItT6CL8YEcniL7oAK2Kx8cLf/IWbKf/VywaGBGVo34OrJiZP8cRqseus719w716EAknxz3O62vHhOEp6pzmj+2o6OQL6o/bcSi/TaWGa3BIIAHl/98I8zwIDAQAB";
	public static final String SKU1 = "huaxin_creditoption_1";
	public static final String SKU2 = "huaxin_creditoption_2";

	private static HuaxinApp app;

	public static HuaxinApp getApp() {
		return app;
	}

	public static void setApp(HuaxinApp myapp) {
		app = myapp;
	}

}
