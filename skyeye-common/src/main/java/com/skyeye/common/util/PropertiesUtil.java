package com.skyeye.common.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * 
	 * @ClassName: PropertiesUtil
	 * @Description: 读写配置文件
	 * @author 卫志强
	 * @date 2018年10月08日
	 *
 */
public class PropertiesUtil {
	public static Properties prop = new Properties();

	/**
	 * 取出值
	 * 
	 * @param k key键
	 * @param filepath properties文件路径
	 * @return
	 */
	public static String getValue(String k, String filepath) {
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(filepath));
			prop.load(in); /// 加载属性列表
			Iterator<String> it = prop.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (key.equals(k)) {
					return prop.getProperty(key);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取文件中的key-value的size
	 * 
	 * @param filepath properties文件路径
	 * @return
	 */
	public static int getPropertiesSize(String filepath) {
		InputStream in;
		int size = 0;
		try {
			in = new BufferedInputStream(new FileInputStream(filepath));
			prop.load(in); /// 加载属性列表
			Iterator<String> it = prop.stringPropertyNames().iterator();
			while (it.hasNext()) {
				size++;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
     * 设置键值
     * @param filepath properties文件路径
     * @param key key键
     * @param value value值 
     */
    public static void writeProperties(String filepath, String key, String value){
        ///保存属性到properties文件
        FileOutputStream oFile ;
        try {
            oFile = new FileOutputStream(filepath, true);//true表示追加打开
            prop.setProperty(key, value);
            prop.store(oFile, "Update IPproperties value");
            oFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
