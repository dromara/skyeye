/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.util;

import com.skyeye.common.constans.Constants;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @ClassName: SysServerUtil
 * @Description: 获取服务器的信息
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/19 13:43
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SysServerUtil {

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
                if(!ToolUtil.isBlank(BytesUtil.substring(line, kmtidx, rocidx - 1).trim())){
                    kneltime += Long.valueOf(BytesUtil.substring(line, kmtidx, rocidx - 1).replaceAll(" ", "").trim()).longValue();
                }
                if(!ToolUtil.isBlank(BytesUtil.substring(line, umtidx, wocidx - 1).trim())){
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

}
