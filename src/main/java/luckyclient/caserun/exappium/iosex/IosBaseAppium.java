package luckyclient.caserun.exappium.iosex;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.Augmenter;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.ios.IOSTouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import luckyclient.publicclass.LogUtil;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * @author： seagull 
 * @date 2018年2月2日
 * 
 */
public class IosBaseAppium {

	/**
	 * @param args
	 * @throws IOException
	 * IOS手机报错截图
	 */
	public static void screenShot(IOSDriver<IOSElement> appium, String imagname){
		imagname = imagname + ".png";
		String relativelyPath = System.getProperty("user.dir");
		String pngpath=relativelyPath +File.separator+ "log"+File.separator+"ScreenShot" +File.separator+ imagname;
		
		try {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LogUtil.APP.error("IOS手机报错截图休眠出现异常", e);
			}
			File imageFile = ((TakesScreenshot) (new Augmenter().augment(appium))).getScreenshotAs(OutputType.FILE);
			File screenFile = new File(pngpath);
			FileUtils.copyFile(imageFile, screenFile);
			imageFile.deleteOnExit();
			LogUtil.APP
			.info("已对当前界面进行截图操作，可通过用例执行界面的日志明细查看，也可以前往客户端上查看...【{}】",pngpath);
		} catch (IOException e) {
			LogUtil.APP.error("IOS手机报错截图出现异常", e);
		}
	}
	
	/**
	 * @param args
	 * @throws IOException
	 * appium不支持中文输入 参考了robotium的以js方式为元素直接设置value的做法
	 * 利用Selenium中Webdriver执行js方法实现中文输入
	 */
	public static void sendChinese(IOSDriver<IOSElement> appium, String preferences, String value) {
		org.openqa.selenium.JavascriptExecutor jse = (org.openqa.selenium.JavascriptExecutor) appium;
		jse.executeScript("document.getElementByName('" + preferences + "').value='" + value + "'");
	}

	/**
	 * @param args
	 *            js webview 支持4.1～4.4
	 */
	public static void webViewSwipe(IOSDriver<IOSElement> appium, Double sX, Double sY, Double eX, Double eY, Double duration)
			throws Exception {
		JavascriptExecutor js;
		HashMap<String, Double> swipeObject;
		try {
			// 滑动
			js = (JavascriptExecutor) appium;
			swipeObject = new HashMap<String, Double>(5);
			swipeObject.put("startX", sX);
			swipeObject.put("startY", sY);
			swipeObject.put("endX", eX);
			swipeObject.put("endY", eY);
			swipeObject.put("duration", duration);
			js.executeScript("mobile: swipe", swipeObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("IOS手机滑动出现异常", e);
		} finally {
			// 释放变量
		}

	}

	/**
	 * @param args
	 *            调用 ADB直接滑动 支持4.1～4.4
	 */
	public static void adbSwipe(IOSDriver<IOSElement> appium, Double sX, Double sY, Double eX, Double eY) throws Exception {
		int xLine;
		int yLine;
		int sX2;
		int sY2;
		int eX2;
		int eY2;
		try {
			// 滑动
			xLine = appium.manage().window().getSize().getWidth();
			yLine = appium.manage().window().getSize().getHeight();

			sX2 = (int) (xLine * sX);
			sY2 = (int) (yLine * sY);
			eX2 = (int) (xLine * eX);
			eY2 = (int) (yLine * eY);
			// logger.info("滑动11111111");
			Runtime.getRuntime()
					.exec("adb -s " + "IOS" + " shell input swipe " + sX2 + " " + sY2 + " " + eX2 + " " + eY2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("IOS手机调用 ADB直接滑动出现异常", e);
		} finally {
			// 释放变量
		}

	}

	/**
	 * @param args
	 * 屏幕点击事件
	 */
	public static void clickScreenForJs(IOSDriver<IOSElement> drivers, int x, int y, int duration) {
		JavascriptExecutor js = (JavascriptExecutor) drivers;
		HashMap<String, Integer> tapObject = new HashMap<String, Integer>(3);
		tapObject.put("x", x);
		tapObject.put("y", y);
		tapObject.put("duration", duration);
		js.executeScript("mobile: tap", tapObject);
	}
	
	/**
	 * 拖住页面按屏幕比例向上滑动(手指向下，页面向上)
	 * @param driver
	 * @param second 持续时间
	 * @param num 滚动次数
	 */
	public static void swipePageUp(IOSDriver<IOSElement> driver, Double second, int num) {
		int nanos = (int) (second * 1000);
		Duration duration = Duration.ofNanos(nanos);
		int width = driver.manage().window().getSize().width;
		int height = driver.manage().window().getSize().height;
		IOSTouchAction action = new IOSTouchAction(driver);
		
		for (int i = 0; i <= num; i++) {
			action.press(PointOption.point(width / 2, height / 4)).waitAction(WaitOptions.waitOptions(duration))
					.moveTo(PointOption.point(width / 2, height / 2)).release().perform();
		}
	}

	/**
	 * 拖住页面按屏幕比例向下滑动(手指向上，页面向下)
	 * @param driver
	 * @param second
	 * @param num
	 */
	public static void swipePageDown(IOSDriver<IOSElement> driver,Double second,int num){
		int nanos = (int) (second * 1000);
		Duration duration = Duration.ofNanos(nanos);
		int width = driver.manage().window().getSize().width;
		int height = driver.manage().window().getSize().height;
		IOSTouchAction action = new IOSTouchAction(driver);
		for (int i = 0; i <= num; i++) {
			action.press(PointOption.point(width / 2, height / 2)).waitAction(WaitOptions.waitOptions(duration))
					.moveTo(PointOption.point(width / 2, height / 4)).release().perform();
		}
	}

	/**
	 * 拖住页面按屏幕比例向左滑动(手指向左，页面向左滚动)
	 * @param driver
	 * @param second
	 * @param num
	 */
	public static void swipePageLeft(IOSDriver<IOSElement> driver, Double second, int num) {
		int nanos = (int) (second * 1000);
		Duration duration = Duration.ofNanos(nanos);
		int width = driver.manage().window().getSize().width;
		int height = driver.manage().window().getSize().height;
		IOSTouchAction action = new IOSTouchAction(driver);
		for (int i = 0; i <= num; i++) {
			action.press(PointOption.point(width - 10, height / 2)).waitAction(WaitOptions.waitOptions(duration))
					.moveTo(PointOption.point(10, height / 2)).release().perform();
		}
	}

	/**
	 * 拖住页面按屏幕比例向右滑动(手指向右，页面向右)
	 * @param driver
	 * @param second
	 * @param num
	 */
	public static void swipePageRight(IOSDriver<IOSElement> driver, Double second, int num) {
		int nanos = (int) (second * 1000);
		Duration duration = Duration.ofNanos(nanos);
		int width = driver.manage().window().getSize().width;
		int height = driver.manage().window().getSize().height;
		IOSTouchAction action = new IOSTouchAction(driver);
		for (int i = 0; i <= num; i++) {
			action.press(PointOption.point(10, height / 2)).waitAction(WaitOptions.waitOptions(duration))
					.moveTo(PointOption.point(width - 10, height / 2)).release().perform();
		}
	}
     
}
