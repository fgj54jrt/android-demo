package com.cwits.cyx_drive_sdk.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;
import com.cwits.cyx_drive_sdk.widget.zxing.camera.CameraManager;
import com.cwits.cyx_drive_sdk.widget.zxing.decoding.CaptureActivityHandler;
import com.cwits.cyx_drive_sdk.widget.zxing.decoding.InactivityTimer;
import com.cwits.cyx_drive_sdk.widget.zxing.decoding.RGBLuminanceSource;
import com.cwits.cyx_drive_sdk.widget.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
/**
 * Initial the camera
 * @author Ryan.Tang
 */
public class cyx_CaptureBindingActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private cyx_CustomAlertDialog mDialog;
	private String[] mResult = null;
	private ImageView img_back, light_btn, photo_btn;
	private boolean isLightOpen = false;
	private Camera  camera;
	private Parameters parameter;
	private TextView light_tv, title;
	private static final int REQUEST_CODE = 100;
	private static final int PARSE_BARCODE_SUC = 300;
	private static final int PARSE_BARCODE_FAIL = 303;
	private String photo_path;
	private ProgressDialog mProgressDialog; 
	private Bitmap scanBitmap;
	private UserInfo userInfo;
	private String hintContent = "";
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getLayoutId(getApplicationContext(), "cyx_capture"));
		cyx_MyApplication.getInstance().addActivity(this);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(MResource.getID(getApplicationContext(), "viewfinder_view"));
		light_tv = (TextView) findViewById(MResource.getID(getApplicationContext(), "light_tv"));
		
		title = (TextView)findViewById(MResource.getID(getApplicationContext(), "TextTitle"));
		title.setText(MResource.getStringId(getApplicationContext(), "capture"));
		
		img_back = (ImageView) findViewById(MResource.getID(getApplicationContext(), "btn_back"));
		img_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cyx_CaptureBindingActivity.this.finish();
			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		
		light_btn = (ImageView) findViewById(MResource.getID(getApplicationContext(), "light_btn"));
		light_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isLightOpen) {
					openLight();
					light_tv.setText(MResource.getStringId(getApplicationContext(), "light_off"));
					light_btn.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "light_on"));
				} else {
					closeLight();
					light_tv.setText(MResource.getStringId(getApplicationContext(), "light_up"));
					light_btn.setBackgroundResource(MResource.getDrawableId(getApplicationContext(), "light_off"));
				}
				isLightOpen = !isLightOpen;
			}
		});
		photo_btn = (ImageView) findViewById(MResource.getID(getApplicationContext(), "photo_btn"));
		photo_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openPhotoAlbum();
			}
		});
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(getResources().getString(MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "wait")));
		mProgressDialog.setCanceledOnTouchOutside(false);
		
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(mProgressDialog!=null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			switch (msg.what) {
			case PARSE_BARCODE_SUC:
				if(TextUtils.isEmpty((String)msg.obj)){
					Toast.makeText(cyx_CaptureBindingActivity.this, MResource.getStringId(getApplicationContext(), "scan_fail"), Toast.LENGTH_SHORT).show();
					return;
				}
				mResult = analyticalData((String)msg.obj);
				createDialog();
				break;
			case PARSE_BARCODE_FAIL:
				Toast.makeText(cyx_CaptureBindingActivity.this, MResource.getStringId(getApplicationContext(), "scan_fail"), Toast.LENGTH_LONG).show();
				break;

			}
		}
		
	};

	@Override
	protected void onResume() {
		super.onResume();
		surfaceView = (SurfaceView) findViewById(MResource.getID(getApplicationContext(), "preview_view"));
		surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		if(scanBitmap!=null && !scanBitmap.isRecycled()) {
			scanBitmap.recycle();
			scanBitmap = null;
		}
		inactivityTimer.shutdown();
		cyx_MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	
	/**
	 * 处理扫描结果
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(cyx_CaptureBindingActivity.this, MResource.getStringId(getApplicationContext(), "scan_fail"), Toast.LENGTH_SHORT).show();
		}else {
			mResult = analyticalData(resultString);
			if (null != mResult && null != mResult[0] && null != mResult[1]) {
				createDialog();
			} else {
				Toast.makeText(cyx_CaptureBindingActivity.this, MResource.getStringId(getApplicationContext(), "parse_error"), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					MResource.getRawID(getApplicationContext(), "beep"));
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	
	//自定义对话框
    private void createDialog(){
    	if(mDialog!=null&&mDialog.isShowing()){
    		mDialog.dismiss();
    		mDialog=null;
    	}
    	mDialog = new cyx_CustomAlertDialog(cyx_CaptureBindingActivity.this);
		mDialog.setTitle(MResource.getStringId(getApplicationContext(), "notice"));
		mDialog.setMessage(getResources().getString(MResource.getStringId(getApplicationContext(), "if_binding")));
		mDialog.setNumberVisible(false);
		mDialog.showLine();
		mDialog.setNegativeButton(getResources().getString
				(MResource.getStringId(getApplicationContext(), "cancel")), new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
						mDialog = null ;
						cyx_CaptureBindingActivity.this.finish();
					}
				});
		mDialog.setPositiveButton(getResources().getString(MResource.getStringId(getApplicationContext(), "ensure")), new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mDialog.dismiss();
						mDialog = null;
						if(mProgressDialog!=null && !mProgressDialog.isShowing())
							mProgressDialog.show();
						Connection.getInstance().sendExtData(new ExtraDataProcess().bindingCXB(userInfo.getUserID(), mResult[0], mResult[1]), new RequestCallback() {

							@Override
							public void onSuccess(String bizJsonData) {
								// TODO Auto-generated method stub
								try {
									JSONObject jsonObj = new JSONObject(bizJsonData);
									int resultCode = jsonObj.getInt("result");
									switch (resultCode) {
									case ConstantContext.SUCCESS:
										if(mProgressDialog!=null&&mProgressDialog.isShowing())
											mProgressDialog.dismiss();
										Toast.makeText(cyx_CaptureBindingActivity.this,getResources().getString(MResource.getStringId(getApplicationContext(),"binding_success" )),Toast.LENGTH_SHORT).show();
										CYX_Drive_SDK.getInstance().getUserManager().setUserFlag(userInfo.getName(), 2);	//更新用户信息
										userInfo.setFlag(2);
										cyx_CaptureBindingActivity.this.finish();
										break;
									//userid为空
									case ConstantContext.ERROR_1:
										hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "user_id_null"));
										break;
										//pn为空
									case ConstantContext.ERROR_2:
										hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "pn_null_error"));		
										break;
										//sn为空
									case ConstantContext.ERROR_3:
										hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "sn_null_error"));
										break;
										//用户不合法
									case ConstantContext.ERROR_4:
										hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "user_error"));
										break;
										//用户为非正式用户
									case ConstantContext.ERROR_5:
										hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "informality_user_error"));
										break;
										//用户已绑定其他设备
									case ConstantContext.ERROR_6:
										hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "already_binding_device"));
										break;
										//设备已被绑定
									case ConstantContext.ERROR_7:
										hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "already_binding"));
										break;
										// pn或sn不正确
									case ConstantContext.ERROR_8:
										hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "pn_sn_wrong_error"));
										break;
										//程序错误
									case ConstantContext.ERROR_9:
										hintContent = getResources().getString(MResource.getStringId(getApplicationContext(), "procedure_error"));
										break;
									}
									showHintContent();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							@Override
							public void onFailed(int reason) {
								switch(reason) {
								case RequestCallback.REASON_NO_NETWORK:
									hintContent += getResources().getString(
											MResource.getStringId(getApplicationContext(), "network_switch_off"));
									break;
								case RequestCallback.REASON_NO_SIGNAL:
									hintContent += getResources().getString(
											MResource.getStringId(getApplicationContext(), "network_error"));
									break;
								case RequestCallback.REASON_NOT_AUTHENTICATED:
									hintContent += getResources().getString(
											MResource.getStringId(getApplicationContext(), "no_auth"));
									break;
								case RequestCallback.REASON_TIMEOUT:
									hintContent += getResources().getString(
											MResource.getStringId(getApplicationContext(), "request_timeOut"));
									break;
								case RequestCallback.REASON_DATA_INCRECT:
									hintContent += getResources().getString(
											MResource.getStringId(getApplicationContext(), "data_increct"));
									break;
							}
							showHintContent();
							}
						});
					}
				});
    }
    
	// 解析二维码数据
	private String[] analyticalData(String data) {

		String[] sum = new String[2];
		// 首先对长度进行判断
		if (null == data)
			return null;
		int lastIndex = 0;
		ArrayList<String> list = new ArrayList<String>();
		while (lastIndex < data.length()) {
			int firstStartIndex = data.indexOf('[', lastIndex);
			if (-1 != firstStartIndex) {
				lastIndex = firstStartIndex;
				int firstEndIndex = data.indexOf(']', lastIndex);
				if (-1 != firstEndIndex) {
					lastIndex = firstEndIndex;
					list.add(data.substring(firstStartIndex + 1, firstEndIndex));
				} else
					break;

			} else
				break;

		}
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).length() < 6 || list.get(i).length()>100) {
					break;
				} else {
					int s1 = list.get(i).indexOf("IMEI:", 0);
					int s2 = list.get(i).indexOf("ICCID:", 0);
					int s3 = list.get(i).indexOf("PN:", 0);
					int s4 = list.get(i).indexOf("SN:", 0);
					if (-1 != s1
							/*&& (list.get(i).length() == 20 || list.get(i)
									.length() == 21)*/) {
						sum[0] = list.get(i).substring((s1 + 5),
								list.get(i).length());
					} else if (-1 != s2 /*&& list.get(i).length() == 26*/) {
						sum[1] = list.get(i).substring((s2 + 6),
								list.get(i).length());
					}else if(-1 != s3){
						sum[0] = list.get(i).substring(s3+3,list.get(i).length());
					}else if(-1 != s4){
						sum[1] = list.get(i).substring(s4+3,list.get(i).length());
					}
					
				}

			}
		}
		return sum;
	}
	
	private void openLight() {
		camera = CameraManager.get().getCamera();
		Parameters parameter = camera.getParameters();  
		parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(parameter);
	}
	
	private void closeLight() {
		parameter = camera.getParameters();  
		parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(parameter);
	}
	
	private void openPhotoAlbum() {
		//打开手机中的相册
		Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
        this.startActivityForResult(wrapperIntent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch(requestCode){
			case REQUEST_CODE:
				//获取选中图片的路径
				Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
				if (cursor.moveToFirst()) {
					photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				}
				cursor.close();
				if(mProgressDialog!=null && !mProgressDialog.isShowing())
					mProgressDialog.show();
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						Result result = scanningImage(photo_path);
						if (result != null) {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_SUC;
							m.obj = result.getText();
							mHandler.sendMessage(m);
						} else {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_FAIL;
							m.obj = "Scan failed!";
							mHandler.sendMessage(m);
						}
					}
				}).start();
				
				break;
			
			}
		}
	}
	
	/**
	 * 扫描二维码图片的方法
	 * @param path
	 * @return
	 */
	public Result scanningImage(String path) {
		if(TextUtils.isEmpty(path)){
			return null;
		}
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);

		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void showHintContent() {
		if(!TextUtils.isEmpty(hintContent)) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mProgressDialog!=null&&mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_CaptureBindingActivity.this,hintContent,Toast.LENGTH_SHORT).show(); 
					hintContent = "";
					continuePreview();
				}
			});
		}
	}
	
	/**
	 * 重复扫描
	 */
	private void continuePreview(){
		if (hasSurface) 
			initCamera(surfaceHolder);
	      if (handler != null)
	           handler.restartPreviewAndDecode();   
	 }
}