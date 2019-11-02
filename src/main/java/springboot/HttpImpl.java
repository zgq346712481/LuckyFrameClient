package springboot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import luckyclient.caserun.exappium.AppTestControl;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebTestControl;
import luckyclient.publicclass.LogUtil;
import luckyclient.publicclass.SysConfig;
import luckyclient.publicclass.remoterinterface.HttpRequest;
import luckyclient.serverapi.api.GetServerApi;
import luckyclient.serverapi.entity.TaskExecute;
import luckyclient.serverapi.entity.TaskScheduling;
import luckyclient.serverapi.entity.monitor.Server;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springboot.model.RunBatchCaseEntity;
import springboot.model.RunTaskEntity;
import springboot.model.WebDebugCaseEntity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Properties;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * @author seagull
 * @date 2018��7��27�� ����10:28:32
 */
@RestController
public class HttpImpl {
	private static final Logger log = LoggerFactory.getLogger(HttpImpl.class);
	private static final String OS=System.getProperty("os.name").toLowerCase();
	/**
	 * �����Զ�������
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/runTask")
	private String runTask(HttpServletRequest req) throws RemoteException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = req.getReader();) {
			char[] buff = new char[1024];
			int len;
			while ((len = reader.read(buff)) != -1) {
				sb.append(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("��ʼת��RunTaskEntityִ������ʵ��...");
		RunTaskEntity runTaskEntity = JSONObject.parseObject(sb.toString(), RunTaskEntity.class);
		log.info("TaskId:{},SchedulingName:{},LoadPath:{}",runTaskEntity.getTaskId(),runTaskEntity.getSchedulingName(),runTaskEntity.getLoadPath());
		try{
			log.info("��ʼ��ȡ�ͻ�������·��...");
			File file =new File(System.getProperty("user.dir")+runTaskEntity.getLoadPath()); 
			log.info("�ͻ�������·��:{}",file.getAbsolutePath());
			if  (!file .isDirectory())      
			{       
				log.warn("�ͻ��˲�������׮·�������ڣ����顾{}��",file.getPath());
				return "�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��";
			}
			log.info("��ʼ��Runtime...");
			Runtime run = Runtime.getRuntime();
			StringBuffer sbf=new StringBuffer();
			sbf.append(runTaskEntity.getTaskId()).append(" ");
			String taskid = runTaskEntity.getTaskId();//debug����ģʽ����ȡ����id taskid
			sbf.append(runTaskEntity.getLoadPath());
			log.info("��������ģʽ���Գ���...��������:��{}��  ����ID:��{}��",runTaskEntity.getSchedulingName(),runTaskEntity.getTaskId());
			if(OS.startsWith("win")){
				log.info("��ʼ����windows�����д���...");
//				run.exec("cmd.exe /k start " + "task.cmd" +" "+ sbf.toString(), null,new File(System.getProperty("user.dir")+File.separator));

				//debug�������ģʽ----begin
				try {
					PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.conf");//debug ���������ļ�·����\src\main\Resources\
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
					LogUtil.APP.error("���������������������������쳣�����飡", e);
				}//debug�������ģʽ-----end

				log.info("����windows�����д������...");
			}else{
				log.info("��ʼ����Linux����ű�...");
				Process ps = Runtime.getRuntime().exec(System.getProperty("user.dir")+File.separator+"task.sh"+ " " +sbf.toString());
		        ps.waitFor();
				log.info("����Linux����ű����...");
			}			
		} catch (Exception e) {
			log.error("��������ģʽ���Գ����쳣������",e);
			return "��������ģʽ���Գ����쳣������";
		}
		return "��������ģʽ���Գ�������";
	}

	/**
	 * ������������
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/runBatchCase")
	private String runBatchCase(HttpServletRequest req) throws RemoteException {
		StringBuilder sbd = new StringBuilder();
		try (BufferedReader reader = req.getReader();) {
			char[] buff = new char[1024];
			int len;
			while ((len = reader.read(buff)) != -1) {
				sbd.append(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("��ʼת��RunBatchCaseEntity����ִ������ʵ��...");
		RunBatchCaseEntity runBatchCaseEntity = JSONObject.parseObject(sbd.toString(), RunBatchCaseEntity.class);
		
		String projectName = runBatchCaseEntity.getProjectname();
		String taskId = runBatchCaseEntity.getTaskid();
		String loadPath = runBatchCaseEntity.getLoadpath();
		String batchCase = runBatchCaseEntity.getBatchcase();
		log.info("������������:{}",batchCase);
		try{
			log.info("��ʼ��ȡ�ͻ�������·��...");
			File file =new File(System.getProperty("user.dir")+loadPath);
			log.info("�ͻ�������·��:{}",file.getAbsolutePath());
			if  (!file .isDirectory())      
			{    
				log.warn("�ͻ��˲�������׮·�������ڣ����顾{}��",file.getPath());
				return "�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��";
			}
			log.info("��ʼ��Runtime...");
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(taskId).append(" ");
			sb.append(batchCase).append(" ");
			sb.append(loadPath);
			log.info("������������ģʽ���Գ���...������Ŀ:{}  ����ID:{}",projectName,taskId);
			if(OS.startsWith("win")){
				log.info("��ʼ����windows�����д���...");
				run.exec("cmd.exe /k start " + "task_batch.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+File.separator));				
				log.info("����windows�����д������...");
			}else{
				log.info("��ʼ����Linux����ű�...");
				Process ps = Runtime.getRuntime().exec(System.getProperty("user.dir")+File.separator+"task_batch.sh"+ " " +sb.toString());
		        ps.waitFor();
		        log.info("����Linux����ű����...");
			}		
		} catch (Exception e) {		
			e.printStackTrace();
			log.error("������������ģʽ���Գ����쳣������",e);
			return "������������ģʽ���Գ����쳣������";
		} 
		return "������������ģʽ���Գ�������";
	}
	
	/**
	 * web������Ƚӿ�
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/webDebugCase")
	private String webDebugCase(HttpServletRequest req) throws RemoteException {
		StringBuilder sbd = new StringBuilder();
		try (BufferedReader reader = req.getReader();) {
			char[] buff = new char[1024];
			int len;
			while ((len = reader.read(buff)) != -1) {
				sbd.append(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		WebDebugCaseEntity webDebugCaseEntity = JSONObject.parseObject(sbd.toString(), WebDebugCaseEntity.class);
		log.info("Web�˵�������ID:{} ������ID:{}",webDebugCaseEntity.getCaseId(),webDebugCaseEntity.getUserId());
		try{
			File file =new File(System.getProperty("user.dir")+webDebugCaseEntity.getLoadpath()); 	   
			if  (!file .isDirectory())      
			{    
				log.warn("�ͻ��˲�������׮·�������ڣ����顾{}��",file.getPath());
				return "�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(webDebugCaseEntity.getCaseId()).append(" ");
			sb.append(webDebugCaseEntity.getUserId()).append(" ");
			sb.append(webDebugCaseEntity.getLoadpath());
			if(OS.startsWith("win")){
				run.exec("cmd.exe /k start " + "web_debugcase.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+File.separator));			
			}else{
				Process ps = Runtime.getRuntime().exec(System.getProperty("user.dir")+File.separator+"web_debugcase.sh"+ " " +sb.toString());
	            ps.waitFor();  
			}	
		} catch (Exception e) {		
			e.printStackTrace();
			log.error("����Web����ģʽ���Գ����쳣������",e);
			return "����Web����ģʽ���Գ����쳣������";
		} 
		return "����Web����ģʽ���Գ�������";
	}
	
	/**
	 * ��ȡ�ͻ��˱�����־
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@GetMapping("/getLogdDetail")
	private String getLogdDetail(HttpServletRequest req) throws RemoteException{
		String fileName=req.getParameter("filename");
		String ctxPath = System.getProperty("user.dir")+File.separator+"log";
		String downLoadPath = ctxPath +File.separator+ fileName;

		String str = "";
		InputStreamReader isr=null;
		try {
			isr = new InputStreamReader(new FileInputStream(downLoadPath), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("��ȡ��־·����������ͻ�����־·���Ƿ����!downLoadPath: "+downLoadPath,e);
			return "��ȡ��־·����������ͻ�����־·���Ƿ����!downLoadPath: "+downLoadPath;
		}
		BufferedReader bos = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		try {
			while ((str = bos.readLine()) != null)
			{
				sb.append(str).append("##n##");
			}
			bos.close();
			log.info("����˶�ȡ������־�ɹ�!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("�ͻ���תBufferedReaderʧ�ܣ�����ԭ��",e);
			return "�ͻ���תBufferedReaderʧ�ܣ�����ԭ��";
		}
		return sb.toString();
	}
	
	/**
	 * ��ȡ�����ͼ
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@GetMapping("/getLogImg")
	private byte[] getLogImg(HttpServletRequest req,HttpServletResponse res) throws RemoteException{
		String imgName=req.getParameter("imgName");
		String ctxPath = System.getProperty("user.dir")+File.separator+"log"+File.separator+"ScreenShot";
		String downLoadPath = ctxPath+File.separator+imgName;
        byte[] b = null;
        try {
            File file = new File(downLoadPath);
            b = new byte[(int) file.length()];
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
            is.read(b);
            is.close();
        	log.info("����˻�ȡ����ͼƬ:{}",downLoadPath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            log.error("���ļ������ڣ�����:{}",downLoadPath,e);
            return b;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return b;
        }     
        return b;
	}
	
	@PostMapping("/uploadJar")
	private String uploadJar(HttpServletRequest req,HttpServletResponse res, HttpSession session,@RequestParam("jarfile") MultipartFile jarfile) throws IOException, ServletException{
		if (!jarfile.isEmpty()){
            if (!FilenameUtils.getExtension(jarfile.getOriginalFilename())
                    .equalsIgnoreCase("jar")) {
            	log.warn("�ļ���ʽ��������.jar���ϴ�ʧ��");
                return "�ļ���ʽ��������.jar���ϴ�ʧ��";
            }
		}else{
			log.warn("�ϴ��ļ�Ϊ�գ����飡");
            return "�ϴ��ļ�Ϊ�գ����飡";
		}

		String name = jarfile.getOriginalFilename();
		String loadpath = req.getParameter("loadpath");
		String path = System.getProperty("user.dir")+loadpath;
		if  (!new File(path) .isDirectory())      
		{    
			log.warn("�ͻ��˲�������׮·�������ڣ����顾{}��",path);
			return "�ͻ��˲�������׮·�������ڣ����顾"+path+"��";
		}	
		String pathName = path +File.separator+ name;

		File file = new File(pathName);
        try { 
            if (file.exists()){
            	file.deleteOnExit();
            }
            file.createNewFile();
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            byte[] jarfileByte = jarfile.getBytes();
            os.write(jarfileByte);
            os.flush();
            os.close();
            log.info("�ϴ�JAR����{}�����ͻ�������Ŀ¼��{}���ɹ�!",name,file.getAbsolutePath());
            return "�ϴ�JAR����"+name+"�����ͻ�������Ŀ¼��"+file.getAbsolutePath()+"���ɹ�!";
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            log.error("�ͻ���δ�ҵ���ȷ·�����ļ����ϴ�ʧ�ܣ��ļ�·������:{}",pathName,e);
            return "�ͻ���δ�ҵ���ȷ·�����ļ����ϴ�ʧ�ܣ��ļ�·�����ƣ�"+pathName;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("�ͻ���IOExceptiona����δ�ҵ�����·�����ļ�·������:{}",pathName,e);
            return "�ͻ���IOExceptiona����δ�ҵ�����·�����ļ�·�����ƣ�"+pathName;
        }
	}
	
	/**
	 * ���ͻ�������
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@GetMapping("/getClientStatus")
	private String getClientStatus(HttpServletRequest req) throws RemoteException{
		Properties properties = SysConfig.getConfiguration();
		String verison=properties.getProperty("client.verison");
		return "{\"status\":\"success\",\"version\":\""+verison+"\"}";
	}

	/**
	 * ��ȡ�ͻ�����Դ������
	 * @param req
	 * @return
	 * @author Seagull
	 * @throws Exception 
	 * @date 2019��5��5��
	 */
	@GetMapping("/getClientMonitorData")
	private String getClientMonitorData(HttpServletRequest req) throws Exception{
        Server server = new Server();
        server.copyTo();
        return JSON.toJSONString(server);
	}
	
	/**
	 * ���ͻ����е�����
	 * @return
	 * @author Seagull
	 * @date 2019��5��6��
	 */
	public static boolean checkHostNet() {
		log.info("���ͻ���������,���Ժ�......");
		Properties properties = SysConfig.getConfiguration();
		String version=properties.getProperty("client.verison");
		String webip=properties.getProperty("server.web.ip");
		Integer webport=Integer.valueOf(properties.getProperty("server.web.port"));
        try {
        	String result = HttpRequest.loadJSON("/openGetApi/clientGetServerVersion.do");
        	if(version.equals(result)){
            	log.info("�ͻ��˷���Web������: {}:{} ���ͨ��......",webip,webport);
        	}else{
        		log.warn("�ͻ��˰汾:{} ����˰汾:{} �ͻ��������˰汾��һ�£��п��ܻᵼ��δ֪���⣬����...",version,result);
        	}

        } catch (Exception e) {
        	log.error("�ͻ������ü���쳣����ȷ������Ŀ��Ŀ¼�µĿͻ��������ļ�(sys_config.properties)�Ƿ��Ѿ���ȷ���á�",e);
            return false;
        }
        return true;
    }

}
