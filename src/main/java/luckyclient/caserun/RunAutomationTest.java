package luckyclient.caserun;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exappium.AppTestControl;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebTestControl;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.entity.TaskExecute;
import luckyclient.serverapi.entity.TaskScheduling;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author： seagull
 * 
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class RunAutomationTest extends TestControl {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.conf");
			String taskid = args[0];
			TaskExecute task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
			TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(Integer.valueOf(taskid));
			if (taskScheduling.getTaskType() == 0) {
				// 接口测试
				TestControl.taskExecutionPlan(task);
			} else if (taskScheduling.getTaskType() == 1) {
				// UI测试
				WebTestControl.taskExecutionPlan(task);
			} else if (taskScheduling.getTaskType() == 2) {
				AppTestControl.taskExecutionPlan(task);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("启动测试任务运行主函数出现异常，请检查！",e);
		} finally{
			System.exit(0);
		}
	}
}
