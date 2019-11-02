package luckyclient.caserun;

import luckyclient.caserun.exappium.AppTestControl;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebTestControl;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerApi;
import luckyclient.serverapi.entity.TaskExecute;
import luckyclient.serverapi.entity.TaskScheduling;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * 
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class RunAutomationTest extends TestControl {
	public static void main(String[] args) {
//	public static void runAutomationTestdebug(String taskid) {

		// TODO Auto-generated method stub
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.conf");//debug ���������ļ�·����\src\main\Resources\
			String taskid = args[0];
			TaskExecute task = GetServerApi.cgetTaskbyid(Integer.valueOf(taskid));
			TaskScheduling taskScheduling = GetServerApi.cGetTaskSchedulingByTaskId(Integer.valueOf(taskid));
			if (taskScheduling.getTaskType() == 0) {
				// �ӿڲ���
				TestControl.taskExecutionPlan(task);
			} else if (taskScheduling.getTaskType() == 1) {
				// UI����
				WebTestControl.taskExecutionPlan(task);
			} else if (taskScheduling.getTaskType() == 2) {
				AppTestControl.taskExecutionPlan(task);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("���������������������������쳣�����飡",e);
		} finally{
			System.exit(0);
		}
	}
}
