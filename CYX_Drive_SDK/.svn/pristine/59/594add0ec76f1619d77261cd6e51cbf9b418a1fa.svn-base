package com.cwits.cyx_drive_sdk.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cwits.cyx_drive_sdk.integrate.CYX_Drive_SDK;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * 文件工具类
 * @author Administrator
 *
 */
public class FileUtil {
	private static final String TAG = "FileUtil";
	private static final String DIR = CYX_Drive_SDK.getSdcardDir()+File.separator+"cyx_dirvers";	//文件夹名称

    /**
     * 创建文件
     * 
     * @throws IOException
     */
    public static File createFileInSDCard(Context context,String fileName)
            throws IOException {
        return createFileInSDCard(context, "", fileName);
    }
    
    public static File createFileInSDCard(Context context,String subDir, String fileName)
            throws IOException {
    	if(!isFilePathExist(context, subDir)) {
    		creatSDDir(context, subDir);
    	}
        if (isFileExist(context, subDir, fileName)) {
            deleteFile(context, subDir, fileName);
        }
        File file;
        if(TextUtils.isEmpty(subDir)) {
        	file = new File( DIR + File.separator + fileName);
        } else {
        	file = new File( DIR + File.separator + subDir + File.separator + fileName);
        }
        file.createNewFile();
        return file;
    }
    

    /**
     * 创建目录
     * 
     * @param dirName
     */
    public static File creatSDDir(Context context, String subDir) {
    	File dirFile;
    	if(TextUtils.isEmpty(subDir)) {
        	dirFile = new File( DIR + File.separator);
    	} else {
    		dirFile = new File( DIR + File.separator + subDir + File.separator);
    	}
        dirFile.mkdirs();
        return dirFile;
    }

    /**
     * 删除文件
     * 
     * @param dir
     *            文件夹目录(已包含根目录)
     * @param fileName
     *            文件名
     * @return
     * @throws IOException
     */
    public static boolean deleteFile(Context context,String subDir, String fileName)
            throws IOException {
        File dirFile = null;
        if(TextUtils.isEmpty(subDir)) {
        	dirFile = new File( DIR + File.separator + fileName);
        } else {
        	dirFile = new File( DIR + File.separator + subDir + File.separator + fileName);
        }
        return dirFile.delete();
    }


    /**
     * 判断文件是否存在
     */
    public static boolean isFileExist(Context context, String subDir, String fileName) {
        File file = null;
        if(TextUtils.isEmpty(subDir)) {
        	 file = new File( DIR + File.separator + fileName);
        } else {
        	file = new File( DIR + File.separator + subDir + File.separator + fileName);
        }
        return file.exists();
    }
    
    public static boolean isFileExist(Context context, String fileName) {
    	return isFileExist(context, "" , fileName);
    }


    /**
     * 判断文件夹是否存在
     */
    public static boolean isFilePathExist(Context context, String subDir) {
        File file = null;
        if(TextUtils.isEmpty(subDir)) {
        	file = new File( DIR);
        } else {
        	file = new File( DIR + File.separator + subDir);
        }
        return file.exists();
    }

    
    /**
     * 往文件中写入json字符串
     * @param path
     * @param fileName
     * @param jsonStr
     * @return true表示成功，false表示失败
     */
    public static boolean writeJsonStr2SD(Context context,String fileName, String jsonStr) {
    	return writeJsonStr2SD(context, "", fileName, jsonStr);
    }
    
    public static boolean writeJsonStr2SD(Context context,String subDir, String fileName, String jsonStr) {
    	boolean result = true;
    	File file;
    	if(TextUtils.isEmpty(subDir)) {
    		file = new File(  DIR + File.separator, fileName);
    	} else {
    		file = new File( DIR + File.separator + subDir + File.separator, fileName);
    	}
    	try {
    		FileWriter fileWriter = null;
    		if(TextUtils.isEmpty(subDir)) {
    			fileWriter = new FileWriter(file, false);	
    		} else {
    			fileWriter = new FileWriter(file, true);	
    		}
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(jsonStr);
			bufferedWriter.newLine();
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "创建FileWriter出错");
			result = false;
		}
    	return result;
    }
    
    /**
     * 读取fileName文件中的json字符串
     * @param path
     * @param fileName
     * @return
     */
    public static String readJsonStrFromSD(Context context,String fileName) {
    	return readJsonStrFromSD(context, "", fileName);
    }
    
    public static String readJsonStrFromSD(Context context,String subDir, String fileName) {
    	String result = "";
    	if(isFileExist(context,subDir,fileName)) {
    		File file;
    		if(TextUtils.isEmpty(subDir)) {
    			file = new File(DIR + File.separator, fileName);
    		} else {
    			file = new File(DIR + File.separator + subDir + File.separator, fileName);
    		}
    		try {
				FileInputStream fis = new FileInputStream(file);
				try {
					// 准备一个字节数组用户装即将读取的数据   
					byte[] buffer = new byte[fis.available()];
					//对文件进行读取
					fis.read(buffer);
					//关闭流
					fis.close();
					result = new String(buffer, "UTF-8");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d(TAG, "创建byte数组出错");
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d(TAG, "读取文件数据出错");
			}
    	}
    	return result;
    }
    
    public static List<String> getAllFiles(Context context, String subDir){
    	File[] files = null;
		if(isFilePathExist(context, subDir)) {
			files = new File(DIR + File.separator + subDir + File.separator).listFiles();
		}
		if(files == null)
			return null;
		List<String> fileNames = new ArrayList<String>();
		for(File f : files){
			fileNames.add(f.getName());
		}
		return fileNames;
    }


}