package springboot;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * @author seagull
 * @date 2018��7��27�� ����10:16:40
 */
@SpringBootApplication
public class RunService {

	private static final Logger log = LoggerFactory.getLogger(RunService.class);
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "bootlog4j.conf");//Ĭ��·��user.dir
		SpringApplication.run(RunService.class, args);
        try {
        	String host = InetAddress.getLocalHost().getHostAddress();
        	log.info("�����ͻ��˼���,���Ժ�......����IP:{}",host);
        } catch (UnknownHostException e) {
        	log.error("��ȡ����IP�����쳣......", e);
        }
		HttpImpl.checkHostNet();
	}

}
