package luckyclient.dblog;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.api.PostServerAPI;
import luckyclient.serverapi.entity.TaskExecute;
import luckyclient.serverapi.entity.TaskScheduling;

/**
 * 
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年4月23日
 */
public class LogOperation {
	static int exetype = DbLink.exetype;

	/**
	 * 插入用例执行状态 0通过 1失败 2锁定 3执行中 4未执行
	 */
	public void insertTaskCaseExecute(String taskIdStr, Integer projectId,Integer caseId,  String caseSign,String caseName, Integer caseStatus) {
		if (0 == exetype) {
			Integer taskId=Integer.valueOf(taskIdStr);
			PostServerAPI.clientPostInsertTaskCaseExecute(taskId, projectId, caseId, caseSign, caseName, caseStatus);
		}
	}

	/**
	 * 更新用例执行状态 0通过 1失败 2锁定 3执行中 4未执行
	 */
	public void updateTaskCaseExecuteStatus(String taskIdStr, Integer caseId, Integer caseStatus) {
		if (0 == exetype) {
			Integer taskId=Integer.valueOf(taskIdStr);
			PostServerAPI.clientUpdateTaskCaseExecuteStatus(taskId, caseId, caseStatus);
		}
	}

	/**
	 * 插入用例执行日志
	 */
	public void insertTaskCaseLog(String taskIdStr, Integer caseId, String logDetail, String logGrade, String logStep,
			String imgname) {
		if (0 == exetype) {
			if (logDetail.length()>5000) {
				 LogUtil.APP.info("第{}步，日志级别{}，日志明细【{}】...日志明细超过5000字符，无法进入数据库存储，进行日志明细打印...",logStep,logGrade,logDetail);
				 logDetail="日志明细超过5000字符无法存入数据库，已在LOG4J日志中打印，请前往查看...";
			}
			
			Integer taskId=Integer.valueOf(taskIdStr);
			PostServerAPI.clientPostInsertTaskCaseLog(taskId, caseId, logDetail, logGrade, logStep, imgname);
		}
	}

	/**
	 * 更新本次任务的执行统计情况
	 * 状态 0未执行 1执行中 2执行完成 3执行失败 4唤起客户端失败
	 */
	public static int[] updateTaskExecuteData(String taskIdStr, int caseCount, int taskStatus) {
		int[] taskcount = null;
		if (0 == exetype) {
			Integer taskId = Integer.parseInt(taskIdStr);
			String str = PostServerAPI.clientUpdateTaskExecuteData(taskId, caseCount,taskStatus);
			JSONObject jsonObject = JSONObject.parseObject(str);

			// 返回本次任务执行情况
			taskcount = new int[5];
			taskcount[0] = jsonObject.getInteger("caseCount");
			taskcount[1] = jsonObject.getInteger("caseSuc");
			taskcount[2] = jsonObject.getInteger("caseFail");
			taskcount[3] = jsonObject.getInteger("caseLock");
			taskcount[4] = jsonObject.getInteger("caseNoExec");

		}
		return taskcount;
	}

	/**
	 * 更新本次任务的执行状态
	 * 状态 0未执行 1执行中 2执行完成 3执行失败 4唤起客户端失败
	 */
	public static void updateTaskExecuteStatusIng(String taskIdStr, int caseCount) {
		if (0 == exetype) {
			Integer taskId = Integer.parseInt(taskIdStr);
			PostServerAPI.clientUpdateTaskExecuteData(taskId, caseCount,1);
		}
	}

	/**
	 * 删除单次任务指定的用例日志明细
	 */
	public static void deleteTaskCaseLog(Integer caseId, String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		PostServerAPI.clientDeleteTaskCaseLog(taskId, caseId);
	}

	/**
	 * 取出指定任务ID中的不属于成功状态的用例ID
	 */
	public List<Integer> getCaseListForUnSucByTaskId(String taskIdStr) {
		int taskId = Integer.parseInt(taskIdStr);
		return GetServerAPI.clientGetCaseListForUnSucByTaskId(taskId);
	}

	/**
	 * 取出指定任务ID中所属的调度是否要发送邮件状态及收件人地址 发送邮件通知时的具体逻辑, -1-不通知 0-全部，1-成功，2-失败
	 * 发送 eMailer varchar(100) ; --收件人
	 */

	public static String[] getEmailAddress(String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		String[] address = null;
		try {
			TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(taskId);
			if (!taskScheduling.getEmailSendCondition().equals(-1)) {
				String temp = taskScheduling.getEmailAddress();
				// 清除最后一个;
				if (temp.indexOf(";") > -1 && temp.substring(temp.length() - 1, temp.length()).indexOf(";") > -1) {
					temp = temp.substring(0, temp.length() - 1);
				}
				// 多个地址
				if (temp.indexOf("null") <= -1 && temp.indexOf(";") > -1) {
					address = temp.split(";", -1);
					// 一个地址
				} else if (temp.indexOf("null") <= -1 && temp.indexOf(";") <= -1) {
					address = new String[1];
					address[0] = temp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("获取邮件收件人地址出现异常，请检查！",e);
			return address;
		}
		return address;
	}

	/**
	 * 取出指定任务ID中所属的调度是否要自动构建以及构建的项目名称 为空时不构建
	 */
	public static String[] getBuildName(String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		String[] buildname = null;
		try {
			TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(taskId);
			if (null == taskScheduling.getBuildingLink() || "".equals(taskScheduling.getBuildingLink())) {
				return buildname;
			}else{
				String temp = taskScheduling.getBuildingLink();
				// 清除最后一个;
				if (temp.indexOf(";") > -1 && temp.substring(temp.length() - 1, temp.length()).indexOf(";") > -1) {
					temp = temp.substring(0, temp.length() - 1);
				}
				// 多个名称
				if (temp.indexOf("null") <= -1 && temp.indexOf(";") > -1) {
					buildname = temp.split(";", -1);
					// 一个名称
				} else if (temp.indexOf("null") <= -1 && temp.indexOf(";") <= -1) {
					buildname = new String[1];
					buildname[0] = temp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("获取构建地址出现异常，请检查！",e);
			return buildname;
		}
		return buildname;
	}

	/**
	 * 取出指定任务ID中所属的调度是否要自动重启TOMCAT
	 * 自动重启 restartcomm varchar(200) ; -- 格式：服务器IP;服务器用户名;服务器密码;ssh端口;Shell命令;
	 * 例：192.168.222.22;pospsettle;pospsettle;22;cd
	 * /home/pospsettle/tomcat-7.0-7080/bin&&./restart.sh;
	 */

	public static String[] getRestartComm(String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		String[] command = null;
		try {
			TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(taskId);
			if (null == taskScheduling.getRemoteShell() || "".equals(taskScheduling.getRemoteShell())) {
				return command;
			}else{
				String temp = taskScheduling.getRemoteShell();
				// 清除最后一个;
				if (temp.indexOf(";") > -1 && temp.substring(temp.length() - 1, temp.length()).indexOf(";") > -1) {
					temp = temp.substring(0, temp.length() - 1);
				}
				// 多个名称
				if (temp.indexOf("null") <= -1 && temp.indexOf(";") > -1) {
					command = temp.split(";", -1);
					// 一个名称
				} else if (temp.indexOf("null") <= -1 && temp.indexOf(";") <= -1) {
					command = new String[1];
					command[0] = temp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("获取远程shell地址出现异常，请检查！",e);
			return command;
		}
		return command;

	}

	/**
	 * 获取任务测试时长
	 */
	public static String getTestTime(String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		String desTime = "计算测试时长出错！";
		try {
			TaskExecute taskExecute = GetServerAPI.cgetTaskbyid(taskId);
			Date start = taskExecute.getCreateTime();
            if (null!= taskExecute.getFinishTime()) {
                Date finish = taskExecute.getFinishTime();
                long l = finish.getTime() - start.getTime();
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                desTime = "<font color='#2828FF'>" + hour + "</font>小时<font color='#2828FF'>" + min
                        + "</font>分<font color='#2828FF'>" + s + "</font>秒";
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("获取任务测试时长出现异常，请检查！",e);
			return desTime;
		}
		return desTime;
	}

	/**
	 * 查询web执行，浏览器类型  UI自动化浏览器类型 0 IE 1 火狐 2 谷歌 3 Edge
	 */
	public static int querydrivertype(String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		Integer driverType = 0;
		try {
			TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(taskId);
			driverType = taskScheduling.getBrowserType();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("获取浏览器类型出现异常，请检查！",e);
			return driverType;
		}
		return driverType;
	}

}
