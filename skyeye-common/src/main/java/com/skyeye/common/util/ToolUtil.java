package com.skyeye.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.ObjectConstant;

public class ToolUtil {
	
	/**
	 * 
	     * @Title: useLoop
	     * @Description: 检查数组是否包含某个值的方法
	     * @param @param arr
	     * @param @param targetValue
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean useLoop(String[] arr, String targetValue) {
		for (String s : arr) {
			if (s.equals(targetValue))
				return true;
		}
		return false;
	}
	
	/**
	 * 
	     * @Title: getUrlParams
	     * @Description: 将url参数转换成map
	     * @param @param param aa=11&bb=22&cc=33
	     * @param @return    参数
	     * @return Map<String,Object>    返回类型
	     * @throws
	 */
	public static Map<String, Object> getUrlParams(String param) {
		Map<String, Object> map = new HashMap<String, Object>();
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
	     * @param @param str
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
	     * @param @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	     * @Title: isPhone
	     * @Description: 检测手机号是否合格
	     * @param @param mobiles
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isPhone(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	/**
	 * 
	     * @Title: isEmail
	     * @Description: 验证邮箱
	     * @param @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isEmail(String str) {
		String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		return match(regex, str);
	}
	
	/**
	 * 
	     * @Title: isIP
	     * @Description: 验证IP地址
	     * @param @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isIP(String str) {
		String num = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
		String regex = "^" + num + "\\." + num + "\\." + num + "\\." + num + "$";
		return match(regex, str);
	}
	
	/**
	 * 
	     * @Title: isDate
	     * @Description: 验证日期时间
	     * @param @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isDate(String str) {
		String regex = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$";
		return match(regex, str);
	}
	
	/**
	 * 
	     * @Title: IsUrl
	     * @Description: 验证网址Url
	     * @param @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isUrl(String str) {
		String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
		return match(regex, str);
	}
	
	/**
	 * 
	     * @Title: IsPostalcode
	     * @Description: 验证输入邮政编号
	     * @param @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isPostalcode(String str) {
		String regex = "^\\d{6}$";
		return match(regex, str);
	}
	
	/**
	 * 
	     * @Title: match
	     * @Description: 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	     * @param @param regex
	     * @param @param str
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
	 * 
	     * @Title: IsIDcard
	     * @Description: 验证输入身份证号
	     * @param @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isIDcard(String str) {
		String regex = "(^\\d{18}$)|(^\\d{15}$)";
		return match(regex, str);
	}
	
	/**
	 * 
	     * @Title: isDouble
	     * @Description: 验证小数点后两位,一般用于金钱验证
	     * @param @param str
	     * @param @return    参数
	     * @return boolean    返回类型
	     * @throws
	 */
	public static boolean isDouble(String str) {
		String regex = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";
		return match(regex, str);
	}
	
	/**
	 * 
	     * @Title: getTimeAndToString
	     * @Description: 获取当前日期(2016-12-29 11:23:09)
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public static String getTimeAndToString() {
		Date dt = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = df.format(dt);
		return nowTime;
	}
	
	/**
	 * 
	     * @Title: getProperties
	     * @Description: 读取.properties 结尾的配置文件用，getP, getParam
	     * @param @param path
	     * @param @return
	     * @param @throws Exception    参数
	     * @return Map<String,String>    返回类型
	     * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String,String> getProperties(String path) throws Exception{  
        Resource resource = new ClassPathResource(path);  
        Properties props = PropertiesLoaderUtils.loadProperties(resource);  
		Map<String, String> param = new HashMap<String, String>((Map) props);  
        return param;  
    }  
	
	/**
	 * 
	     * @Title: containsBoolean
	     * @Description: 请求xml参数判断
	     * @param @param ref
	     * @param @param str
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public static String containsBoolean(String [] ref,String str){
		for(String s : ref){
			switch (s.toLowerCase()) {
			case ObjectConstant.REQUIRED://不为空
				if(ToolUtil.isBlank(str))
					return "不能为空";
				break;
			case ObjectConstant.NUM://数字类型不正确
				if (!ToolUtil.isBlank(str))
					if (!ToolUtil.isNumeric(str))
						return "数字类型不正确";
				break;
			case ObjectConstant.DATE://时间类型不正确
				if (!ToolUtil.isBlank(str))
					if(!ToolUtil.isDate(str))
						return "时间类型不正确";
				break;
			case ObjectConstant.EMAIL://邮箱类型不正确
				if (!ToolUtil.isBlank(str))
					if(!ToolUtil.isEmail(str))
						return "邮箱类型不正确";
				break;
			case ObjectConstant.IDCARD://证件号类型不正确
				if (!ToolUtil.isBlank(str))
					if(!ToolUtil.isIDcard(str))
						return "证件号类型不正确";
				break;
			case ObjectConstant.PHONE://手机号类型不正确
				if (!ToolUtil.isBlank(str))
					if(!ToolUtil.isPhone(str))
						return "手机号类型不正确";
				break;
			case ObjectConstant.URL://请求链接类型不正确
				if (!ToolUtil.isBlank(str))
					if(!ToolUtil.isUrl(str))
						return "请求链接类型不正确";
				break;
			case ObjectConstant.IP://请求IP类型不正确
				if (!ToolUtil.isBlank(str))
					if(!ToolUtil.isIP(str))
						return "请求IP类型不正确";
				break;
			case ObjectConstant.POSTCODE://国内邮编类型不正确
				if (!ToolUtil.isBlank(str))
					if(!ToolUtil.isPostalcode(str))
						return "国内邮编类型不正确";
				break;
			case ObjectConstant.DOUBLE://验证小数点后两位,一般用于金钱验证不正确
				if (!ToolUtil.isBlank(str))
					if(!ToolUtil.isDouble(str))
						return "小数格式类型不正确";
				break;
			default:
				break;
			}
		}
		return null;
	}
	
	/**
	 * 
	     * @Title: getSurFaceId
	     * @Description: 获取ID
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public static String getSurFaceId() {
		UUID uuid = java.util.UUID.randomUUID();
		return "" + uuid.toString().replaceAll("-", "");
	}
	
	/**
	 * 
	     * @Title: MD5
	     * @Description: MD5加密技术
	     * @param @param str
	     * @param @return
	     * @param @throws Exception    参数
	     * @return String    返回类型
	     * @throws
	 */
	public static String MD5(String str) throws Exception {
		byte[] bt = str.getBytes();
		StringBuffer sbf = null;
		MessageDigest md = null;
		md = MessageDigest.getInstance("MD5");
		byte[] bt1 = md.digest(bt);
		sbf = new StringBuffer();
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
     * @param treeNodes
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
    @SuppressWarnings("unchecked")
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
     * 获取ip.properties路径
     * @return
     */
    public static String getIPPropertiesPath(){
    	String contextPath = new Object() {
    		public String getPath() {
    			return this.getClass().getResource("/").getPath();
    		}
	    }.getPath().substring(1);
		String path = contextPath + "/properties/ip.properties";
    	return path;
    }
    
    /**
	 * 删除单个文件
	 *
	 * @param fileName
	 *            要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
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
	 * @param s
	 * @return
	 */
	public static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}
	
	/**
	 * 写入内容到文件
	 * @param content
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static boolean writeTxtFile(String content, File fileName) throws Exception {
		RandomAccessFile mm = null;
		boolean flag = false;
		FileOutputStream o = null;
		try {
			o = new FileOutputStream(fileName);
			o.write(content.getBytes("GBK"));
			o.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mm != null) {
				mm.close();
			}
		}
		return flag;
	}
	
	/**
	 * 
	     * @Title: getDateStr
	     * @Description: 将日期转化为正常的年月日时分秒
	     * @param @param timeStmp
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public static String getDateStr(long timeStmp) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date(timeStmp));
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
		for(String str : Constants.FILTER_FILE_IP_OPTION) {
			if (ip.indexOf(str) != -1) {
				ip = request.getParameter("loginPCIp");
				break;
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
//			Thread.sleep(Constants.CPUTIME);
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
				// ThreadCount,UserModeTime,WriteOperation
				String caption = Bytes.substring(line, capidx, cmdidx - 1).trim();
				String cmd = Bytes.substring(line, cmdidx, kmtidx - 1).trim();
				if (cmd.indexOf("wmic.exe") >= 0) {
					continue;
				}
				// log.info("line="+line);
				if (caption.equals("System Idle Process") || caption.equals("System")) {
					idletime += Long.valueOf(Bytes.substring(line, kmtidx, rocidx - 1).trim()).longValue();
					idletime += Long.valueOf(Bytes.substring(line, umtidx, wocidx - 1).trim()).longValue();
					continue;
				}
				if(!isBlank(Bytes.substring(line, kmtidx, rocidx - 1).trim())){
					kneltime += Long.valueOf(Bytes.substring(line, kmtidx, rocidx - 1).trim()).longValue();
				}
				if(!isBlank(Bytes.substring(line, umtidx, wocidx - 1).trim())){
					usertime += Long.valueOf(Bytes.substring(line, umtidx, wocidx - 1).trim()).longValue();
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

	
	public static void main(String[] args) throws Exception {
//		String str = "123456";
//		for(int i = 0; i < 5; i++){
//			str = MD5(str);
//		}
//		System.out.println(str);
		System.out.println(replaceUnderLineAndUpperCase("code_model"));
	}

}
