/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.util;

import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.SaveFormat;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: OfficeUtils
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/30 14:56
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class OfficeUtils {

    /**
     * 验证aspose.word组件是否授权：无授权的文件有水印标记
     * 需要使用（aspose-words-15.8.0-jdk16.jar），版本要对应。无水印
     *
     * @return
     */
    public static boolean isWordLicense() {
        boolean result = false;
        try {
            String s = "<License><Data><Products><Product>Aspose.Total for Java</Product><Product>Aspose.Words for Java</Product></Products><EditionType>Enterprise</EditionType><SubscriptionExpiry>20991231</SubscriptionExpiry><LicenseExpiry>20991231</LicenseExpiry><SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber></Data><Signature>sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=</Signature></License>";
            ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
            com.aspose.words.License license = new com.aspose.words.License();
            license.setLicense(inputStream);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //outputStream转inputStream
    public static ByteArrayInputStream parse(OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }

    /**
     * word和txt文件转换图片
     *
     * @param inputStream
     * @param
     * @return
     * @throws Exception
     */
    public static List<BufferedImage> wordToImg(InputStream inputStream) throws Exception {
        if (!isWordLicense()) {
            return null;
        }
        try {
            Date start = new Date();
            Document doc = new Document(inputStream);
            ImageSaveOptions options = new ImageSaveOptions(SaveFormat.PNG);
            options.setPrettyFormat(true);
            options.setUseAntiAliasing(true);
            options.setUseHighQualityRendering(true);
            int pageCount = doc.getPageCount();
            //生成前pageCount张，这可以限制输出长图时的页数（方法入参可以传值pageNum）
           /*if (pageCount > pageNum) {
               pageCount = pageNum;
           }*/
            List<BufferedImage> imageList = new ArrayList<>();
            for (int i = 0; i < pageCount; i++) {
                OutputStream output = new ByteArrayOutputStream();
                options.setPageIndex(i);
                doc.save(output, options);
                ImageInputStream imageInputStream = javax.imageio.ImageIO.createImageInputStream(parse(output));
                imageList.add(javax.imageio.ImageIO.read(imageInputStream));
            }
            List<BufferedImage> imageList2 = new ArrayList<BufferedImage>();
            //这个重新生成新的图片是因为直接输出的图片底色为红色
            for (int j = 0; j < imageList.size(); j++) {
                // 生成新图片
                BufferedImage destImage = imageList.get(j);
                int w1 = destImage.getWidth();
                int h1 = destImage.getHeight();
                destImage = new BufferedImage(w1, h1, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = (Graphics2D) destImage.getGraphics();
                g2.setBackground(Color.LIGHT_GRAY);
                g2.clearRect(0, 0, w1, h1);
                g2.setPaint(Color.RED);
                // 从图片中读取RGB
                int[] ImageArrayOne = new int[w1 * h1];
                ImageArrayOne = imageList.get(j).getRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 逐行扫描图像中各个像素的RGB到数组中
                destImage.setRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
                imageList2.add(destImage);
            }
            Date end = new Date();
            long l = end.getTime() - start.getTime();
            long hour = l / (1000 * 60 * 60);
            long min = (l - hour * (1000 * 60 * 60)) / (1000 * 60);
            long s = (l - hour * (1000 * 60 * 60) - min * 1000 * 60) / (1000);
            long ss = (l - hour * (1000 * 60 * 60) - min * 1000 * 60 - s * 1000) / (1000 / 60);
            System.out.println("word转图片时间:" + min + "分" + s + "秒" + ss + "毫秒");//hour+"小时"+
            return imageList2;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 合并任数量的图片成一张图片
     *
     * @param isHorizontal true代表水平合并，fasle代表垂直合并
     * @param imgs         待合并的图片数组
     * @return
     * @throws IOException
     */
    public static BufferedImage mergeImage(boolean isHorizontal, List<BufferedImage> imgs) throws IOException {
        // 生成新图片
        BufferedImage destImage = null;
        // 计算新图片的长和高
        int allw = 0, allh = 0, allwMax = 0, allhMax = 0;
        // 获取总长、总宽、最长、最宽
        for (int i = 0; i < imgs.size(); i++) {
            BufferedImage img = imgs.get(i);
            allw += img.getWidth();
            if (imgs.size() != i + 1) {
                allh += img.getHeight() + 5;
            } else {
                allh += img.getHeight();
            }
            if (img.getWidth() > allwMax) {
                allwMax = img.getWidth();
            }
            if (img.getHeight() > allhMax) {
                allhMax = img.getHeight();
            }
        }
        // 创建新图片
        if (isHorizontal) {
            destImage = new BufferedImage(allw, allhMax, BufferedImage.TYPE_INT_RGB);
        } else {
            destImage = new BufferedImage(allwMax, allh, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g2 = (Graphics2D) destImage.getGraphics();
        g2.setBackground(Color.LIGHT_GRAY);
        g2.clearRect(0, 0, allw, allh);
        g2.setPaint(Color.RED);

        // 合并所有子图片到新图片
        int wx = 0, wy = 0;
        for (int i = 0; i < imgs.size(); i++) {
            BufferedImage img = imgs.get(i);
            int w1 = img.getWidth();
            int h1 = img.getHeight();
            // 从图片中读取RGB
            int[] ImageArrayOne = new int[w1 * h1];
            ImageArrayOne = img.getRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 逐行扫描图像中各个像素的RGB到数组中
            if (isHorizontal) { // 水平方向合并
                destImage.setRGB(wx, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            } else { // 垂直方向合并
                destImage.setRGB(0, wy, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            }
            wx += w1;
            wy += h1 + 5;
        }
        return destImage;
    }


    // 测试工具类
    public static void main(String[] args) {
        //word转图片格式
        try {
            File file = new File("G:\\test\\test22.docx");
            try (InputStream inStream = new FileInputStream(file)) {
                            List<BufferedImage> wordToImg = wordToImg(inStream);
                            //for(int i=0; i<wordToImg.size(); i++){ //可以保存图片（每页保存为一张）
                            //    ImageIO.write(wordToImg.get(i), "jpg", new File("G:\\test\\"+ i +".png")); //将其保存在C:/imageSort/targetPIC/下
                            //}
                            BufferedImage mergeImage = mergeImage(false, wordToImg);
                            //保存图片（长图）
                            ImageIO.write(mergeImage, "jpg", new File("G:\\test\\test.png"));
                        } catch (Exception e) {
                            e.printStackTrace();
            }
        }
    }

}