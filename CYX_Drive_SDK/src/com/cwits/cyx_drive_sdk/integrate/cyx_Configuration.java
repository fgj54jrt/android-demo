package com.cwits.cyx_drive_sdk.integrate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;

import com.cwits.cyx_drive_sdk.ui.MResource;

public class cyx_Configuration implements IConfiguration{
	Context mContext;
	private String mServerIP = new String("203.195.143.251");
	private int mServerPort = 5222;
	private String mTestServerIP = new String("172.16.0.252");
	private int mTestServerPort = 5222;					// 测试服务器端口
	public final static String CONFIG_FILE_NAME = new String("Config.xml");
	private String mConfigDir = ""; 					// 文件路径
	private String CYX_DRIVE = "CYX_DRIVE";						
	private boolean mIsTestServer = false;
	
	public void InitConfig(Context context){
		this.mContext = context;
		mConfigDir = context.getDir(CYX_DRIVE, Context.MODE_PRIVATE)
				.getAbsolutePath();
		//获取defaule.xml文件中的ip值
		readDefault_config();
		/* 确认配置文件存在 */
		File configFile = new File(mConfigDir + "/" + CONFIG_FILE_NAME);
		
		if(!configFile.exists()){
			copyDefault(context, configFile);
		}
		/* 读取配置XML文件 */
		try {
			XmlPullParserFactory factory;
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new FileReader(configFile));
			int eventType = xpp.getEventType();
			String tagName = new String("");
			// 分析读取事件并保存数据
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					tagName = xpp.getName();
				} else if (eventType == XmlPullParser.TEXT) {
					if (tagName.equalsIgnoreCase("server_ip")) {
						mServerIP = xpp.getText();
					} else if (tagName.equalsIgnoreCase("server_port")) {
						mServerPort = Integer.parseInt(xpp.getText());
					} else if (tagName.equalsIgnoreCase("test_server_ip")) {
						mTestServerIP = xpp.getText();
					} else if (tagName.equalsIgnoreCase("test_server_port")) {
						mTestServerPort = Integer.parseInt(xpp.getText());
					} else if (tagName.equalsIgnoreCase("is_test_server")) {
						if (xpp.getText().equals("1")) {
							mIsTestServer = true;
						} else {
							mIsTestServer = false;
						}
					} 
				} else {
					tagName = new String("");
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//将default_confg.cml 中的内容拷贝到手机中
	private void copyDefault(Context context,File configFile){
			FileOutputStream fileOutput = null;
			try {
				fileOutput = new FileOutputStream(configFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			if (null != fileOutput) {
				InputStream in = null;
				try {
					in = context.getResources().openRawResource(MResource.getRawID(mContext, "default_config"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// 拷贝数据
				if (null != in) {
					byte[] buf = new byte[1024];
					int length = buf.length;

					try {
						while (-1 != (length = in.read(buf, 0, length))) {
							fileOutput.write(buf, 0, length);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// 关闭文件
				try {
					fileOutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	
	String default_serverIp="";
	int default_serverPort=5222;
	String default_testServerIp="";
	int default_testServerPort=522;
	//解析默认的default_config文件
	private void readDefault_config(){
		try {
			InputStream in=mContext.getResources().openRawResource(MResource.getRawID(mContext, "default_config"));
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xmlParser = factory.newPullParser();
			xmlParser.setInput(in, "UTF-8");
			String tagName = new String("");
			int eventType=xmlParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					tagName = xmlParser.getName();
				} else if (eventType == XmlPullParser.TEXT) {
					if (tagName.equalsIgnoreCase("server_ip")) {
						default_serverIp = xmlParser.getText();
					} else if (tagName.equalsIgnoreCase("server_port")) {
						default_serverPort = Integer.parseInt(xmlParser.getText());
					} else if (tagName.equalsIgnoreCase("test_server_ip")) {
						default_testServerIp = xmlParser.getText();
					} else if (tagName.equalsIgnoreCase("test_server_port")) {
						default_testServerPort = Integer.parseInt(xmlParser.getText());
					} 
				}else {
					tagName = new String("");
				}
				try {
					eventType = xmlParser.next();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	public String getServerIP() {
		return mServerIP;
	}

	@Override
	public boolean setServerIP(String serverIP) {
		mServerIP = serverIP;
		return writeDocToFile();
	}

	@Override
	public int getServerPort() {
		return mServerPort;
	}

	@Override
	public boolean setServerPort(int serverPort) {
		mServerPort = serverPort;
		return writeDocToFile();
	}

	@Override
	public String getTestServerIP() {
		return mTestServerIP;
	}

	@Override
	public boolean setTestServerIP(String serverIP) {
		mTestServerIP = serverIP;
		return writeDocToFile();
	}

	@Override
	public int getTestServerPort() {
		return mTestServerPort;
	}

	@Override
	public boolean setTestServerPort(int serverPort) {
		mTestServerPort = serverPort;
		return writeDocToFile();
	}

	@Override
	public boolean isTestServer() {
		return mIsTestServer;
	}

	@Override
	public boolean setTestServer(boolean serverTag) {
		mIsTestServer = serverTag;
		return writeDocToFile();
	}
	private boolean writeDocToFile() {
		File configFile = new File(mConfigDir + "/" + CONFIG_FILE_NAME);
		XmlSerializer xmlFile = null;

		/* 确保文件已经创建过 */
		try {
			configFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* 创建XML文件并保存 */
		try {
			xmlFile = XmlPullParserFactory.newInstance().newSerializer();
			xmlFile.setOutput(new FileWriter(configFile));
			xmlFile.startDocument("UTF-8", false);

			// <config>
			xmlFile.startTag(null, "config");
			
			// <server_ip>
			xmlFile.startTag(null, "server_ip");
			xmlFile.text(mServerIP);
			xmlFile.endTag(null, "server_ip");

			// <server_port>
			xmlFile.startTag(null, "server_port");
			xmlFile.text(Integer.valueOf(mServerPort).toString());
			xmlFile.endTag(null, "server_port");

			// <test_server_ip>
			xmlFile.startTag(null, "test_server_ip");
			xmlFile.text(mTestServerIP);
			xmlFile.endTag(null, "test_server_ip");

			// <test_server_port>
			xmlFile.startTag(null, "test_server_port");
			xmlFile.text(Integer.valueOf(mTestServerPort).toString());
			xmlFile.endTag(null, "test_server_port");

			// is_test_server
			xmlFile.startTag(null, "is_test_server");
			xmlFile.text(Integer.valueOf(mIsTestServer ? 1 : 0).toString());
			xmlFile.endTag(null, "is_test_server");

			xmlFile.endTag(null, "config");

			xmlFile.endDocument();
			xmlFile.flush();

			return true;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public int getAppVersionCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getAppVersionName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getConfigDir() {
		return mConfigDir;
	}

}
