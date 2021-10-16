/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.util;

import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.ObjectConstant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 工具类
 */
public class ToolUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ToolUtil.class);
	

	/**
	 * 创建一个空的docx文件
	 *
	 * @param path 文件路径
	 * @throws Exception
	 */
	public static void createNewDocxFile(String path) throws Exception {
		FileOutputStream out = null;
		try{
			XWPFDocument document = new XWPFDocument();
			out = new FileOutputStream(path);
			document.write(out);
		}finally{
			if(out != null) {
				out.close();
			}
		}
	}

	/**
	 * 创建一个空的excel文件
	 *
	 * @param path 文件路径
	 * @throws Exception
	 */
	public static void createNewExcelFile(String path) throws Exception {
		FileOutputStream output = null;
		HSSFWorkbook wb = null;
		try {
			// 创建Excel文件对象
			wb = new HSSFWorkbook();
			// 用文件对象创建sheet对象
			HSSFSheet sheet = wb.createSheet("sheet");
			// 用sheet对象创建行对象
			HSSFRow row = sheet.createRow(0);
			// 用行对象创建单元格对象Cell
			Cell cell = row.createCell(0);
			// 用cell对象读写。设置Excel工作表的值
			cell.setCellValue("");
			// 输出Excel文件
			output = new FileOutputStream(path);
			wb.write(output);
			output.flush();
		} finally {
			if (output != null){
				output.close();
			}
			if (wb != null){
				wb.close();
			}
		}
	}

	/**
	 * 创建一个空的ppt文件
	 *
	 * @param path 文件路径
	 * @throws Exception
	 */
	public static void createNewPPtFile(String path) throws Exception {
		FileOutputStream out = null;
		try {
			XMLSlideShow ppt = new XMLSlideShow();
			out = new FileOutputStream(new File(path));
			ppt.write(out);
		} finally {
			if (out != null){
				out.close();
			}
		}
	}

	/**
	 * 创建一个空的普通的文件
	 *
	 * @param path 文件路径
	 * @throws Exception
	 */
	public static void createNewSimpleFile(String path) throws Exception {
		File file = new File(path);
		file.createNewFile();
	}

	/**
	 * 
	     * @Title: getUrlParams
	     * @Description: 将url参数转换成map
	     * @param param aa=11&bb=22&cc=33
	     * @param @return    参数
	     * @return Map<String,Object>    返回类型
	     * @throws
	 */
	public static Map<String, Object> getUrlParams(String param) {
		Map<String, Object> map = new HashMap<>();
		if (ToolUtil.isBlank(param)) {
			return map;
		}
		String[] params = param.split("&");
		for (int i = 0; i < params.length; i++) {
			String[] p = params[i].split("=", 2);
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}
		return map;
	}
	
	/**
	 * 
	     * @Title: isBlank
	     * @Description: 判断字符串是否为空
	     * @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
	
	/**
	 * 
	     * @Title: isNumeric
	     * @Description: 判断是不是数字
	     * @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isNumeric(String str) {
		return match(ObjectConstant.VerificationParams.NUM.getRegular(), str);
	}
	
	/**
	 * 
	     * @Title: isEmail
	     * @Description: 验证邮箱
	     * @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isEmail(String str) {
		return match(ObjectConstant.VerificationParams.EMAIL.getRegular(), str);
	}
	
	/**
	 * 
	     * @Title: isDate
	     * @Description: 验证日期时间
	     * @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isDate(String str) {
		boolean convertSuccess = true;
		// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// 设置lenient为false,否则SimpleDateFormat会比较宽松地验证日期，比如2007-02-29会被接受，并转换成2007-03-01
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			convertSuccess = false;
		}
		return convertSuccess;
	}
	
	/**
	 * 
	     * @Title: match
	     * @Description: 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	     * @param regex
	     * @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 获取id
	 *
	 * @return
	 */
	public static String getSurFaceId() {
		UUID uuid = java.util.UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}
	
	/**
	 * MD5加密技术
	 *
	 * @param str 要加密的内容
	 * @return 加密后的内容
	 * @throws Exception
	 */
	public static String MD5(String str) throws Exception {
		byte[] bt = str.getBytes();
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] bt1 = md.digest(bt);
		StringBuffer sbf = new StringBuffer();
		for (int i = 0; i < bt1.length; i++) {
			int val = ((int) bt1[i]) & 0xff;
			if (val < 16)
				sbf.append("0");
			sbf.append(Integer.toHexString(val));
		}
		return sbf.toString();
	}
	
	/**
     * 使用递归方法建树
     * @param allMenu allMenu
     * @return
     */
	public static List<Map<String, Object>> allMenuToTree(List<Map<String, Object>> allMenu){
		List<Map<String, Object>> resultList = new ArrayList<>();
		for(Map<String, Object> bean : allMenu){
			if ("0".equals(bean.get("parentId").toString())) {
				resultList.add(findChildren(bean, allMenu, 0));
            }
		}
		return resultList;
	}
	
	/**
     * 递归查找子节点
     * @param treeNodes
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> findChildren(Map<String, Object> treeNode, List<Map<String, Object>> treeNodes, int level) {
    	List<Map<String, Object>> child = null;
        for (Map<String, Object> it : treeNodes) {
        	if(Integer.parseInt(it.get("menuLevel").toString()) == level + 1){
        		if(treeNode.get("id").toString().equals(it.get("parentId").toString().split(",")[level])) {
        			child = (List<Map<String, Object>>) treeNode.get("childs");
        			if (child == null) {
        				child = new ArrayList<>();
        			}
        			child.add(findChildren(it, treeNodes, level + 1));
        			treeNode.put("childs", child);
        		}
        	}
        }
        return treeNode;
    }
    
    /**
     * 使用递归建树
     * @param deskTops
     * @return
     */
    public static List<Map<String, Object>> deskTopsTree(List<Map<String, Object>> deskTops){
		List<Map<String, Object>> resultList = new ArrayList<>();
		for(Map<String, Object> bean : deskTops){
			if ("0".equals(bean.get("parentId").toString())) {
				resultList.add(findChildren(bean, deskTops, 0));
            }
		}
		for(Map<String, Object> bean : deskTops){
			if(!findChildren(resultList, bean.get("id").toString())){
				resultList.add(bean);
			}
		}
		return resultList;
	}
    
    /**
     * 递归判断id是否在集合中存在
     * @param treeNode
     * @param id
     * @return
     */
	public static boolean findChildren(List<Map<String, Object>> treeNode, String id){
    	List<Map<String, Object>> child = null;
    	for(Map<String, Object> bean : treeNode){
    		if(id.equals(bean.get("id").toString())){
    			return true;
    		}else{
    			child = (List<Map<String, Object>>) bean.get("childs");
    			if(child != null){
    				boolean in = findChildren(child, id);
    				if(in){
    					return in;
    				}
    			}
			}
		}
    	return false;
    }
	
	/**
	 * 随机不重复的6-8位
	 * @return
	 */
	public static int card() {
		int[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
			int index = rand.nextInt(i);
			int tmp = array[index];
			array[index] = array[i - 1];
			array[i - 1] = tmp;
		}
		int result = 0;
		for (int i = 0; i < 6; i++) {
			result = result * 10 + array[i];
		}
		return result;
	}
	
	/**
	 * 将表名转为Java经常使用的名字，如code_model转CodeModel
	 * @param str
	 * @return
	 */
	public static String replaceUnderLineAndUpperCase(String str) {
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		int count = sb.indexOf("_");
		while (count != 0) {
			int num = sb.indexOf("_", count);
			count = num + 1;
			if (num != -1) {
				char ss = sb.charAt(count);
				char ia = (char) (ss - 32);
				sb.replace(count, count + 1, ia + "");
			}
		}
		String result = sb.toString().replaceAll("_", "");
		return StringUtils.capitalize(result);
	}
	
	/**
	 * 首字母转小写
	 *
	 * @param str 要转换的字符串
	 * @return
	 */
	public static String toLowerCaseFirstOne(String str) {
		if (Character.isLowerCase(str.charAt(0))){
			return str;
		} else {
			return (new StringBuilder()).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
		}
	}
	
	/**
	 * 根据request获取ip
	 * @param request
	 * @return
	 */
	public static String getIpByRequest(HttpServletRequest request){
		String ip;
		ip = request.getHeader("x-forwarded-for");
		// 针对IP是否使用代理访问进行处理
		if (ToolUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ToolUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ToolUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(0, ip.indexOf(","));
		}
		if(!isBlank(request.getParameter("loginPCIp"))){
			for(String str : Constants.FILTER_FILE_IP_OPTION) {
				if (ip.indexOf(str) != -1) {
					ip = request.getParameter("loginPCIp");
					break;
				}
			}
		}
		return ip;
	}
	
	/**
	 * 获得CPU使用率.
	 * @return
	 */
	public static double getCpuRatioForWindows() {
		try {
			String procCmd = System.getenv("windir") + "//system32//wbem//wmic.exe process get Caption,CommandLine,"
					+ "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
			// 取进程信息
			long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
			long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
			if (c0 != null && c1 != null) {
				long idletime = c1[0] - c0[0];
				long busytime = c1[1] - c0[1];
				return Double.valueOf(Constants.PERCENT * (busytime) / (busytime + idletime)).doubleValue();
			} else {
				return 0.0;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0.0;
		}
	}
	
	/**
	 * 读取CPU信息
	 * @param proc
	 * @return
	 */
	public static long[] readCpu(final Process proc) {
		long[] retn = new long[2];
		try {
			proc.getOutputStream().close();
			InputStreamReader ir = new InputStreamReader(proc.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line = input.readLine();
			if (line == null || line.length() < Constants.FAULTLENGTH) {
				return null;
			}
			int capidx = line.indexOf("Caption");
			int cmdidx = line.indexOf("CommandLine");
			int rocidx = line.indexOf("ReadOperationCount");
			int umtidx = line.indexOf("UserModeTime");
			int kmtidx = line.indexOf("KernelModeTime");
			int wocidx = line.indexOf("WriteOperationCount");
			long idletime = 0;
			long kneltime = 0;
			long usertime = 0;
			while ((line = input.readLine()) != null) {
				if (line.length() < wocidx) {
					continue;
				}
				// 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
				String caption = BytesUtil.substring(line, capidx, cmdidx - 1).trim();
				String cmd = BytesUtil.substring(line, cmdidx, kmtidx - 1).trim();
				if (cmd.indexOf("wmic.exe") >= 0) {
					continue;
				}
				if (caption.equals("System Idle Process") || caption.equals("System")) {
					idletime += Long.valueOf(BytesUtil.substring(line, kmtidx, rocidx - 1).replaceAll(" ", "").trim()).longValue();
					idletime += Long.valueOf(BytesUtil.substring(line, umtidx, wocidx - 1).replaceAll(" ", "").trim()).longValue();
					continue;
				}
				if(!isBlank(BytesUtil.substring(line, kmtidx, rocidx - 1).trim())){
					kneltime += Long.valueOf(BytesUtil.substring(line, kmtidx, rocidx - 1).replaceAll(" ", "").trim()).longValue();
				}
				if(!isBlank(BytesUtil.substring(line, umtidx, wocidx - 1).trim())){
					usertime += Long.valueOf(BytesUtil.substring(line, umtidx, wocidx - 1).replaceAll(" ", "").trim()).longValue();
				}
			}
			retn[0] = idletime;
			retn[1] = kneltime + usertime;
			return retn;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				proc.getInputStream().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * listToTree
	 * <p>方法说明<p>
	 * 将JSONArray数组转为树状结构
	 * @param arr 需要转化的数据
	 * @param id 数据唯一的标识键值
	 * @param pId 父id唯一标识键值
	 * @param child 子节点键值
	 * @return JSONArray
	 */
	public static List<Map<String, Object>> listToTree(List<Map<String, Object>> arr, String id, String pId, String child) {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> hash = new HashMap<>();
		// 将数组转为Object的形式，key为数组中的id
		for (int i = 0; i < arr.size(); i++) {
			Map<String, Object> json = arr.get(i);
			hash.put(json.get(id).toString(), json);
		}
		// 遍历结果集
		for (int j = 0; j < arr.size(); j++) {
			// 单条记录
			Map<String, Object> aVal = arr.get(j);
			// 在hash中取出key为单条记录中pid的值
			Map<String, Object> hashVP = (Map<String, Object>) hash.get(aVal.get(pId).toString());
			// 如果记录的pid存在，则说明它有父节点，将她添加到孩子节点的集合中
			if (hashVP == null) {
				aVal.put(child, listToTreeTem(arr, id, pId, child, aVal.get(id).toString()));
				result.add(aVal);
			}
		}
		return result;
	}

	private static List<Map<String, Object>> listToTreeTem(List<Map<String, Object>> list, String id, String pId, String child, String idValue){
		List<Map<String, Object>> resultList = new ArrayList<>();
		for(Map<String, Object> bean : list){
			if (idValue.equals(bean.get(pId).toString())) {
				bean.put(child, listToTreeTem(list, id, pId, child, bean.get(id).toString()));
				resultList.add(bean);
			}
		}
		return resultList;
	}

	/**
	 * javaBean2Map
	 * @param javaBean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> javaBean2Map(Object javaBean) {
		Map<K, V> ret = new HashMap<>();
		try {
			Method[] methods = javaBean.getClass().getDeclaredMethods();// 获取所有的属性
			for (Method method : methods) {
				if (method.getName().startsWith("get")) {
					String field = method.getName();
					field = field.substring(field.indexOf("get") + 3);
					field = field.toLowerCase().charAt(0) + field.substring(1);
					Object value = method.invoke(javaBean, (Object[]) null);// invoke(调用)就是调用Method类代表的方法。它可以让你实现动态调用
					ret.put((K) field, (V) (null == value ? "" : value));
				}
			}
		} catch (Exception ee) {
			logger.warn("covert {} to map failed.", javaBean.getClass().toString(), ee);
		}
		return ret;
	}
	
	/**
	 * 
	     * @Title: randomStr
	     * @Description: 获取指定的随机值
	     * @param minLen
	     * @param maxLen
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public static String randomStr(int minLen, int maxLen) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		int length = random.nextInt(maxLen - minLen) + minLen;
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
	
	/**
	 * 
	     * @Title: formatDateByPattern
	     * @Description: 日期转换cron表达式  e.g:yyyy-MM-dd HH:mm:ss
	     * @param date
	     * @param dateFormat
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public static String formatDateByPattern(Date date, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String formatTimeStr = null;
		if (date != null) {
			formatTimeStr = sdf.format(date);
		}
		return formatTimeStr;
	}

	/**
	 * 日期转换cron表达式
	 *
	 * @param date yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public static String getCrons1(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateFormat = "ss mm HH dd MM ? yyyy";
		return formatDateByPattern(df.parse(date), dateFormat);
	}
	
	/**
	 * 
	     * @Title: getTalkGroupNum
	     * @Description: 获取聊天群号
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public static String getTalkGroupNum() {
		String numStr = "";
		String trandStr = String.valueOf((Math.random() * 9 + 1) * 10000);
		String dataStr = new SimpleDateFormat("yyyyMMddHHMMSS").format(new Date());
		numStr = trandStr.toString().substring(0, 5);
		numStr = numStr + dataStr;
		return numStr;
	}
	
	/**
	 * 获取视频截图
	 * @param videoLocation
	 * @param imageLocation
	 * @return
	 */
	public static boolean take(String videoLocation, String imageLocation, String ffmpegGPath){
		// 低精度
		List<String> commend = new ArrayList<String>();
		commend.add(ffmpegGPath);//视频提取工具的位置
		commend.add("-i");
		commend.add(videoLocation);
		commend.add("-y");
		commend.add("-f");
		commend.add("image2");
		commend.add("-ss");
		commend.add("08.010");
		commend.add("-t");
		commend.add("0.001");
		commend.add("-s");
		commend.add("352x240");
		commend.add(imageLocation);
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			builder.start();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 获取四位随机码
	 * @return
	 */
	public static String getFourWord() {
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder(4);
		for (int i = 0; i < 4; i++) {
			char ch = str.charAt(new Random().nextInt(str.length()));
			sb.append(ch);
		}
		return sb.toString();
	}
	
	/**
	 * 将文件父id变换
	 *
	 * @param folderNew 文件集合
	 * @param folderId 新的父id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void FileListParentISEdit(List<Map<String, Object>> folderNew, String folderId) {
		for(Map<String, Object> folder : folderNew){
			folder.put("newParentId", folderId);
			if(folder.get("children") != null){
				List<Map<String, Object>> child = (List<Map<String, Object>>) folder.get("children");
				FileListParentISEdit(child, String.format(Locale.ROOT, "%s%s,", folder.get("newParentId").toString(), folder.get("newId").toString()));
			}
		}
	}
	
	/**
	 * 将树转化为list
	 * @param folderNew
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> FileTreeTransList(List<Map<String, Object>> folderNew) {
		List<Map<String, Object>> beans = new ArrayList<>();
		for(Map<String, Object> folder : folderNew){
			if(folder.get("children") != null){
				List<Map<String, Object>> child = (List<Map<String, Object>>) folder.get("children");
				beans.addAll(FileTreeTransList(child));
				folder.remove("children");
			}
			beans.add(folder);
		}
		return beans;
	}
	
	/**
	 * 文件复制
	 * @param source 源文件
	 * @param target 拷贝后的文件地址
	 * @throws Exception
	 */
	public static void NIOCopyFile(String source, String target) throws Exception {
		// 1.采用RandomAccessFile双向通道完成，rw表示具有读写权限
		RandomAccessFile fromFile = new RandomAccessFile(source, "rw");
		FileChannel fromChannel = fromFile.getChannel();
		RandomAccessFile toFile = new RandomAccessFile(target, "rw");
		FileChannel toChannel = toFile.getChannel();
		long count = fromChannel.size();
		while (count > 0) {
			long transferred = fromChannel.transferTo(fromChannel.position(), count, toChannel);
			count -= transferred;
		}
		if (fromFile != null) {
			fromFile.close();
		}
		if (fromChannel != null) {
			fromChannel.close();
		}
		if (toFile != null) {
			toFile.close();
		}
		if (toChannel != null) {
			toChannel.close();
		}
	}
	
	/**
	 * 往数组中添加新元素
	 * @param arr
	 * @param num
	 * @return
	 */
	public static int[] addElementToArray(int[] arr, int num) {
		int[] result = new int[arr.length + 1];
		for (int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		result[result.length - 1] = num;
		return result;
	}
	
	/**
	 * 往数组中添加新元素
	 * @param arr
	 * @param num
	 * @return
	 */
	public static String[] addElementToArray(String[] arr, String num) {
		String[] result = new String[arr.length + 1];
		for (int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		result[result.length - 1] = num;
		return result;
	}
	
	/**
	 * 文件夹打包
	 *
	 * @param zipOut 压缩包流
	 * @param beans 树结构文件
	 * @param baseDir 扩展路径
	 * @param fileBath 压缩包输出路径
	 * @param type 文件输出类型  1.往文件里面写内容  2.直接输出文件
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked"})
	public static void recursionZip(ZipOutputStream zipOut, List<Map<String, Object>> beans, String baseDir, String fileBath, int type) throws Exception {
		String[] zipFileNames = {};
		int[] zipFileNamesNum = {};
		for(Map<String, Object> bean : beans){
			// 文件压缩包中的文件名
			String name = bean.get("fileName").toString();
			String fileType = bean.get("fileType").toString();
			Integer index = Arrays.asList(zipFileNames).indexOf(name);
			if(index >= 0){
				zipFileNamesNum[index] = zipFileNamesNum[index] + 1;
				if ("folder".equals(fileType)) {
					// 文件夹
					name = name + "(" + zipFileNamesNum[index] + ")";
				}else{
					// 文件
					name = name.substring(0, name.lastIndexOf(".")) + "(" + zipFileNamesNum[index] + ")." + fileType;
				}
			}
			zipFileNames = ToolUtil.addElementToArray(zipFileNames, name);
			zipFileNamesNum = ToolUtil.addElementToArray(zipFileNamesNum, 0);
			if ("folder".equals(fileType)) {
				// 空文件夹的处理
				zipOut.putNextEntry(new ZipEntry(baseDir + name + "/"));
				// 没有文件，不需要文件的copy
				zipOut.closeEntry();
				if(bean.containsKey("children") && !isBlank(bean.get("children").toString())){
					recursionZip(zipOut, (List<Map<String, Object>>) bean.get("children"), baseDir + name + "/", fileBath, type);
				}
			} else {
				if(type == 2){
					byte[] buf = new byte[1024];
					InputStream input = new FileInputStream(new File(fileBath + bean.get("fileAddress").toString()));
					zipOut.putNextEntry(new ZipEntry(baseDir + name));
					int len;
					while ((len = input.read(buf)) != -1) {
						zipOut.write(buf, 0, len);
					}
					zipOut.closeEntry();
					input.close();
				}else if(type == 1){
					byte[] buf = new byte[1024];
					int len;
					if(bean.containsKey("content") && !isBlank(bean.get("content").toString())){
						ByteArrayInputStream stream = new ByteArrayInputStream(bean.get("content").toString().getBytes());
						zipOut.putNextEntry(new ZipEntry(baseDir + name + "." + fileType));
						while ((len = stream.read(buf)) != -1) {
							zipOut.write(buf, 0, len);
						}
						stream.close();
					}else{
						InputStream input = new FileInputStream(new File(fileBath + bean.get("filePath").toString()));
						zipOut.putNextEntry(new ZipEntry(baseDir + name));
						while ((len = input.read(buf)) != -1) {
							zipOut.write(buf, 0, len);
						}
						input.close();
					}
					zipOut.closeEntry();
				}
			}
		}
	}
	
	/**
	 * 获取倒数第num个后面的内容
	 */
	public static String getSubStr(String str, int num) {
		String result = "";
		int i = 0;
		while (i < num) {
			int lastFirst = str.lastIndexOf('/');
			result = str.substring(lastFirst) + result;
			str = str.substring(0, lastFirst);
			i++;
		}
		return result.substring(1);
	}
	
	/**
	 * 获取集合中的文件夹
	 */
	public static List<Map<String, Object>> getFolderByList(List<Map<String, Object>> beans) {
		List<Map<String, Object>> items = new ArrayList<>();
		for(Map<String, Object> bean : beans){
			if("folder".equals(bean.get("fileExtName").toString())){
				items.add(bean);
			}
		}
		return items;
	}
	
	/**
	 * 获取集合中的文件
	 */
	public static List<Map<String, Object>> getFileByList(List<Map<String, Object>> beans) {
		List<Map<String, Object>> items = new ArrayList<>();
		for(Map<String, Object> bean : beans){
			if(!"folder".equals(bean.get("fileExtName").toString())){
				items.add(bean);
			}
		}
		return items;
	}

	/**
	 * 判断是否是json串
	 * @param content
	 * @return
	 */
	public static boolean isJson(String content) {
		try {
			JSONObject.fromObject(content);
			return true;
		} catch (Exception e) {
			try {
				JSONArray.fromObject(content);
				return true;
			} catch (Exception e2) {
				return false;
			}
		}
	}
	
	/**
	 * 获取中文首字母
	 * 
	 * @param c 中文字符串
	 * @return
	 */
	public static String chineseToFirstLetter(String c) {
		String string = "";
		int a = c.length();
		for (int k = 0; k < a; k++) {
			String d = String.valueOf(c.charAt(k));
			String str = converterToFirstSpell(d);
			String s = str.toUpperCase();
			char h;
			for (int y = 0; y <= 0; y++) {
				h = s.charAt(0);
				string += h;
			}
		}
		return string;
	}

	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			String s = String.valueOf(nameChar[i]);
			if (s.matches("[\\u4e00-\\u9fa5]")) {
				try {
					String[] mPinyinArray = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
					pinyinName += mPinyinArray[0];
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

	/**
	 * 纳秒时间戳和随机数生成唯一的随机数
	 * 
	 * @return
	 */
	public static String getUniqueKey() {
		Random random = new Random();
		Integer number = random.nextInt(900000000) + 100000000;
		return System.currentTimeMillis() + String.valueOf(number);
	}
	
	/**
	 * 通过身份证号码获取出生日期、性别、年龄
	 * 
	 * @param certificateNo
	 * @return 返回的出生日期格式：1990-01-01 性别格式：F-女，M-男
	 */
	public static Map<String, String> getBirAgeSex(String certificateNo) {
		String birthday = "";
		String age = "";
		String sexCode = "";

		int year = Calendar.getInstance().get(Calendar.YEAR);
		char[] number = certificateNo.toCharArray();
		boolean flag = true;
		if (number.length == 15) {
			for (int x = 0; x < number.length; x++) {
				if (!flag)
					return new HashMap<String, String>();
				flag = Character.isDigit(number[x]);
			}
		} else if (number.length == 18) {
			for (int x = 0; x < number.length - 1; x++) {
				if (!flag)
					return new HashMap<String, String>();
				flag = Character.isDigit(number[x]);
			}
		}
		if (flag && certificateNo.length() == 15) {
			birthday = "19" + certificateNo.substring(6, 8) + "-" + certificateNo.substring(8, 10) + "-"
					+ certificateNo.substring(10, 12);
			sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 3, certificateNo.length()))
					% 2 == 0 ? "F" : "M";
			age = (year - Integer.parseInt("19" + certificateNo.substring(6, 8))) + "";
		} else if (flag && certificateNo.length() == 18) {
			birthday = certificateNo.substring(6, 10) + "-" + certificateNo.substring(10, 12) + "-"
					+ certificateNo.substring(12, 14);
			sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 4, certificateNo.length() - 1))
					% 2 == 0 ? "F" : "M";
			age = (year - Integer.parseInt(certificateNo.substring(6, 10))) + "";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("birthday", birthday);
		map.put("age", age);
		map.put("sexCode", sexCode);
		return map;
	}
	
	/**
	 * 
	     * @Title: timeFormat
	     * @Description: 时间转换
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */ 
	public static String timeFormat(String time) throws Exception {
		String returnTime = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
		SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date dateTime = formatter.parse(time);
		long interval = (new Date().getTime() - dateTime.getTime())/1000;
		if(interval <= 0){
            returnTime = "刚刚";
        }else if(interval > 0 && interval < 60){
			returnTime = interval + "秒前";
		}else if(interval >= 60 && interval < 3600){
			returnTime = interval/60 + "分钟前";
		}else if(interval >= 3600 && interval <= 3600 * 24){
			returnTime = interval/3600 + "小时前";
		}else if(interval >= 3600 * 24 && interval <= 3600 * 48){
			returnTime = "昨天 " + hm.format(dateTime);
		}else if(interval >= 3600 * 48 && interval <= 3600 * 72){
			returnTime = "前天 " + hm.format(dateTime);
		}else{
			returnTime = ymdhm.format(dateTime);
		}
		return returnTime;
	}
	
	/**
	 * 获取服务器文件夹
	 * @param host 服务器连接地址-邮箱收件服务器
	 * @param userName 邮箱地址
	 * @param password 邮箱密码或者授权码
	 * @param storeType 邮箱类型
	 * @param folder 邮箱文件夹
	 * @return
	 * @throws Exception
	 */
	public static Folder getFolderByServer(String host, String userName, String password, String storeType, String folder) throws Exception{
		//配置文件
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		Store store = session.getStore(storeType);
		store.connect(host, userName, password);//登录链接邮箱服务器
		return store.getFolder(folder);
	}
	
	/**
	 * 判断保存附件的文件夹是否存在，不存在则创建
	 * @param basePath
	 */
	public static void createFolder(String basePath){
		// 创建目录
		File pack = new File(basePath);
		if(!pack.isDirectory()){
			// 目录不存在 
			try {
				// 创建目录
				pack.mkdirs();
			} catch (Exception e) {
				logger.warn("create folder [{}] failed.", basePath);
			}
		}
	}
	
	/**
	 * 判断该邮件在邮箱中是否包含
	 * @param emailHasMail
	 * @param messageId
	 * @return false 不存在；true 存在
	 */
	public static boolean judgeInListByMessage(List<Map<String, Object>> emailHasMail, String messageId){
		if(emailHasMail != null && !emailHasMail.isEmpty() && !ToolUtil.isBlank(messageId)){
			for(Map<String, Object> bean : emailHasMail){
				if(bean.containsKey("messageId") && messageId.equals(bean.get("messageId").toString())){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取邮件内容-使用工具类对邮件进行解析
	 * @param re 工具对象
	 * @param message 邮件内容体
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getEmailMationByUtil(ShowMail re, Message message) throws Exception{
		Map<String, Object> bean = new HashMap<>();
		re.setDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
		bean.put("title", re.getSubject());//标题
		bean.put("sendDate", re.getSentDate());//发送时间
		//是否需要回复   1.需要   2.不需要
		if(re.getReplySign()){
			bean.put("replaySign", 1);
		}else{
			bean.put("replaySign", 2);
		}
		//是否已读   1.已读  2.未读
		if(re.isNew()){
			bean.put("isNew", 1);
		}else{
			bean.put("isNew", 2);
		}
		//是否包含附件  1.是  2.否
		if(re.isContainAttach((Part) message)){
			bean.put("isContainAttach", 1);
		}else{
			bean.put("isContainAttach", 2);
		}
		bean.put("fromPeople", re.getAddressFrom());//发件人
		bean.put("toPeople", re.getMailAddress("to"));//收件人
		bean.put("toCc", re.getMailAddress("cc"));//抄送人
		bean.put("toBcc", re.getMailAddress("bcc"));//暗送
		bean.put("messageId", re.getMessageId());//消息id
		String contentType = ((Part) message).getContentType();
		boolean plainFlag;
		if (contentType.startsWith("text/plain")) {
			plainFlag = true;
		}else{
			plainFlag = false;
		}
		re.getMailContent((Part) message, plainFlag);//设置内容
		bean.put("content", re.getBodyText());//邮件内容
		bean.put("createTime", re.getSentDate());//创建时间
		return bean;
	}
	
	/**
	 * java将字符串转换成可执行代码 工具类
	 *
	 * @param jexlExp
	 * @param map
	 * @return
	 */
	public static Object convertToCode(String jexlExp, Map<String, String> map) {
		JexlEngine jexl = new JexlEngine();
		Expression expression = jexl.createExpression(jexlExp);
		JexlContext jc = new MapContext();
		for (String key : map.keySet()) {
			jc.set(key, map.get(key));
		}
		if (null == expression.evaluate(jc)) {
			return "";
		}
		return expression.evaluate(jc);
	}

}
