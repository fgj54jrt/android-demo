package com.cwits.cyx_drive_sdk.userInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.text.TextUtils;

import com.cwits.cyx_drive_sdk.data.PersistentStorageProvider;
import com.cwits.cyx_drive_sdk.ui.MResource;

public class UserManager implements IUserManager {

	private String mDefUserName = ""; // 当前用户名
	private Context mContext;
	private String mConfigDir = new String(); // 用户文件路径
	private String CYX_DRIVE = "CYX_DRIVE";
	private final String USERXML_NAME = new String("UsersConfig.xml"); // 用户信息配置文件
	private User users;
	private List<User> userList;

	@Override
	public void initUserInfo(Context context) {
		// TODO Auto-generated method stub
		mContext = context;
		/* 获得配置文件及数据文件路径 */
		mConfigDir = context.getDir(CYX_DRIVE, Context.MODE_PRIVATE)
				.getAbsolutePath();
		initUserInfo();
	}

	public String getConfigDir() {
		return mConfigDir;
	}

	@Override
	public String[] getAllUserName() {

		if (userList == null || userList.size() <= 0)
			return new String[0];

		String[] devices = new String[userList.size()];
		for (int i = 0; i < userList.size(); ++i)
			devices[i] = userList.get(i).getUserName();
		return devices;
	}

	@Override
	public String getCurrentUserName() {

		return mDefUserName;
	}

	@Override
	public boolean addUser(String name, int loginFlag, String password, int flag) {
		boolean ret = false;
		String oldUser = mDefUserName;
		boolean isExits = false;
		if (userList != null && userList.size() > 0) {
			for (int i = 0; i < userList.size(); i++) {
				if (!TextUtils.isEmpty(name)) {
					if ((userList.get(i).getUserName()).equals(name)) {
						isExits = true;
						userList.get(i).setPassword(password);
						userList.get(i).setIsAutoLogin(loginFlag);
						userList.get(i).setFlag(flag);
						writeUserXMl();
						break;
					}
				}
			}
		}
		if (!isExits) {
			// //新用户
			User user = new User();
			user.setUserName(name);
			user.setIsAutoLogin(loginFlag);
			user.setPassword(password);
			user.setFlag(flag);
			userList.add(user);
			ret = writeUserXMl();
			/* 如果保存成功则创建相应文件夹，否则还原 */
			if (ret) {
				File userDir = new File(mConfigDir + "/" + name);
				if(!userDir.exists())
					userDir.mkdir();
				/* 在相应文件夹下创建一套默认路径 */
				changeUserContext(oldUser, name);
			} else {

			}
		} else {
			ret = true;
		}

		return ret;
	}

	@Override
	public boolean setDefaultUser(String name) {
		// TODO Auto-generated method stub
		String oldDefUserName = mDefUserName;
		boolean ret = false;
		boolean isExist = false;
		for (int i = 0; i < userList.size(); i++) {
			if ((userList.get(i).getUserName()).equals(name)) {
				isExist = true;
				break;
			}
		}
		if (isExist) {
			mDefUserName = name;
			ret = writeUserXMl();
			if (!ret) {
				mDefUserName = oldDefUserName;
			} else {
				File userDir = new File(mConfigDir + "/" + name);
				if(!userDir.exists())
					userDir.mkdir();
				/* 在相应文件夹下创建一套默认路径 */
				changeUserContext(oldDefUserName, name);
			}
		}
		return ret;

	}

	private boolean writeUserXMl() {
		File userxml = new File(mConfigDir + "/" + USERXML_NAME);
		XmlSerializer xmlFile = null;
		/* 确保文件已经创建过 */
		try {
			userxml.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			xmlFile = XmlPullParserFactory.newInstance().newSerializer();
			xmlFile.setOutput(new FileWriter(userxml));
			xmlFile.startDocument("UTF-8", false);
			xmlFile.startTag(null, "user_list");
			xmlFile.attribute(null, "default_username",
					new String(mDefUserName));
			// user
			for (int i = 0; i < userList.size(); i++) {
				xmlFile.startTag(null, "user");
				xmlFile.startTag(null, "username");
				xmlFile.text(userList.get(i).getUserName());
				xmlFile.endTag(null, "username");
				xmlFile.startTag(null, "save_password");
				xmlFile.text(userList.get(i).getPassword());
				xmlFile.endTag(null, "save_password");

				xmlFile.startTag(null, "auto_login");
				xmlFile.text(Integer.valueOf(userList.get(i).getIsAutoLogin())
						.toString());
				xmlFile.endTag(null, "auto_login");

				xmlFile.startTag(null, "login_falg");
				xmlFile.text(Integer.valueOf(userList.get(i).getFlag()).toString());
				xmlFile.endTag(null, "login_falg");
				xmlFile.endTag(null, "user");
			}
			// </user_list>
			xmlFile.endTag(null, "user_list");
			// </user_config>
			// xmlFile.endTag(null, "user_config");
			xmlFile.endDocument();
			xmlFile.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 保存用户信息文件
	private void initUserInfo() {
		File userConfig = new File(mConfigDir + "/" + USERXML_NAME);
		if (!userConfig.exists()) {
			FileOutputStream fileOutput = null;
			try {
				fileOutput = new FileOutputStream(userConfig);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			if (null != fileOutput) {
				InputStream in = null;
				try {
					in = mContext.getResources()
							.openRawResource(
									MResource.getRawID(mContext,
											"default_user_config"));
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

		// 解析UsercConfig.xml文件
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new FileReader(userConfig));
			int enventType = xpp.getEventType();
			String tagName = new String("");
			int flag = 0;
			userList = new ArrayList<User>();
			while (enventType != XmlPullParser.END_DOCUMENT) {
				if (enventType == XmlPullParser.START_TAG) {
					tagName = xpp.getName();
					if (tagName.equalsIgnoreCase("user_list")) {
						mDefUserName = xpp.getAttributeValue(null,
								"default_username");
					}
					if (tagName.equalsIgnoreCase("user")) {
						users = new User();
						flag = 0;
					}
				} else if (enventType == XmlPullParser.TEXT) {
					// 用户名
					if (tagName.equalsIgnoreCase("username")
							&& (!xpp.getText().equalsIgnoreCase("_default_"))) {
						users.setUserName(xpp.getText());
						flag++;
					}
					// 是否保存密码标志
					else if (tagName.equalsIgnoreCase("save_password")) {
						users.setPassword(xpp.getText().toString());
						flag++;
					}
					// 是否自动登录标志
					else if (tagName.equalsIgnoreCase("auto_login")) {
						users.setIsAutoLogin(Integer.parseInt(xpp.getText()
								.toString()));
						flag++;
					} else if (tagName.equalsIgnoreCase("login_falg")) {
						users.setFlag(Integer
								.parseInt(xpp.getText().toString()));
						flag++;
					}
					if (4 == flag) {
						userList.add(users);
					}
				} else {
					tagName = new String("");
				}
				enventType = xpp.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean setSavePassWord(String name, String password) {
		boolean ret = false;
		if (userList != null && userList.size() > 0) {
			for (int i = 0; i < userList.size(); i++) {
				if (name.equals(userList.get(i).getUserName())) {
					userList.get(i).setPassword(password);
					ret = true;
					break;
				}
			}
		}
		if (ret) {
			writeUserXMl();
		}
		return ret;
	}

	private void changeUserContext(String oldUserName, String newUserName) {
		String userDir = mConfigDir + "/" + newUserName + "/"; // 文件路径
		final String UP_LOAD_TRIP_DATA_DB = "upload_trip_data.db";   //上传行驶数据的数据库
//		((IDataStorageLocation) PersistentStorageProvider.getPersistentStorage(newUserName))
//		.SetDataStorageLocation(userDir + UP_LOAD_TRIP_DATA_DB);
	}


	@Override
	public boolean setAutoLogin(String name, int login) {
		boolean ret = false;
		if (userList != null && userList.size() > 0) {
			for (int i = 0; i < userList.size(); i++) {
				if (name.equals(userList.get(i).getUserName())) {
					userList.get(i).setIsAutoLogin(login);
					ret = true;
					break;
				}
			}
		}
		if (ret) {
			writeUserXMl();
		}
		return ret;
	}
	
	@Override
	public boolean setUserFlag(String name, int flag) {
		// TODO Auto-generated method stub
		boolean ret = false;
		if (userList != null && userList.size() > 0) {
			for (int i = 0; i < userList.size(); i++) {
				if (name.equals(userList.get(i).getUserName())) {
					userList.get(i).setFlag(flag);
					ret = true;
					break;
				}
			}
			System.out.println("--------setUserFlag"+ret);
		}
		if (ret) {
			writeUserXMl();
		}
		return ret;
	}

	class User {
		String userName;
		int isAutoLogin;
		String password;
		int flag;

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public int getIsAutoLogin() {
			return isAutoLogin;
		}

		public void setIsAutoLogin(int isAutoLogin) {
			this.isAutoLogin = isAutoLogin;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public int getFlag() {
			return flag;
		}

		public void setFlag(int flag) {
			this.flag = flag;
		}

	}
	@Override
	public void createAllFilesForDirector(String username) {
		String userDir = mConfigDir + "/" + username + "/"; // 文件路径
		final String UP_LOAD_TRIP_DATA_DB = "upload_trip_data.db";   //上传行驶数据的数据库
		File userFile = new File(userDir);
		if(!userFile.exists())
			userFile.mkdir();
//		((IDataStorageLocation) PersistentStorageProvider.getPersistentStorage(username))
//		.SetDataStorageLocation(userDir + UP_LOAD_TRIP_DATA_DB);
	}

}
