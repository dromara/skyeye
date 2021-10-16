package com.youbenzi.md2.export;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFDecorator5xHelper extends PdfPageEventHelper {

	private static Logger LOGGER = LoggerFactory.getLogger(PDFDecorator5xHelper.class);
	
	private String sysWaterMark;

	private int presentFontSize = 10;

	// 模板
	public PdfTemplate total;
	
	// 基础字体对象
    public BaseFont bf = null;
    
    public PDFDecorator5xHelper(){
    	
    }
    
    public PDFDecorator5xHelper(String sysWaterMark){
    	this.sysWaterMark = sysWaterMark;
    }

	/**
	 * 
	 * Title: onOpenDocument Description: 文档打开时创建模板
	 * 
	 * @param pdfWriter
	 * @param document
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	@Override
	public void onOpenDocument(PdfWriter pdfWriter, Document document) {
		try {
			// 共 页 的矩形的长宽高
			total = pdfWriter.getDirectContent().createTemplate(50, 50);
			bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Title: onEndPage Description: 关闭每页的时候，写入页眉，写入'第几页共'这几个字。
	 * 
	 * @param pdfWriter
	 * @param document
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	@Override
	public void onEndPage(PdfWriter pdfWriter, Document document) {
		try {
			loadPageMation(pdfWriter, document);
			loadWaterMark(pdfWriter);
		} catch (Exception e) {
			LOGGER.info("PDFDecorator5xHelper onEndPage is {}", e);
		}
	}

	/**
	 * @throws Exception
	 * 
	 * @Title: loadWaterMark @Description: 加水印 @throws
	 *         DocumentException @return: void @throws
	 */
	private void loadWaterMark(PdfWriter pdfWriter) throws Exception {
		// 加入水印
		PdfContentByte waterMar = pdfWriter.getDirectContent();
		// 开始设置水印
		waterMar.beginText();
		// 设置水印透明度
		PdfGState gs = new PdfGState();
		try {
			// 设置透明度为0.4
			gs.setFillOpacity(0.4f);
			gs.setStrokeOpacity(0.4f);
			// 设置水印字体大小
			waterMar.setFontAndSize(bf, 15);
			// 设置透明度
			waterMar.setGState(gs);
			// 设置水印对齐方式 水印内容 X坐标 Y坐标 旋转角度
			waterMar.showTextAligned(Element.ALIGN_CENTER, sysWaterMark, 300, 350, 45);
			// 设置水印颜色
			waterMar.setColorFill(BaseColor.GRAY);
			// 结束设置
			waterMar.endText();
			waterMar.stroke();
		} finally {
			waterMar = null;
			gs = null;
		}
	}

	private void loadPageMation(PdfWriter pdfWriter, Document document) throws Exception {
		// 拿到当前的PdfContentByte,如果用getDirectContentUnder方法，如果PDF中有图片，那么水印会被覆盖
		PdfContentByte waterMar = pdfWriter.getDirectContent();
		// 设置水印字体参数(字体参数，字体编码格式，是否将字体信息嵌入到pdf中（一般不需要嵌入）
		BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		Font fontDetail = new Font(bf, presentFontSize, Font.NORMAL);// 数据体字体
		// 1.写入页眉
		ColumnText.showTextAligned(waterMar, Element.ALIGN_LEFT, new Phrase(sysWaterMark, fontDetail),
				document.left() + 50, document.top() + 20, 0);
		// 2.写入前半部分的 第 X页/共
		int pageS = pdfWriter.getPageNumber();
		String foot1 = "第 " + pageS + " 页 /共";
		Phrase footer = new Phrase(foot1, fontDetail);
		// 3.计算前半部分的foot1的长度，后面好定位最后一部分的'Y页'这俩字的x轴坐标，字体长度也要计算进去 = len
		float len = bf.getWidthPoint(foot1, presentFontSize);
		// 4.写入页脚1，x轴就是(右margin+左margin + right() -left()- len)/2.0F
		// 再给偏移20F适合人类视觉感受，否则肉眼看上去就太偏左了
		// ,y轴就是底边界-20,否则就贴边重叠到数据体里了就不是页脚了；注意Y轴是从下往上累加的，最上方的Top值是大于Bottom好几百开外的。
		ColumnText.showTextAligned(waterMar, Element.ALIGN_CENTER, footer,
				(document.rightMargin() + document.right() + document.leftMargin() - document.left() - len) / 2.0F
						+ 20F,
				document.bottom() - 20, 0);
		// 5.写入页脚2的模板（就是页脚的Y页这俩字）添加到文档中，计算模板的和Y轴,X=(右边界-左边界 - 前半部分的len值)/2.0F +
		// len ， y
		// 轴和之前的保持一致，底边界-20
		waterMar.addTemplate(total,
				(document.rightMargin() + document.right() + document.leftMargin() - document.left()) / 2.0F + 20F,
				document.bottom() - 20); // 调节模版显示的位置
	}

	/**
	 * 
	 * Title: onCloseDocument Description: 关闭文档时，替换模板，完成整个页眉页脚组件
	 * 
	 * @param pdfWriter
	 * @param doc
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	@Override
	public void onCloseDocument(PdfWriter pdfWriter, Document doc) {
		// 6.最后一步了，就是关闭文档的时候，将模板替换成实际的 Y 值,至此，page x of y 制作完毕，完美兼容各种文档size。
		total.beginText();
		total.setFontAndSize(bf, presentFontSize);// 生成的模版的字体、颜色
		String foot2 = " " + (pdfWriter.getPageNumber() - 1) + " 页";
		total.showText(foot2);// 模版显示的内容
		total.endText();
		total.closePath();
	}
}
