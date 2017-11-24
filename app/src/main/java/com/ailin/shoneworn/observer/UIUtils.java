package com.ailin.shoneworn.observer;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class UIUtils {
	
	//字体大小
	public static int SMALLEST = 14;
	public static int NORMAL = 16;
	public static int LARGER = 18;
	public static int LARGEST = 20;
	public static int TEXTSIZE = NORMAL;
	private static int flag = 1;
	private static int status_bar_height = -1; 
	public static Context getContext(){
		return BaseApplication.getContext();
	}
	public static int getMainThreadId(){
		return BaseApplication.getMainThreadId();
	}
//	public static void callSp2px(){
//		SMALLEST = sp2px(R.dimen.small_text_size);
//		NORMAL = sp2px(R.dimen.normal_text_size);
//		LARGER = sp2px(R.dimen.larger_text_size);
//		LARGEST =sp2px(R.dimen.largest_text_size);
//		TEXTSIZE = CacheUtils.getInt(UIUtils.getContext(), Constants.TEXTSIZE, NORMAL);
//	}
	public static int sp2px(int id){
		return (int) (BaseApplication.getContext().getResources().getDimension(id)/BaseApplication.px);
	}
	/** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue 
     *            （DisplayMetrics类中属性scaledDensity）
     * @return 
     */ 
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  


	public static Handler getHandler(){
		return BaseApplication.getHandler();
	}
	
	//string
	public static String getString(int id){
		return getContext().getResources().getString(id);
	}
	//drawable
	public static Drawable getDrawable(int id){
		return getContext().getResources().getDrawable(id);
	}
	//stringArray
	public static String[] getStringArray(int id){
		return getContext().getResources().getStringArray(id);
	}
	
	//dip--->px   1dp = 1px   1dp = 2px  
	public static int dip2px(double dip){
		//dp和px的转换关系比例
		float density = getContext().getResources().getDisplayMetrics().density;
		return (int)(dip*density+0.5);
	}

	/**
	 * dip 转换成 px
	 * @param dip
	 * @param context
	 * @return
	 */
	public static float dip2Dimension(float dip, Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
	}
	
	public static float dip2pxExact(float dip){
        //dp和px的转换关系比例
        float density = getContext().getResources().getDisplayMetrics().density;
        return (dip*density+0.5f);
    }
	//px---->dp
	public static int px2dip(int px){
		//dp和px的转换关系比例
		float density = getContext().getResources().getDisplayMetrics().density;
		return (int)(px/density+0.5);
	}
	
	//判断是否是主线的方法
	public static boolean isRunInMainThread(){	
		return getMainThreadId() == android.os.Process.myTid(); 
	}
	
	//保证当前的UI操作在主线程里面运行
	public static void runInMainThread(Runnable runnable){
		if(isRunInMainThread()){
			//如果现在就是在珠现场中，就直接运行run方法
			runnable.run();
		}else{
			//否则将其传到主线程中运行
			getHandler().post(runnable);
		}
	}
	//java代码区设置颜色择器的方法
	public static ColorStateList getColorStateList(int mTabTextColorResId) {
		return getContext().getResources().getColorStateList(mTabTextColorResId);
	}
	
	public static View inflate(int id){
		return View.inflate(getContext(), id, null);
	}
	public static int getDimens(int id) {
		//根据dimens中提供的id，将其对应的dp值转换成相应的像素大小
		return UIUtils.getContext().getResources().getDimensionPixelSize(id);
	}
	public static void postDelayed(Runnable runnable, long delayTime) {
		getHandler().postDelayed(runnable, delayTime);
	}
	public static void removeCallBack(Runnable runnableTask) {
		//移除传递进来任务
		getHandler().removeCallbacks(runnableTask);
	}
	public static int getColor(int id) {
		return getContext().getResources().getColor(id);
	}
//	public static String transfer(String date) {
//		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		if(TextUtils.isEmpty(date))
//			return "";
//		 long millionSeconds = 0;
//		 String str2 = null;
//		if (date != null) {
//			try {
//				millionSeconds = sdf.parse(date).getTime();
//
//				long currentTimeMillis = System.currentTimeMillis();
//				int time = (int) ((currentTimeMillis - millionSeconds)/60000);
//				if(time>=0){
//					if(time<=20){
//						str2 = getContext().getString(R.string.latest);
//					}else if(time>20&&time<60){
//						str2 = time+getContext().getString(R.string.min_ago);
//					}else if(time<24*60&&time>=60){
//						str2 = time/(60)+getContext().getString(R.string.hour_ago);
//					}else if(time<=7*24*60&&time>=24*60){
//						str2 = time/(24*60)+getContext().getString(R.string.day_ago);
//					}else if(time<=((4*7+2)*24*60)&&time>=7*24*60){
//						str2 = time/(7*24*60)+getContext().getString(R.string.week_ago);
//					}else if(time<=12*(4*7+2)*24*60&&time>=(4*7+2)*24*60){
//						str2 = time/((4*7+2)*24*60) +getContext().getString(R.string.month_ago);
//					}else {
//						str2 = getContext().getString(R.string.latest);
//					}
//				}else{
//					str2 = getContext().getString(R.string.latest);
//				}
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		}
//		return str2;
//	}
//
	public static void rotate(ImageView ib_refresh) {
		RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setRepeatCount(Integer.MAX_VALUE);
		animation.setRepeatMode(Animation.RESTART);
		LinearInterpolator interpolator = new LinearInterpolator();
		animation.setInterpolator(interpolator);
		animation.setDuration(600);
		ib_refresh.startAnimation(animation);
	}
	public static void createToast1(final String text) {
	    UIUtils.runInMainThread(new Runnable() {
	        
	        @Override
	        public void run() {
	            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	public static void createToast1(final Context context, final String text) {
		UIUtils.runInMainThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});
	}
//	public static void createToast2(final Context context, final String text) {
//		UIUtils.runInMainThread(new Runnable() {
//
//			@Override
//			public void run() {
//				MToast.showToast(context,text, Toast.LENGTH_SHORT) ;
//			}
//		});
//	}
	
//	public static void createToast1(final Context context, String text, View v, boolean loadMore) {
//		View view = UIUtils.inflate(R.layout.pop);
//		view.measure(0, 0);
//		int diff = BaseApplication.screenWidth - view.getMeasuredWidth();
//		float px = BaseApplication.px;
//		createToast1(context,px+"");
//
////		PopupWindow popupWindow = new PopupWindow(view,
////                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
////		popupWindow.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bg_notify));
////			popupWindow.showAsDropDown(v, -13*px/2, 0);
//	}
	public static void setBackgroundColor(View view, int id) {
		view.setBackgroundColor(getContext().getResources().getColor(id));
	}
	public static void setTextcolor(TextView mtv, int id) {
		mtv.setTextColor(getContext().getResources().getColor(id));
	}
//	public static String transfer(long ct, long cacheT) {
//		if(cacheT==0){
//			return getContext().getString(R.string.last_refresh_time);
//		}
//		long time = (ct-cacheT)/60000;
//		String str2 = null;
//		if(time>=0){
//			 if(time<=1){
//				 str2 = getContext().getString(R.string.last_refresh_time);
//			 }else if(time>1&&time<60){
//				 str2 = getContext().getString(R.string.last_refresh)+time+getContext().getString(R.string.min_ago);
//			 }else if(time<24*60&&time>=60){
//				 str2 =getContext().getString(R.string.last_refresh)+time/(60)+getContext().getString(R.string.hour_ago);
//			 }else if(time<=7*24*60&&time>=24*60){
//				 str2 = getContext().getString(R.string.last_refresh)+time/(24*60)+getContext().getString(R.string.day_ago);
//			 }else if(time<=((4*7+2)*24*60)&&time>=7*24*60){
//				 str2 = getContext().getString(R.string.last_refresh) + time/(7*24*60)+getContext().getString(R.string.week_ago);
//			 }else if(time<=12*(4*7+2)*24*60&&time>=(4*7+2)*24*60){
//				 str2 = getContext().getString(R.string.last_refresh) + time/((4*7+2)*24*60) +getContext().getString(R.string.month_ago);
//			 }else {
//				 str2 = "long long ago";
//			 }
//		 }else{
//			 str2 = getContext().getString(R.string.last_refresh_time);
//		 }
//
//		return str2;
//	}
//	private static int arrD[] = {R.color.tuiguang_day, R.color.re_day, R.color.shiping_day, R.color.zhuanti_day, R.color.tu_day, R.color.jian_day, R.color.nuan_day};
//	private static int arrN[] = {R.color.tuiguang_night, R.color.re_night, R.color.shiping_night, R.color.zhuanti_night, R.color.tu_night, R.color.jian_night, R.color.nuan_night};
//	public static void setTextStyle(TextView tv, int i, String mode) {
//		if("night".equals(mode)){
//			switch (i) {
//			case 0:
//				setTextStyle(tv, getContext().getString(R.string.tuiguang), arrN[0], R.color.ziti_night);
//				break;
//			case 1:
//				setTextStyle(tv, getContext().getString(R.string.hot),arrN[1], R.color.ziti_night);
//				break;
//			case 2:
//				setTextStyle(tv,getContext().getString(R.string.video),arrN[2], R.color.ziti_night);
//				break;
//			case 3:
//				setTextStyle(tv, getContext().getString(R.string.topic),arrN[3], R.color.ziti_night);
//				break;
//			case 4:
//				setTextStyle(tv,  getContext().getString(R.string.pic),arrN[4], R.color.ziti_night);
//				break;
//			case 5:
//				setTextStyle(tv, getContext().getString(R.string.rec),arrN[5], R.color.ziti_night);
//				break;
//			case 6:
//				setTextStyle(tv, getContext().getString(R.string.warm),arrN[6], R.color.ziti_night);
//				break;
//			default:
//				break;
//			}
//		}else {
//			switch (i) {
//			case 0:
//				setTextStyle(tv, getContext().getString(R.string.tuiguang),arrD[0], R.color.ziti_day);
//				break;
//			case 1:
//				setTextStyle(tv, getContext().getString(R.string.hot),arrD[1], R.color.ziti_day);
//				break;
//			case 2:
//				setTextStyle(tv, getContext().getString(R.string.video),arrD[2], R.color.ziti_day);
//				break;
//			case 3:
//				setTextStyle(tv, getContext().getString(R.string.topic),arrD[3], R.color.ziti_day);
//				break;
//			case 4:
//				setTextStyle(tv,getContext().getString(R.string.pic),arrD[4], R.color.ziti_day);
//				break;
//			case 5:
//				setTextStyle(tv,getContext().getString(R.string.rec),arrD[5], R.color.ziti_day);
//				break;
//			case 6:
//				setTextStyle(tv, getContext().getString(R.string.warm),arrD[6], R.color.ziti_day);
//				break;
//			default:
//				break;
//			}
//		}
//
//	}
	public static void setTextStyle(TextView tv, String hot , int backgroudId , int zitiId) {
		tv.setText(hot);
		tv.setPadding(5, 0, 5, 0);
		tv.setTextColor(getContext().getResources().getColor(zitiId));
		tv.setBackgroundColor(getContext().getResources().getColor(backgroudId));
	}
	@SuppressWarnings("rawtypes")
	public static int getScreenHeightByVersion(Context app) {
		int height;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			height = ((Activity)app).getWindowManager().getDefaultDisplay().getHeight();
		}else{
			height = ((Activity)app).getWindowManager().getDefaultDisplay().getHeight()-getStatusBarHeight(app);
		}
		return height;
	}
	@SuppressWarnings("rawtypes")
	public static int getStatusBarHeight(Context app) {
		if(status_bar_height == -1)
			try{ 
				Class c = Class.forName("com.android.internal.R$dimen");
				Object obj = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = Integer.parseInt(field.get(obj).toString());
				status_bar_height = app.getResources().getDimensionPixelSize(x); 
			}catch(Exception e){
				
			}
		 return status_bar_height;
	}
//	public static void initSystemBar(Activity activity) {
//
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(activity, true);
//		}
//		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setStatusBarTintResource(R.color.transparent);
//		tintManager.setStatusBarDarkMode(true, activity);
//	}
//	public static void initSystemBar(Activity activity, int color) {
//
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(activity, true);
//		}
//		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setStatusBarTintColor(color);
////		tintManager.setStatusBarTintResource(R.color.transparent);
//		tintManager.setStatusBarDarkMode(true, activity);
//	}
//	public static void initSystemBarNormal(Activity activity) {
//
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(activity, true);
//		}
//		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//		tintManager.setStatusBarTintEnabled(true);
////			tintManager.setStatusBarDarkMode(true, activity);
//		tintManager.setStatusBarTintResource(R.color.new_login_color);
//		tintManager.setStatusBarDarkMode(true, activity);
//	}
//	public static void initSystemBar(Activity activity, int drawableRes, boolean textColorChange) {
//
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(activity, true);
//		}
//		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//		tintManager.setStatusBarTintEnabled(true);
//		// 使用颜色资源
//		tintManager.setStatusBarTintResource(drawableRes);
//		if(textColorChange)
//			tintManager.setStatusBarDarkMode(true, activity);
//		else{
//			tintManager.setStatusBarDarkMode(false, activity);
//		}
//	}

	@TargetApi(19)
	private static void setTranslucentStatus(Activity activity, boolean on) {

		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();

		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

		if (on) {
				winParams.flags |= bits;
			} else {
				winParams.flags &= ~bits;
			}
		win.setAttributes(winParams);
	}

	/**
     * 截取scrollview的屏幕
     * **/
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
//            scrollView.getChildAt(i).setBackgroundResource(R.drawable.bg3);
        }
//        Log.d("zb", "实际高度:" + h);
//        Log.d("zb", " 高度:" + scrollView.getHeight());
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h - UIUtils.dip2px(40),
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("/sdcard/screen_test.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return bitmap;
    }

	/**
	 * 获取圆角位图的方法
	 *
	 * @param bitmap
	 *            需要转化成圆角的位图
	 * @param pixels
	 *            圆角的度数，数值越大，圆角越大
	 * @return 处理后的圆角位图
	 */
	public static Bitmap toRoundCornerImage(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		// 抗锯齿
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

}
