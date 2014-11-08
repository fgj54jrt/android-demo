package com.cwits.cyx_drive_sdk.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cwits.cyx_drive_sdk.bean.PositionResult;
import com.cwits.cyx_drive_sdk.bean.StrokeResult;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.util.BMapUtil;
import com.cwits.cyx_drive_sdk.util.Coordinate;
import com.cwits.cyx_drive_sdk.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 行程历史详情界面
 * 
 * @author lxh
 * 
 */
public class cyx_TripHistoryMapActivity extends Activity {
	private ImageView btnBack;
	private MapView mMapView = null;
	private MapController mMapController = null;
	private GraphicsOverlay graphicsOverlay;// 绘制点�?线�?面的图层
	private ArrayList<OverlayItem> mItems = null;
	private MyOverlay mOverlay = null;
	private TextView chaosu_tv, jizhuanwan_tv, jishache_tv, jijiasu_tv,
			tv_pilaojiashi, tv_jibiandao;// 超速 急转弯 急刹车 急加速
	private TextView tv_drive_score; // 驾驶评分
	private TextView mileage_tv, duration_tv, ave_speed_tv, high_speed_tv; // 里程
																			// 驾驶时长
	private List<PositionResult> positoinList;
	// private int percent = 0; // 查询数据的百分比
	private String userId;
	private String stroke;
	private Handler handler;
	private StrokeResult strokeResult; // 行程数据
	private ProgressDialog mProgressDialog;
	private TextView title;
	private cyx_CustomAlertDialog mDialog;
	private int score;
	int mtotalPage = -1;
	String hintContent = "";
	boolean isExist = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (cyx_MyApplication.mBMapManager == null) {
			cyx_MyApplication.mBMapManager = new BMapManager(
					getApplicationContext());
			cyx_MyApplication.mBMapManager
					.init(new cyx_MyApplication.MyGeneralListener());
		}
		setContentView(MResource.getLayoutId(getApplicationContext(),
				"cyx_trip_hisory_map"));
		cyx_MyApplication.getInstance().addActivity(this);
		init();
	}

	private void init() {
		btnBack = (ImageView) findViewById(MResource.getID(
				getApplicationContext(), "btn_back"));
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cyx_TripHistoryMapActivity.this.finish();
			}
		});
		userId = CYX_Drive_SDK.getInstance().getUserInfo().getUserID();

		chaosu_tv = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "chaosu_tv"));
		jijiasu_tv = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "jijiasu_tv"));
		jishache_tv = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "jishache_tv"));
		mileage_tv = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "mileage_tv"));
		jizhuanwan_tv = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "jizhuanwan_tv"));
		tv_drive_score = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "tv_drive_score"));
		duration_tv = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "duration_tv"));
		ave_speed_tv = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "ave_speed_tv"));
		high_speed_tv = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "high_speed_tv"));
		title = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "TextTitle"));
		tv_jibiandao = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "tv_jibiandao"));
		tv_pilaojiashi = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "tv_pilaojiashi"));
		title.setText(getResources().getString(
				MResource.getStringId(getApplicationContext(), "tripdetial")));
		mMapView = (MapView) findViewById(MResource.getID(
				getApplicationContext(), "history_map"));
		mMapController = mMapView.getController();
		mMapController.setZoom(15);
		mMapController.enableClick(true);
		mMapController.setScrollGesturesEnabled(true);
		mMapView.setEnabled(true);
		SharedPreferences sh = getSharedPreferences("lastLocation",
				Activity.MODE_PRIVATE);
		String lat = sh.getString("lat", "");
		String lon = sh.getString("lon", "");
		mItems = new ArrayList<OverlayItem>();
		graphicsOverlay = new GraphicsOverlay(mMapView);
		mMapView.getOverlays().add(graphicsOverlay);
		mOverlay = new MyOverlay(getResources().getDrawable(
				MResource.getDrawableId(getApplicationContext(), "ic_click")),
				mMapView);
		mMapView.getOverlays().add(mOverlay);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(getResources().getString(
				MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getResources().getString(
				MResource.getStringId(getApplicationContext(), "wait")));
		mProgressDialog.setCanceledOnTouchOutside(false);
		handler = new Handler();
		initInfo();
		if (strokeResult != null && strokeResult.getStartlat() != 0
				&& strokeResult.getStartlon() != 0) {
			mMapController.animateTo(new GeoPoint((int) (strokeResult
					.getStartlat() * 1e6),
					(int) (strokeResult.getStartlon() * 1e6)));
		} else {
			if (!TextUtils.isEmpty(lon) && !TextUtils.isEmpty(lat)) {
				GeoPoint point = new GeoPoint(
						(int) (Double.parseDouble(lat) * 1e6),
						(int) (Double.parseDouble(lon) * 1e6));
				mMapController.animateTo(point);
			}
		}
		showInfo();
	}

	private void initInfo() {

		String jsonStr = getIntent().getStringExtra("jsonStr");
		System.out.println("------------jsonStr:" + jsonStr);
		if (!TextUtils.isEmpty(jsonStr)) {
			try {
				JSONObject jsonObj = new JSONObject(jsonStr);
				stroke = jsonObj.optString("rid");
				score = jsonObj.optInt("score");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!TextUtils.isEmpty(stroke)) {
			List<StrokeResult> strokeList = new ArrayList<StrokeResult>();
			if (cyx_MyApplication.getStrokeDataList() != null
					&& cyx_MyApplication.getStrokeDataList().size() > 0) {
				strokeList = cyx_MyApplication.getStrokeDataList();
				for (int i = 0; i < cyx_MyApplication.getStrokeDataList()
						.size(); i++) {
					if (strokeList.get(i).getStroke().equals(stroke)) {
						strokeResult = strokeList.get(i);
					}
				}
			} else {
				// request(1);
				Toast.makeText(cyx_TripHistoryMapActivity.this,
						"行程信息有误，请重新查询！", Toast.LENGTH_SHORT).show();
			}
			if (strokeResult != null) {
				request(1);
			}
		}
	}

	private void showInfo() {
		// 展示数据
		if (strokeResult != null) {
			jijiasu_tv.setText(strokeResult.getAece() + "");
			jishache_tv.setText(strokeResult.getDece() + "");
			jizhuanwan_tv.setText(strokeResult.getTurns() + "");
			duration_tv.setText(TimeUtil.getHoueAndMinute(strokeResult
					.getTimes() * 1000));
			mileage_tv.setText(formatMileage(strokeResult.getMileages() / 1000)
					+ "km");
			ave_speed_tv.setText(formatSpeed(strokeResult.getAve()) + "km/h");
			high_speed_tv.setText(formatSpeed(strokeResult.getMax()) + "km/h");
			tv_drive_score.setText(score + "");
			chaosu_tv.setText(strokeResult.getSpeeds() + "");
			tv_pilaojiashi.setText(strokeResult.getTires() + "");
			tv_jibiandao.setText(strokeResult.getChanges() + "");
		} else {
			jijiasu_tv.setText("0");
			jishache_tv.setText("0");
			jizhuanwan_tv.setText("0");
			duration_tv.setText("0");
			mileage_tv.setText("0km");
			ave_speed_tv.setText("0km/h");
			high_speed_tv.setText("0km/h");
			tv_drive_score.setText("0");
			chaosu_tv.setText("0");
			tv_pilaojiashi.setText("0");
			tv_jibiandao.setText("0");
		}

	}

	private void request(final int page) {
		mProgressDialog.show();
		CYX_Drive_SDK
				.getInstance()
				.getConnection()
				.sendExtData(
						new ExtraDataProcess().getQueryStrokeDetailData(userId,
								stroke, strokeResult.getBegin(),
								strokeResult.getEnd(), page),
						new MyRequestcallback());
	}

	private Runnable mtimeoutDialog = new Runnable() {
		@Override
		public void run() {
			if (null != mProgressDialog && mProgressDialog.isShowing()) {
				mProgressDialog.cancel();
			}
			creatDialog(getString(MResource.getStringId(
					getApplicationContext(), "queryTimeOut")));
		}
	};

	class MyRequestcallback extends RequestCallback {
		@Override
		public void onSuccess(String bizJsonData) {
			if (cyx_MyApplication
					.getInstance()
					.getActivityStack()
					.lastElement()
					.getClass()
					.getName()
					.equals(cyx_TripHistoryMapActivity.this.getClass()
							.getName())) {
				JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(bizJsonData);
					String hises = jsonObj.optString("hises");
					int nowPage = jsonObj.optInt("nowpage");
					int resultCode = jsonObj.getInt("result");
					Gson gson = new Gson();
					List<PositionResult> postionResult = new ArrayList<PositionResult>();
					if (!TextUtils.isEmpty(hises))
						postionResult = gson.fromJson(hises,
								new TypeToken<List<PositionResult>>() {
								}.getType());
					String stroke = jsonObj.optString("stroke");
					String pagenum = jsonObj.optString("pagenum");
					int totalPage = 0;
					if (!(TextUtils.isEmpty(pagenum))) {
						totalPage = Integer.parseInt(pagenum);
					} else {
						totalPage = -1;
					}
					switch (resultCode) {
					case ConstantContext.SUCCESS:
						if (1 == nowPage) {
							positoinList = new ArrayList<PositionResult>();
							mtotalPage = totalPage;
						}
						Log.i("lxh", "---------nowPage:" + nowPage
								+ "   totalPage:" + mtotalPage);
						// percent = (int) nowPage * 100 / mtotalPage;
						double[] baidus;
						if (postionResult != null && postionResult.size() > 0) {

							for (int i = 0; i < postionResult.size(); i++) {
								PositionResult result = new PositionResult();
								result.setDirection(postionResult.get(i)
										.getDirection());
								result.setHeight(postionResult.get(i)
										.getHeight());
								baidus = Coordinate.wgtobaidu(postionResult
										.get(i).getLon(), postionResult.get(i)
										.getLat());
								result.setLat(baidus[1]);
								result.setLon(baidus[0]);
								result.setSpeed(postionResult.get(i).getSpeed());
								result.setTime(postionResult.get(i).getTime());
								result.setType(postionResult.get(i).getType());
								result.setTer_id(postionResult.get(i)
										.getTer_id());
								result.setState(postionResult.get(i).getState());
								result.setStroke(stroke);
								positoinList.add(result);
							}
						}
						if (nowPage < mtotalPage) {
							request(nowPage + 1);
						} else if (nowPage == mtotalPage) {
							// 查询结束
							remove();
							if (positoinList != null && positoinList.size() > 0) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										Log.i("lxh", "received history over");
										if (mProgressDialog != null
												&& mProgressDialog.isShowing())
											mProgressDialog.dismiss();
										Toast.makeText(
												cyx_TripHistoryMapActivity.this,
												"查询完毕", Toast.LENGTH_SHORT)
												.show();
										Log.i("lxh",
												"------received history over positoinList:  "
														+ positoinList.size());
										setmark(positoinList);
										drawLocationLine(positoinList);
										addDBOverlay(positoinList);
										setmark(positoinList);
										mMapView.refresh();
									}
								});

							}
						} else if (nowPage == -1 || (nowPage < totalPage)) {
							return;
						}

						break;
					case ConstantContext.ERROR_1:
						if (mProgressDialog != null
								&& mProgressDialog.isShowing())
							mProgressDialog.dismiss();
						creatDialog("设备内部编号有错");
						break;
					case ConstantContext.ERROR_2:
						if (mProgressDialog != null
								&& mProgressDialog.isShowing())
							mProgressDialog.dismiss();
						creatDialog("行程编号有错");
						break;
					case ConstantContext.ERROR_3:
						if (mProgressDialog != null
								&& mProgressDialog.isShowing())
							mProgressDialog.dismiss();
						creatDialog("开始时间有错	");
						break;
					case ConstantContext.ERROR_4:
						if (mProgressDialog != null
								&& mProgressDialog.isShowing())
							mProgressDialog.dismiss();
						creatDialog("结束时间有错");
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}
			}
		}

		@Override
		public void onFailed(int reason) {
			if (cyx_MyApplication
					.getInstance()
					.getActivityStack()
					.lastElement()
					.getClass()
					.getName()
					.equals(cyx_TripHistoryMapActivity.this.getClass()
							.getName())) {
				switch (reason) {
				case RequestCallback.REASON_NO_NETWORK:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(),
									"network_switch_off"));
					break;
				case RequestCallback.REASON_NO_SIGNAL:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(),
									"no_network_signal"));
					break;
				case RequestCallback.REASON_NOT_AUTHENTICATED:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(),
									"no_auth"));
					break;
				case RequestCallback.REASON_TIMEOUT:
					if (mtimeoutDialog != null)
						handler.removeCallbacks(mtimeoutDialog);
					handler.post(mtimeoutDialog);
					break;
				case RequestCallback.REASON_DATA_INCRECT:
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(),
									"data_increct"));
					break;
				}
				showHintContent();
			}
		}
	}

	private void showHintContent() {
		if (!TextUtils.isEmpty(hintContent)) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (mProgressDialog != null && mProgressDialog.isShowing())
						mProgressDialog.dismiss();
					Toast.makeText(cyx_TripHistoryMapActivity.this,
							hintContent, Toast.LENGTH_SHORT).show();
					hintContent = "";
				}
			});
		}
	}

	private void creatDialog(String message) {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		mDialog = new cyx_CustomAlertDialog(cyx_TripHistoryMapActivity.this);
		mDialog.setTitle(MResource.getStringId(getApplicationContext(),
				"notice"));
		mDialog.setMessage(message);
		mDialog.setNumberVisible(false);
		mDialog.setPositiveButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
			}
		});
	}

	// 标记起点和终点
	private void setmark(List<PositionResult> list) {
		if (list != null && list.size() > 1) {
			if (list.get(0).getState() == 1) {
				GeoPoint start_point = new GeoPoint(
						(int) (list.get(0).getLat() * 1e6), (int) (list.get(0)
								.getLon() * 1e6));
				OverlayItem itemStart = new OverlayItem(start_point, "", "");
				itemStart.setMarker(getResources().getDrawable(
						MResource.getDrawableId(getApplicationContext(),
								"ic_flag_start")));
				mOverlay.addItem(itemStart);
				mMapController.animateTo(new GeoPoint((int) (list.get(0)
						.getLat() * 1e6), (int) (list.get(0).getLon() * 1e6)));
			}
			if (list.get(list.size() - 1).getState() == 1) {
				GeoPoint end_point = new GeoPoint((int) (list.get(
						list.size() - 1).getLat() * 1e6), (int) (list.get(
						list.size() - 1).getLon() * 1e6));
				OverlayItem itemEnd = new OverlayItem(end_point, "", "");
				itemEnd.setMarker(getResources().getDrawable(
						MResource.getDrawableId(getApplicationContext(),
								"ic_flag_end")));
				mOverlay.addItem(itemEnd);
			}
			if (mOverlay.getAllItem() != null
					&& mOverlay.getAllItem().size() > 0)
				mItems.addAll(mOverlay.getAllItem());
			mMapView.refresh();
		}
	}

	// 绘制行程路线
	private void drawLocationLine(List<PositionResult> results) {
		if (results != null && results.size() > 1) {
			List<GeoPoint> pointList = new ArrayList<GeoPoint>();
			for (int i = 0; i < results.size(); i++) {
				if (results.get(i).getState() == 1) {
					pointList.add(new GeoPoint(
							(int) (results.get(i).getLat() * 1e6),
							(int) (results.get(i).getLon() * 1e6)));

				}
			}
			GeoPoint[] linePoints = pointList.toArray(new GeoPoint[pointList
					.size()]);
			if (linePoints != null && linePoints.length > 1) {
				Geometry lineGeometry = new Geometry();
				lineGeometry.setPolyLine(linePoints);
				Symbol lineSymbol = new Symbol();
				Symbol.Color lineColor = lineSymbol.new Color();// 颜色样式
				lineColor.red = 52;
				lineColor.green = 182;
				lineColor.blue = 122;
				lineColor.alpha = 255;
				lineSymbol.setLineSymbol(lineColor, BMapUtil.dp2px(this, 4));// 设置图形的颜色和大小
				Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);// 构几何图形
				graphicsOverlay.setData(lineGraphic);// 添加�?图形
				mMapView.refresh();// 刷新地图
			}
		} else {
			return;
		}
	}

	// 添加驾驶行为标记
	private void addDBOverlay(List<PositionResult> result) {
		if (result != null && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				if (result.get(i).getState() == 1) {
					GeoPoint point = new GeoPoint(
							(int) (result.get(i).getLat() * 1000000),
							(int) (result.get(i).getLon() * 1000000));
					int type = result.get(i).getType();
					switch (type) {
					// 急加速
					case 6:
						OverlayItem item = new OverlayItem(point, "", "");
						item.setMarker(getResources().getDrawable(
								MResource.getDrawableId(
										getApplicationContext(), "jijiasu")));
						mOverlay.addItem(item);
						break;
					// 急转弯
					case 7:
						OverlayItem item3 = new OverlayItem(point, "", "");
						item3.setMarker(getResources().getDrawable(
								MResource.getDrawableId(
										getApplicationContext(), "jizhuanwan")));
						mOverlay.addItem(item3);
						break;
					// 急减速
					case 8:
						OverlayItem item2 = new OverlayItem(point, "", "");
						item2.setMarker(getResources().getDrawable(
								MResource.getDrawableId(
										getApplicationContext(), "jishache")));
						mOverlay.addItem(item2);
						break;
						// 超速
					case 22:
						OverlayItem item4 = new OverlayItem(point, "", "");
						item4.setMarker(getResources().getDrawable(
								MResource.getDrawableId(
										getApplicationContext(), "chaosu")));
						mOverlay.addItem(item4);
						break;
						// 疲劳驾驶
					case 21:
						OverlayItem item5 = new OverlayItem(point, "", "");
						item5.setMarker(getResources().getDrawable(
								MResource.getDrawableId(
										getApplicationContext(), "pilaojiashi")));
						mOverlay.addItem(item5);
						break;
						// 急变道
					case 23:
						OverlayItem item6 = new OverlayItem(point, "", "");
						item6.setMarker(getResources().getDrawable(
								MResource.getDrawableId(
										getApplicationContext(), "jibiandao")));
						mOverlay.addItem(item6);
						break;
					}
				}
			}
			if (mOverlay.getAllItem() != null
					&& mOverlay.getAllItem().size() > 0)
				mItems.addAll(mOverlay.getAllItem());
			mMapView.refresh();
		}
	}

	public class MyOverlay extends ItemizedOverlay<OverlayItem> {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			return false;
		}

	}

	@Override
	protected void onPause() {
		// tripQuery.removeRecedListener(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// tripQuery.addReceivedListener(this);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		remove();
		mMapView.getOverlays().clear();
		if (mMapView != null)
			mMapView.destroy();
		// cyx_MyApplication.getStrokeDataList().clear();
		cyx_MyApplication.getInstance().removeActivity(this);
	}

	private void remove() {
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		if (mtimeoutDialog != null)
			handler.removeCallbacks(mtimeoutDialog);
	}

	// 格式化里程
	private double formatMileage(double mileage) {
		DecimalFormat df1 = new DecimalFormat("#0.0");
		if (mileage > 0)
			return Double.valueOf(df1.format(mileage));
		return 0;
	}

	private float formatSpeed(float speed) {
		DecimalFormat df1 = new DecimalFormat("#0.0");
		if (speed > 0)
			return Float.valueOf(df1.format(speed));
		return 0;
	}
}
