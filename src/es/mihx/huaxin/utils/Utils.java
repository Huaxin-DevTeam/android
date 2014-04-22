package es.mihx.huaxin.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.widget.Toast;

public class Utils {
	
	public static void toast(String message){
		Toast.makeText(Constants.getApp(), message, Toast.LENGTH_LONG).show();
	}
	
	public static String numberFormat(double f){
		NumberFormat nf = new DecimalFormat("##.#");
		return nf.format(f);
	}

}
