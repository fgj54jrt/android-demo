package com.cwits.cyx_drive_sdk.ui;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwits.cyx_drive_sdk.bean.ARAwardInfo;
import com.cwits.cyx_drive_sdk.bean.ARAwardInfo.AwardData;
import com.cwits.cyx_drive_sdk.bean.StrokeResult;
import com.cwits.cyx_drive_sdk.connection.Connection;
import com.cwits.cyx_drive_sdk.connection.RequestCallback;
import com.cwits.cyx_drive_sdk.data.ConstantContext;
import com.cwits.cyx_drive_sdk.data.ExtraDataProcess;
import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;
import com.cwits.cyx_drive_sdk.userInfo.IExternalInterfaceAR;
import com.cwits.cyx_drive_sdk.userInfo.ITripMark;
import com.cwits.cyx_drive_sdk.userInfo.UserInfo;
import com.cwits.cyx_drive_sdk.util.BMapUtil;
import com.cwits.cyx_drive_sdk.util.TimeUtil;
import com.cwits.cyx_drive_sdk.widget.xlistview.XListView;
import com.cwits.cyx_drive_sdk.widget.xlistview.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class cyx_DriveRecodeActivity extends Activity implements
		IXListViewListener, ITripMark {
	private TextView title;
	private XListView listView;
	List<StrokeResult> mDataList;
	List<ARAwardInfo> mARAwardInfoList;
	private MyAdapter adapter;
	private ProgressDialog mProgressDialog;
	Handler mHandler;
	private UserInfo userInfo;
	private String hintContent = "";
	private String[] ids;
	private List<String> idsList;
	private TextView null_data;
	private IExternalInterfaceAR mIExternalInterfaceAR;
	private ImageView back_btn;
	private GetAddressStrRunnable getAddressStrRunnable;
	private boolean stop = false; // 线程停止
	private int flag = 1; // 上下拉标志，0上拉（刷新），1下拉（更多）
	private boolean isFirstSearch = true;
	// 定义搜索服务类
	private JSONObject dataObject, mJSONObject;
	private String area;
	private StrokeResult strokeResult;
	private ARAwardInfo arAwardInfo;
	private Gson gson;
	private int isSimulate;// 是否为模拟数据
	private View tips;
	private List<StrokeResult> strokes = null; // 每次请求服务端返回的行程数据列表
	private List<ARAwardInfo> infoList = null; // 每次请求服务端返回的奖励数据列表
	private Animation showAction, hideAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		cyx_MyApplication.getInstance().addActivity(this);
		setContentView(MResource.getLayoutId(getApplicationContext(),
				"cyx_drive_recode"));

		isSimulate = getIntent().getIntExtra("simulate", 1);

		title = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "TextTitle"));
		title.setText(MResource.getStringId(getApplicationContext(),
				"drive_recode"));

		null_data = (TextView) findViewById(MResource.getID(
				getApplicationContext(), "null_data"));

		tips = findViewById(MResource.getID(getApplicationContext(),
				"tip_layout"));

		back_btn = (ImageView) findViewById(MResource.getID(
				getApplicationContext(), "btn_back"));
		back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mIExternalInterfaceAR != null)
					mIExternalInterfaceAR.clickOnBack(0);
				cyx_DriveRecodeActivity.this.finish();
			}
		});

		listView = (XListView) findViewById(MResource.getID(
				getApplicationContext(), "listView"));
		listView.setPullLoadEnable(true);
		listView.setXListViewListener(this);
		init();
	}

	private void init() {
		cyx_MyApplication.getStrokeDataList().clear();
		mHandler = new Handler();
		getAddressStrRunnable = new GetAddressStrRunnable();
		new Thread(getAddressStrRunnable).start();
		gson = new Gson();
		userInfo = CYX_Drive_SDK.getInstance().getUserInfo();
		area = userInfo.getArea();
		if (TextUtils.isEmpty(area))
			area = "30";
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(getResources().getString(
				MResource.getStringId(getApplicationContext(), "notice")));
		mProgressDialog.setMessage(getResources().getString(
				MResource.getStringId(getApplicationContext(), "wait")));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mDataList = new LinkedList<StrokeResult>();
		mARAwardInfoList = new LinkedList<ARAwardInfo>();
		adapter = new MyAdapter(this);
		listView.setAdapter(adapter);
		idsList = new LinkedList<String>();
		mIExternalInterfaceAR = CYX_Drive_SDK.getInstance()
				.getExternalInterface();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				strokeResult = mDataList.get(position - 1);
				for (int i = 0, size = mARAwardInfoList.size(); i < size; i++) {
					if (mARAwardInfoList.get(i).getRid()
							.equals(strokeResult.getStroke())) {
						arAwardInfo = mARAwardInfoList.get(i);
					}
				}
				mJSONObject = new JSONObject();
				dataObject = new JSONObject();
				try {
					if (isSimulate == 1) {
						dataObject.put("area", area);
						dataObject.put("flag", 2);// int32，用户标志，0，临时账号，1，正式账号，2，VIP账号
						dataObject.put("rid", strokeResult.getStroke());// 行程id
						dataObject.put("len", strokeResult.getMileages());// 单位米，总里程数。
						dataObject.put("tm", strokeResult.getTimes());// int32，单位秒，行驶耗时。
						dataObject.put("avgspd", strokeResult.getAve());// int32，单位米/秒，平均速度。
						dataObject.put("mxspd", strokeResult.getMax());// 单位米/秒，最大速度
						dataObject.put("stm", strokeResult.getBegin());// 单位秒，行程开始时间戳
						dataObject.put("etm", strokeResult.getEnd());// 单位秒，行程结束时间戳
						dataObject.put("slo", strokeResult.getStartlon());// 行程开始经纬度
						dataObject.put("sla", strokeResult.getStartlat());//
						dataObject.put("elo", strokeResult.getEndlon());//
						dataObject.put("ela", strokeResult.getEndlat());
						dataObject.put("acc", strokeResult.getAece());// 本行程急加速次数总和
						dataObject.put("dec", strokeResult.getDece());// 本行程急减速次数总和
						dataObject.put("turn", strokeResult.getTurns());// 本行程急转弯次数总和
						dataObject.put("over", strokeResult.getSpeeds());// 本行程超速次数总和。
						dataObject.put("slide", strokeResult.getChanges());// 本行程急变道次数总和。
						dataObject.put("tired", strokeResult.getTires());// 本行程疲劳驾驶次数总和。
						dataObject.put("pz", changeListToArray(arAwardInfo)); // 金币
						dataObject.put("sc", arAwardInfo == null ? 0
								: arAwardInfo.getSc()); // 得分
						dataObject.put("gt", arAwardInfo == null ? 1
								: arAwardInfo.getGt()); // 是否领奖
					} else {
						dataObject.put("area", "30"); // int32，用户区域。
						dataObject.put("flag", userInfo.getFlag());// int32，用户标志，0，临时账号，1，正式账号，2，VIP账号
						dataObject.put("rid", cyx_MyApplication
								.getStrokeDataList().get(0).getStroke());// 行程id
						dataObject.put("len", 30000);// 单位米，总里程数。
						dataObject.put("tm", 2000);// int32，单位秒，行驶耗时。
						dataObject.put("avgspd", 60);// int32，单位米/秒，平均速度。
						dataObject.put("mxspd", 120);// 单位米/秒，最大速度
						dataObject.put("stm", cyx_MyApplication
								.getStrokeDataList().get(0).getBegin());// 单位秒，行程开始时间戳
						dataObject.put("etm", cyx_MyApplication
								.getStrokeDataList().get(0).getEnd());// 单位秒，行程结束时间戳
						dataObject.put("slo", 120.00);// 行程开始经纬度
						dataObject.put("sla", 25.0);//
						dataObject.put("elo", 122.00);//
						dataObject.put("ela", 15.00);
						dataObject.put("acc", 1);// 本行程急加速次数总和
						dataObject.put("dec", 0);// 本行程急减速次数总和
						dataObject.put("turn", 1);// 本行程急转弯次数总和
						dataObject.put("over", 0);// 本行程超速次数总和。
						dataObject.put("slide", 0);// 本行程急变道次数总和。
						dataObject.put("tired", 0);// 本行程疲劳驾驶次数总和。
						dataObject.put("pz", getpzListSimulate()); // 金币
						dataObject.put("sc", 80); // 得分
						dataObject.put("gt", 0); // 是否领奖
					}
					dataObject.put("id", userInfo.getUserID());
					mJSONObject.put("data", dataObject);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 if(CYX_Drive_SDK.getInstance().getSdkFlag()==1){
					 String jsonStr = "{ 'rid':" + strokeResult.getStroke() + ","
								+ "   'score':80," + " 'got':0}";
					 CYX_Drive_SDK.getInstance().startToTripDetail(jsonStr);
				 }else{
					 strokeResult = null;
					 if(mIExternalInterfaceAR!=null)
						 mIExternalInterfaceAR.OnFinishDriving(mJSONObject.toString(), "", 0);
					cyx_DriveRecodeActivity.this.finish();
				 }
			}
		});

		showAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		showAction.setDuration(1000);
		hideAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
		hideAction.setDuration(1000);

		// 数据查询及页面展示
		if (CYX_Drive_SDK.getInstance().getConnection().getConnectionState() != Connection.CONN_STATE_LOGIN_OK) {
			tips.startAnimation(showAction);
			tips.setVisibility(View.VISIBLE);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					tips.startAnimation(hideAction);
					tips.setVisibility(View.GONE);
				}
			}, 3 * 1000);
		} else {
			mProgressDialog.show();
			// if(cyx_MyApplication.getStrokeDataList().size()>0) {
			// if(mDataList.size() == 0) {
			// mDataList.addAll(cyx_MyApplication.getStrokeDataList());
			// }
			// if(mARAwardInfoList.size() == 0) {
			// mARAwardInfoList.addAll(cyx_MyApplication.getmARAwardInfoList());
			// }
			// adapter.notifyDataSetChanged();
			// listView.setSelection(0);
			// mProgressDialog.dismiss();
			// null_data.setVisibility(View.GONE);
			// listView.setVisibility(View.VISIBLE);
			// } else {
			Connection.getInstance().sendExtData(
					new ExtraDataProcess().getQueryStrokeData(userInfo
							.getUserID(), TimeUtil.parseToUTC(TimeUtil
							.dateLongFormatString(System.currentTimeMillis(),
									TimeUtil.format1)), flag, Integer
							.valueOf(area)), mRequestCallback);
			// }
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stop = true;
		mHandler.removeCallbacks(getAddressStrRunnable);
		cyx_MyApplication.getInstance().removeActivity(this);
	}

	private class MyAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater inflater;
		String id = "";

		public MyAdapter(Context context) {
			mContext = context;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mDataList == null)
				return 0;
			return mDataList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		// 添加数据
		public boolean addDatas(List<StrokeResult> addDatas) {
			if (mDataList == null)
				mDataList = new LinkedList<StrokeResult>();
			return mDataList.addAll(addDatas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(MResource.getLayoutId(mContext,
						"cyx_drive_recode_item"), null);
				holder = new ViewHolder();
				holder.grade = (TextView) convertView.findViewById(MResource
						.getID(mContext, "grade"));
				holder.mileage = (TextView) convertView.findViewById(MResource
						.getID(mContext, "tv_mileage"));
				holder.times = (TextView) convertView.findViewById(MResource
						.getID(mContext, "tv_duration"));
				holder.origin = (TextView) convertView.findViewById(MResource
						.getID(mContext, "tv_origin"));
				holder.destination = (TextView) convertView
						.findViewById(MResource.getID(mContext,
								"tv_destination"));
				holder.date = (TextView) convertView.findViewById(MResource
						.getID(mContext, "tv_date"));
				holder.begin = (TextView) convertView.findViewById(MResource
						.getID(mContext, "tv_start_time"));
				holder.awardView = convertView.findViewById(MResource.getID(
						mContext, "reward_layout"));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mileage.setText(formatMileage(mDataList.get(position)
					.getMileages() / 1000)
					+ mContext.getResources().getString(
							MResource.getStringId(mContext, "km")));
			if (TextUtils.isEmpty(mDataList.get(position).getStartAddName())) {
				holder.origin.setText(mContext.getResources().getString(
						MResource.getStringId(mContext, "unknow")));
			} else {
				holder.origin
						.setText(mDataList.get(position).getStartAddName());
			}
			if (TextUtils.isEmpty(mDataList.get(position).getEndAddName())) {
				holder.destination.setText(mContext.getResources().getString(
						MResource.getStringId(mContext, "unknow")));
			} else {
				holder.destination.setText(mDataList.get(position)
						.getEndAddName());
			}
			holder.date.setText(TimeUtil.dataFormatMMdd(mDataList.get(position)
					.getBegin(), TimeUtil.format));
			holder.begin.setText(TimeUtil.getHHmm(TimeUtil
					.parseToLocal(mDataList.get(position).getBegin())));
			holder.times.setText(TimeUtil.getHoueAndMinute(mDataList.get(
					position).getTimes() * 1000));
			id = mDataList.get(position).getStroke();
			for (int i = 0, size = mARAwardInfoList.size(); i < size; i++) {
				if (mARAwardInfoList.get(i).getRid().equals(id)) {
					int score = mARAwardInfoList.get(i).getSc();
					holder.grade.setText(score + "");
					holder.grade.setTextColor(getColor(score));
					if (0 == mARAwardInfoList.get(i).getGt()) { // 未领奖
						holder.awardView.setVisibility(View.VISIBLE);
					}
				}
			}
			if (TextUtils.isEmpty(holder.grade.getText())) {
				holder.grade.setText("--");
			}
			return convertView;
		}

		class ViewHolder {
			TextView grade;
			TextView mileage;
			TextView times;
			TextView begin;
			TextView origin;
			TextView destination;
			TextView date;
			View awardView;
		}
	}
	
	private int getColor(int score) {
		if(score < 60) {
			return Color.RED;
		} else if(score >= 60 && score < 70) {
			return getResources().getColor(MResource.getColorId(getApplicationContext(), "recode_1"));
		} else if(score >= 70 && score < 80) {
			return getResources().getColor(MResource.getColorId(getApplicationContext(), "recode_2"));
		} else if(score >= 80 && score < 90) {
			return getResources().getColor(MResource.getColorId(getApplicationContext(), "recode_3"));
		} else if(score >= 90 && score < 100) {
			return getResources().getColor(MResource.getColorId(getApplicationContext(), "recode_4"));
		} else if(score >= 100) {
			return Color.GREEN;
		}
		return Color.BLACK;
	}

	private void onLoad() {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime(TimeUtil.dateLongFormatString(
				System.currentTimeMillis(), TimeUtil.format1));
	}

	// 0上滑， 往后查，加载更多 1下滑，往前查询，刷新
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		flag = 0;// 刷新
		Connection.getInstance().sendExtData(
				new ExtraDataProcess().getQueryStrokeData(userInfo.getUserID(),
						mDataList.get(0).getBegin(), flag,
						Integer.valueOf(area)), mRequestCallback);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		flag = 1;// 更多
		Connection.getInstance().sendExtData(
				new ExtraDataProcess().getQueryStrokeData(userInfo.getUserID(),
						mDataList.get(mDataList.size() - 1).getBegin(), flag,
						Integer.valueOf(area)), mRequestCallback);
	}

	@Override
	public void TripMarkData(String data) {
		// TODO Auto-generated method stub
		try {
			JSONObject jdata = new JSONObject(data);
			String itemStr = "";
			ArData arData = null;
			if (ids != null && ids.length > 0) {
				for (int i = 0, length = ids.length; i < length; i++) {
					itemStr = jdata.getString(ids[i]);
					if (!TextUtils.isEmpty(itemStr)) {
						arData = gson.fromJson(itemStr,
								new TypeToken<ArData>() {
								}.getType());
						for (int j = 0, size = mARAwardInfoList.size(); j < size; j++) {
							if (ids[i].equals(mARAwardInfoList.get(j).getRid())) {
								mARAwardInfoList.get(j).setSc(arData.score);
								mARAwardInfoList.get(j).setGt(arData.got);
							}
						}
					}
				}
				cyx_MyApplication.setmARAwardInfoList(mARAwardInfoList); // 重新设值
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						adapter.notifyDataSetChanged();
					}
				});
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showHintContent() {
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		onLoad();
		if (!TextUtils.isEmpty(hintContent))
			Toast.makeText(cyx_DriveRecodeActivity.this, hintContent,
					Toast.LENGTH_SHORT).show();
		hintContent = "";
	}

	private class GetAddressStrRunnable implements Runnable {
		private double startlon; // 起点经度
		private double startlat; // 起点纬度
		private double endlon; // 钟点经度
		private double endlat; // 终点纬度
		StrokeResult strokeResult;
		int size = 0;
		String startAdd, endAdd;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (!stop) {
				if (mDataList == null || mDataList.size() == 0) {
					try {
						Thread.sleep(3 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					size = mDataList.size();
					for (int i = 0; i < size; i++) {
						strokeResult = mDataList.get(i);
						if (TextUtils.isEmpty(strokeResult.getStartAddName())) {
							startlon = strokeResult.getStartlon();
							startlat = strokeResult.getStartlat();
							try {
								startAdd = BMapUtil.getAddress(
										cyx_DriveRecodeActivity.this, startlon,
										startlat);
							} catch (Exception e) {
								e.printStackTrace();
								startAdd = getResources().getString(
										MResource.getStringId(
												getApplicationContext(),
												"unknow"));
							}
							mDataList.get(i).setStartAddName(startAdd);
						}
						if (TextUtils.isEmpty(strokeResult.getEndAddName())) {
							endlon = strokeResult.getEndlon();
							endlat = strokeResult.getEndlat();
							try {
								endAdd = BMapUtil.getAddress(
										cyx_DriveRecodeActivity.this, endlon,
										endlat);
							} catch (Exception e) {
								e.printStackTrace();
								endAdd = getResources().getString(
										MResource.getStringId(
												getApplicationContext(),
												"unknow"));
							}
							mDataList.get(i).setEndAddName(endAdd);
						}
						try {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									adapter.notifyDataSetChanged();
								}
							});

							Thread.sleep(3 * 1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	private RequestCallback mRequestCallback = new RequestCallback() {
		@Override
		public void onSuccess(String bizJsonData) {
			// TODO Auto-generated method stub
			if (cyx_MyApplication.getInstance().getActivityStack()
					.lastElement().getClass().getName()
					.equals(cyx_DriveRecodeActivity.this.getClass().getName())) {
				try {
					JSONObject jsonObj = new JSONObject(bizJsonData);
					switch (jsonObj.getInt("result")) {
					case ConstantContext.SUCCESS:
						String data = jsonObj.getString("data");
						if (!TextUtils.isEmpty(data)) {
							JSONObject dataObj = new JSONObject(data);
							String extra = dataObj.getString("extra");
							String info = dataObj.getString("info");
							Gson gson = new Gson();
							if (!TextUtils.isEmpty(extra)) {
								try {
									strokes = gson
											.fromJson(
													extra,
													new TypeToken<List<StrokeResult>>() {
													}.getType());
								} catch (Exception e) {
									strokes = null;
								}
							}

							if (!TextUtils.isEmpty(info)) {
								try {
									infoList = gson.fromJson(info,
											new TypeToken<List<ARAwardInfo>>() {
											}.getType());
								} catch (Exception e) {
									infoList = null;
								}
							}
							if (strokes != null && strokes.size() > 0) {
								mDataList.removeAll(strokes);
								if (flag == 0) {
									mDataList.addAll(0, strokes);
								} else {
									mDataList.addAll(strokes);
								}
								for (int i = 0; i < mDataList.size(); i++) {
									StrokeResult stroke = mDataList.get(i);
									cyx_MyApplication.getStrokeDataList().add(
											stroke);
								}
								// cyx_MyApplication.setStrokeDataList(mDataList);
							} else {
								if (cyx_MyApplication.getStrokeDataList() == null
										|| cyx_MyApplication
												.getStrokeDataList().size() == 0) {
									null_data.setVisibility(View.VISIBLE);
									listView.setVisibility(View.GONE);
									hintContent = getResources().getString(
											MResource.getStringId(
													getApplicationContext(),
													"no_data"));
								} else {
									if (flag == 0) {
										hintContent = getResources()
												.getString(
														MResource
																.getStringId(
																		getApplicationContext(),
																		"no_new_data"));
									} else {
										hintContent = getResources()
												.getString(
														MResource
																.getStringId(
																		getApplicationContext(),
																		"no_more_data"));
									}
								}
								onLoad();
								showHintContent();
								return;
							}
							if (infoList != null && infoList.size() > 0) {
								mARAwardInfoList.addAll(infoList);
							}
							cyx_MyApplication
									.setmARAwardInfoList(mARAwardInfoList);
							onLoad();
							if (mDataList == null || mDataList.size() == 0) {
								null_data.setVisibility(View.VISIBLE);
								listView.setVisibility(View.GONE);
							} else {
								null_data.setVisibility(View.GONE);
								listView.setVisibility(View.VISIBLE);
								if (flag == 1 || isFirstSearch) {
									listView.setSelection(0);
								} else {
									listView.setSelection(mDataList.size() - 1);
								}
								adapter.notifyDataSetChanged();
								int size = mDataList.size();
								for (int i = 0; i < size; i++) {
									idsList.add(mDataList.get(i).getStroke());
								}
								ids = idsList.toArray(new String[size]);
								if (mIExternalInterfaceAR != null)
									mIExternalInterfaceAR.receiverTripMark(
											cyx_DriveRecodeActivity.this, ids);
							}
						}
						break;
					case ConstantContext.ERROR_1:	//设备内部编号无传递
						hintContent = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"comment_userid_null"));
						break;
					case ConstantContext.ERROR_2:	//无对应设备
						hintContent = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"no_devices"));
						break;
					case ConstantContext.ERROR_3:	//时间为空
						hintContent = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"time_null"));
						break;
					case ConstantContext.ERROR_4:	//滑动标志不对
						hintContent = getResources().getString(
								MResource.getStringId(getApplicationContext(),
										"search_fail"));
						break;
					}
					showHintContent();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFailed(int reason) {
			// TODO Auto-generated method stub
			if (cyx_MyApplication.getInstance().getActivityStack()
					.lastElement().getClass().getName()
					.equals(cyx_DriveRecodeActivity.this.getClass().getName())) {
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
					hintContent += getResources().getString(
							MResource.getStringId(getApplicationContext(),
									"request_timeOut"));
					tips.setAnimation(showAction);
					tips.setVisibility(View.VISIBLE);
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							tips.setAnimation(hideAction);
							tips.setVisibility(View.GONE);
						}
					}, 3 * 1000);
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
	};

	// 格式化里程
	private double formatMileage(double mileage) {
		DecimalFormat df1 = new DecimalFormat("#0.0");
		if (mileage > 0)
			return Double.valueOf(df1.format(mileage));
		return 0;
	}

	private class ArData {
		int score;
		int got;
	}

	private JSONArray changeListToArray(ARAwardInfo arAwardInfo) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject;
		if (arAwardInfo == null) {
			try {
				jsonObject = new JSONObject();
				jsonObject.put("tp", 1);
				jsonObject.put("id", 1);
				jsonObject.put("n", 0);
				jsonArray.put(jsonObject);
				jsonObject = new JSONObject();
				jsonObject.put("tp", 1);
				jsonObject.put("id", 2);
				jsonObject.put("n", 0);
				jsonArray.put(jsonObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			List<AwardData> adList = arAwardInfo.getPzArray();
			for (int i = 0; i < adList.size(); i++) {
				jsonObject = new JSONObject();
				try {
					jsonObject.put("tp", adList.get(i).getTp());
					jsonObject.put("id", adList.get(i).getId());
					jsonObject.put("n", adList.get(i).getN());
					jsonArray.put(jsonObject);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return jsonArray;
	}

	private JSONArray getpzListSimulate() {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("id", 1);
			jsonObject.put("n", new Random().nextInt(100));
			jsonObject.put("tp", 1);
			jsonArray.put(jsonObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonArray;
	}

}
