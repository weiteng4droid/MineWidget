package com.weiteng.image.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


public class CommonUtil {

	public static String md5(String paramString) {
		String returnStr;
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramString.getBytes());
			returnStr = byteToHexString(localMessageDigest.digest());
			return returnStr;
		} catch (Exception e) {
			return paramString;
		}
	}

	/**
	 * 将指定byte数组转换成16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回短时间字符串格式yyyy-MM-dd HH:mm:ss
	 */

	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间
	 *
	 * @return 返回短时间字符串格式yyyy-MM-dd HH:mm
	 */

	public static String getDateTime() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * px到sp的转换
	 * @param context
	 * @param pxValue
	 * @return
	 */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (pxValue / fontScale + 0.5f); 
    }
	
	public static DisplayMetrics getMetrics(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}
	
	/**
	 * 获取状态栏高度
	 */
	public static int getStatusBarHeight(View v) {
		if (v == null) {
			return 0;
		}
		Rect frame = new Rect();
		v.getWindowVisibleDisplayFrame(frame);
		return frame.top;
	}
	
	/**
	 * 获取手机屏幕的像素密集点
	 *
	 */
	public static int getScreenDensity(Context context) {
		return getMetrics(context).densityDpi;
	}

	private static Drawable createDrawable(Drawable d, Paint p) {

		BitmapDrawable bd = (BitmapDrawable) d;
		Bitmap b = bd.getBitmap();
		Bitmap bitmap = Bitmap.createBitmap(bd.getIntrinsicWidth(), bd.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(b, 0, 0, p);

		return new BitmapDrawable(bitmap);
	}

	/**
	 * 设置Selector。 本次只增加点击变暗的效果，注释的代码为更多的效果
	 *
	 */
	public static StateListDrawable createSLD(Context context, Drawable drawable) {
		StateListDrawable bg = new StateListDrawable();
		int brightness = 50 - 127;
		ColorMatrix cMatrix = new ColorMatrix();
		cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1, 0, 0,
				brightness,// 改变亮度
				0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });

		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

		Drawable normal = drawable;
		Drawable pressed = createDrawable(drawable, paint);
		bg.addState(new int[] { android.R.attr.state_pressed, }, pressed);
		bg.addState(new int[] { android.R.attr.state_focused, }, pressed);
		bg.addState(new int[] { android.R.attr.state_selected }, pressed);
		bg.addState(new int[]{}, normal);
		return bg;
	}

	public static Bitmap getImageFromAssetsFile(Context ct, String fileName) {
		Bitmap image = null;
		AssetManager am = ct.getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;

	}

	public static String getUploadtime(long created) {
		
		StringBuffer when = new StringBuffer();
		int difference_seconds;
		int difference_minutes;
		int difference_hours;
		int difference_days;
		int difference_months;
		long curTime = System.currentTimeMillis();
		difference_months = (int) (((curTime / 2592000) % 12) - ((created / 2592000) % 12));
		if (difference_months > 0) {
			when.append(difference_months + "月");
		}

		difference_days = (int) (((curTime / 86400) % 30) - ((created / 86400) % 30));
		if (difference_days > 0) {
			when.append(difference_days + "天");
		}

		difference_hours = (int) (((curTime / 3600) % 24) - ((created / 3600) % 24));
		if (difference_hours > 0) {
			when.append(difference_hours + "小时");
		}

		difference_minutes = (int) (((curTime / 60) % 60) - ((created / 60) % 60));
		if (difference_minutes > 0) {
			when.append(difference_minutes + "分钟");
		}

		difference_seconds = (int) ((curTime % 60) - (created % 60));
		if (difference_seconds > 0) {
			when.append(difference_seconds + "秒");
		}

		return when.append("前").toString();
	}
	
	/** 
     * 计算两个日期之间相差的天数
	 *
     * @param smdate 较小日期
     * @param bdate  较大日期
     * @return 相差天数
     */
    public static int daysBetweenTwoTime(String smdate, String bdate) {
		try {
			return daysBetween(smdate, bdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return 0;
    }

	/**
	 * 将金钱double转化为String
	 * @param b
	 * @return
	 */
	public static String getMoney(Double b) {
		if(b==0){
			return "无";
		}
		int z=(int) ((b*100)%100);
		int y=(int) (b%1000);
		if (b >= 1000) {
			int i = (int) (b / 1000);
			if (y > 100) {
				if (z < 10) {
					return String.valueOf(i) + "," + String.valueOf(y) + ".0" + String.valueOf(z);
				} else {
					return String.valueOf(i) + "," + String.valueOf(y) + "." + String.valueOf(z);
				}
			} else if (y < 100) {
				if (z < 10) {
					return String.valueOf(i) + ",0" + String.valueOf(y) + ".0" + String.valueOf(z);
				} else {
					return String.valueOf(i) + ",0" + String.valueOf(y) + "." + String.valueOf(z);
				}
			} else if (y < 10) {
				if (z < 10) {
					return String.valueOf(i) + ",00" + String.valueOf(y) + ".0" + String.valueOf(z);
				} else {
					return String.valueOf(i) + ",00" + String.valueOf(y) + "." + String.valueOf(z);
				}
			}

		}
		return String.valueOf(b);
	}

	/**
	 * 获取上个月月份
	 */
	public static String getPreMonth(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		int month = calendar.get(Calendar.MONTH);
		return month + "月";
	}

	/**
	 * 获取当前的日期
	 *
	 * @return 	返回的是当前的日期字符串  如："2015-03-08"
	 */
	public static String getCurrentDate() {
		return getCurrentDate(0);
	}

	/**
	 * get year offset current date
	 */
	public static String getCurrentDate(int offset) {
		Calendar calendar = Calendar.getInstance();
		return formatDate(offset, calendar);
	}

	public static String calculateYearOffset(String date, int offset) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(date));
			return formatDate(offset, calendar);
		} catch (ParseException e) {
			e.printStackTrace();
			return date;
		}
	}

	public static int getYearOfFormatDate(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(date));
			return calendar.get(Calendar.YEAR);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static String formatDate(int offset, Calendar calendar) {
		int year = getCurrentYearOffset(calendar, offset);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return String.format("%d-%s-%s", year, (month < 10 ? "0" + month : month), (day < 10 ? "0" + day : day));
	}

	private static int getCurrentYearOffset(Calendar calendar, int offset) {
		calendar.add(Calendar.YEAR, offset);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取当前的日期
	 * @param model		1:请求用数据， 2.显示用数据
	 * @return 			返回的是当前的日期字符串  如："2015-03-08"
	 */
	public static String getCurrentDateMonth(int model){
		
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		String dateStr = "";
		if(model == 1){
			dateStr = year + "" + (month <10 ? "0"+month : month);
		}else if(model == 2){
			dateStr = year + "年" + month + "月";
		}
		return dateStr;
	}
	
	/**
	 * 获取明天的日期
	 *
	 */
	public static String getTomorrowDate(){
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return String.format("%d-%s-%s", year, (month < 10 ? "0" + month : month), (day < 10 ? "0" + day : day));
	}
	
	/**
	 * 获取一年后的今天
	 */
	public static String getOneYearLater(String currentStr){
		
		Date date = null;
		String syncTime = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		try {
			date = df.parse(currentStr);
			Calendar canlandar = Calendar.getInstance();
			canlandar.setTime(date);
			canlandar.add(Calendar.YEAR, 1);
			canlandar.roll(Calendar.DAY_OF_MONTH, -1);
			syncTime = df.format(canlandar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return syncTime;
	}
	
	/**
	 * 获取一年有多少天
	 * @param year
	 * @return
	 */
	private static int getMaxDaysOfYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);

		return cal.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 计算两个日期之间相差的天数
	 *
	 * @param smdate	较小的日期
	 * @param bdate		较大的日期
	 * @return 			相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 计算两个日期之间相差的天数
	 *
	 * @param smdate	较小的日期
	 * @param bdate		较大的日期
	 * @return			相差天数
	 * @throws ParseException
     */
	public static int daysBetween(String smdate, String bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(smdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(bdate));
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 给定时间距离今天的时间
	 *
	 * @param date	制定的日期
	 * @return		相差天数
     */
	public static int daysBetweenToday(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			String today = sdf.format(new Date());
			return daysBetween(today, date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

    /**
     * example : 2017-03-01 转换为 2017年3月1日到期
     */
    public static String parseDate2(String date) {
        return formatDate("yyyy-MM-dd", "yyyy年M月d日到期", date);
    }

    /**
     * example: 0820 转换为 8月20日
     */
    public static String parseDate3(String date) {
        return formatDate("MMdd", "M月d日", date);
    }

	/**
	 * 转换字符串日期的格式
	 *
	 * @param patternOld	旧的规则
	 * @param patternNew	新的规则
	 * @param dateContent	原始字符串日期
     * @return				新的字符串日期
     */
	private static String formatDate(String patternOld, String patternNew, String dateContent) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(patternOld, Locale.getDefault());
			Date date = format.parse(dateContent);
			format.applyPattern(patternNew);
			return format.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateContent;
	}
	
	/**
	 * 弹出软件盘
	 * 
	 */
	public static void autoPopInput(final EditText eText) {

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager inputManager=(InputMethodManager)eText.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(eText, 0);
			}
		}, 500);
	}
	
	/**
	 * 取消软键盘
	 * 
	 */
	public static void hideKeyboard(Activity context) {
		
		View view = context.getWindow().peekDecorView();
		if (view != null && view.getWindowToken() != null) {
			
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 判断指定的服务是否在运行
	 * @param context 上下文
	 * @param clazz 服务的字节码对象
	 * @return true: is running, false: is not running.
	 */
	public static <T> boolean isServiceRunning(Context context, Class<T> clazz){
		// 获取activity管理器对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am.getRunningServices(20);
		for (RunningServiceInfo serviceInfo : runningServices) {
			String className = serviceInfo.service.getClass().getName();
			if(clazz.getName().equals(className)){
				return true;
			}
		}
		return false;
	}

	private static long lastClickTime;

	/**
	 * 放置按钮短时间内多次点击
	 * @return	true is click fast
	 */
	public synchronized static boolean isFastClick() {
		long time = System.currentTimeMillis();
		if ( time - lastClickTime < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * call phone
	 *
	 */
	public static void callPhone(Context context, String phoneNumber){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + phoneNumber));
		context.startActivity(intent);
	}

	/**
	 * send sms
	 *
	 */
	public static void sendSMS(Context context, String phone, String content) {
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
		intent.putExtra("sms_body", content);
		context.startActivity(intent);
	}

	/**
	 * @return SD卡是否可用   true可用，false不可用
	 *
	 */
	public static boolean hasSDCard(){
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * @return SD卡的路径
	 */
	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * install apk file of system activity
	 */
	public static void installAPK(Context context, File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static void setBackgroundDrawable(View label, Drawable drawable) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			label.setBackground(drawable);
		} else {
			label.setBackgroundDrawable(drawable);
		}
	}

	public static int parserColor(String color) {
		return parserColor(color, 0xff3095ef);
	}

	public static int parserColor(String color, @ColorInt int defaultColor) {
		if (TextUtils.isEmpty(color)) {
			return defaultColor;
		}

		try {
			return Color.parseColor(color);
		} catch (Exception e) {
			e.printStackTrace();
			return defaultColor;
		}
	}

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	private static int createViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	/**
	 * Generate a value suitable for use in setId(int)
	 * This value will not collide with ID values generated at build time by aapt for R.id.
	 *
	 * @return a generated ID value
	 */
	public static int generateViewId() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return createViewId();
		} else {
			return View.generateViewId();
		}
	}

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 设置消息徽章的个数
	 *
	 * @param label	徽章
	 * @param count	数目
     */
	public static void setLabelCount(TextView label, int count) {
		if (count > 0) {
			label.setVisibility(View.VISIBLE);
			if (count > 99) {
				label.setTextSize(10);
				label.setText("99+");
			} else {
				label.setTextSize(12);
				label.setText(String.valueOf(count));
			}
		} else {
			label.setVisibility(View.INVISIBLE);
			label.setText(String.valueOf(0));
		}
	}

	/**
	 * 创建 通用的背景图标(带描边)
	 *
     */
	public static ShapeDrawable createStrokeDrawable(Context context, @DimenRes int radiusId,
													 @DimenRes int strokeId, @ColorRes int colorId) {
		int radius = context.getResources().getDimensionPixelSize(radiusId);
		int strokeWidth = context.getResources().getDimensionPixelSize(strokeId);
		int color = context.getResources().getColor(colorId);

		RectF inset = new RectF((float) strokeWidth, (float) strokeWidth, (float) strokeWidth, (float) strokeWidth);
		float[] outerRadii = new float[] {
				radius, radius, radius, radius,
				radius, radius, radius, radius
		};
		float[] innerRadii = new float[] {
				radius- strokeWidth, radius- strokeWidth, radius- strokeWidth, radius- strokeWidth,
				radius- strokeWidth, radius- strokeWidth, radius- strokeWidth, radius- strokeWidth
		};
		RoundRectShape shape = new RoundRectShape(outerRadii, inset, innerRadii);
		ShapeDrawable drawable = new ShapeDrawable(shape);
		drawable.getPaint().setColor(color);
		return drawable;
	}

	/**
	 * 创建圆角矩形背景
	 */
	public static ShapeDrawable createBackgroundDrawable(Context context, @DimenRes int ltrId,
														 @DimenRes int rtrId, @DimenRes int lbrId,
														 @DimenRes int rbrId, @ColorRes int colorRes) {
		int leftTopRadius = context.getResources().getDimensionPixelSize(ltrId);
		int rightTopRadius = context.getResources().getDimensionPixelSize(rtrId);
		int leftBottomRadius = context.getResources().getDimensionPixelSize(lbrId);
		int rightBottomRadius = context.getResources().getDimensionPixelSize(rbrId);
		int color = context.getResources().getColor(colorRes);
		return createBackgroundDrawable(leftTopRadius, rightTopRadius, leftBottomRadius, rightBottomRadius, color);
	}

	/**
	 * 创建圆角矩形背景
     */
	public static ShapeDrawable createBackgroundDrawable(int leftTopRadius,
														 int rightTopRadius, int leftBottomRadius,
														 int rightBottomRadius, @ColorInt int color) {
		float[] outerRadii = new float[] {
				leftTopRadius, leftTopRadius,
				rightTopRadius, rightTopRadius,
				leftBottomRadius, leftBottomRadius,
				rightBottomRadius, rightBottomRadius
		};
		RoundRectShape shape = new RoundRectShape(outerRadii, null, null);
		ShapeDrawable drawable = new ShapeDrawable(shape);
		drawable.getPaint().setColor(color);
		return drawable;
	}

	public static String getUriFromPath(String path) {
		return "file://" + path;
	}
}
