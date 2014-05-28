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

	// Vending
	public static final String APIKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAokXdl6uISA0xwf1E9mnfpHR6hzK6DOZqe3+n4tvczOrXBErW1I6xseTUqrW1o/SvUuQyUdPrbmVUzgATyYPWKs3Do7ACfvVBBZsXf3hTbrs9K3tmSdYQIx1v00vQTSJtG4ee6Q6VWs3W05uOs+K89hJGy54lm5rthZmLps5td6GOyObAyAYKPfYYDAOww9UpHNg2A21nv5/tNSm45xHKwcdcVUmFaZ+jewyutfZE8CimxO87zmYjcka2cNFKGYaoMgb22L55YahgynS3H3K9ejU/vXYa8uBS4Z+pp+p/nrKf2iUcE5a4ayyb7zovrPO7fXj01u45TbusjoMiBko1dwIDAQAB";
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
