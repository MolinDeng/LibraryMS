package myLib;
import java.text.*;

public class MyDate {
	public static String dateTrans(long t) {
		SimpleDateFormat df = (SimpleDateFormat)DateFormat.getInstance();
		df.applyPattern("yyyy-MM-dd");
		String date = df.format(t);
		return date;
	}
}
