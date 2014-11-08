package com.cwits.cyx_drive_sdk.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.util.Log;

import com.cwits.cyx_drive_sdk.ui.cyx_MyApplication;

/**  
 *   
 *   
 * UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的�?  
 *                           如果程序出现了未捕获异常默认情况下则会出现强行关闭对话框  
 *                           实现该接口并注册为程序中的默认未捕获异常处理   
 *                           这样当未捕获异常发生时，就可以做些异常处理操�? 
 *                           例如：收集异常信息，发�?错误报告 等�?  
 *   
 * UncaughtException处理�?当程序发生Uncaught异常的时�?由该类来接管程序,并记录发送错误报�?  
 */  
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";  
	public static final boolean DEBUG = true;  
	private static CrashHandler INSTANCE;  
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
    private CrashHandler() {  
    }  
    public static CrashHandler getInstance(){
    	if(INSTANCE==null)
    		INSTANCE=new CrashHandler();
    	return INSTANCE;
    }
    public void init() {  
      //获取系统默认的UncaughtException处理�?
    	mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler(); 
    	//设置该CrashHandler为程序的默认处理�? 
    	Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
    
    /** 
     * 当UncaughtException发生时会转入该函数来处理 
     */  
    @Override 
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {  
		    // 如果用户没有处理则让系统默认的异常处理器来处�? 
		    mDefaultHandler.uncaughtException(thread, ex);  
	     } else {  
			// Sleep�?��后结束程�? 
			// 来让线程停止�?��是为了显示Toast信息给用户，然后Kill程序  
			try {  
			     Thread.sleep(3000);  
			     } catch (InterruptedException e) {  
			         Log.e(TAG, "Error : ", e);  
		         }  
			 //打印错误信息
		     Log.e(TAG, showErrInfo(ex));
			   //�?��程序  
			     android.os.Process.killProcess(android.os.Process.myPid());  
			     System.exit(1);  
	      }  
	 }
    
    /** 
     * 自定义错误处�?收集错误信息 发�?错误报告等操作均在此完成. 
     *  
     * @param ex 
     * @return true:如果处理了该异常信息;否则返回false. 
     */  
	private boolean handleException(Throwable ex){
		if(ex==null){
			return false;
		}
		//保存日志文件   
        saveCrashInfo2File(ex);  
		return true;  
	}
	
	//将错误日志写入文�?
	private void saveCrashInfo2File(Throwable ex) {  
		 Writer info = new StringWriter();  
		 PrintWriter printWriter = new PrintWriter(info);
		 ex.printStackTrace(printWriter);  
		 Throwable cause = ex.getCause();  
		 while (cause != null) {  
			 cause.printStackTrace(printWriter);  
			 printWriter.close();  
			 cause = cause.getCause();  
		 }
		 String result = info.toString();  
		 result = "[" + LogUtil.getDateTime() + "]" + "CrashHandler " + result;
		 if(LogUtil.saveSingleLog(cyx_MyApplication.getInstance().getApplicationContext(), result)) {
			 Log.d("CrashHandler", "保存异常信息成功");
		 } else {
			 Log.d("CrashHandler", "保存异常信息失败");
		 }
	 }
	
	//显示错误信息方法
	  private String showErrInfo(Throwable arg1){
	    	 Writer writer = new StringWriter(); 
	         PrintWriter pw = new PrintWriter(writer); 
	    	 arg1.printStackTrace(pw); 
	    	 pw.close(); 
	    	 String error= writer.toString(); 
			 return error;
	     }
}
