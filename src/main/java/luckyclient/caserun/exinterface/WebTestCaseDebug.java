package luckyclient.caserun.exinterface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerApi;
import luckyclient.serverapi.api.PostServerApi;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 *
 * @ClassName: WebTestCaseDebug
 * @Description: �ṩWeb�˵��Խӿ�
 * @author�� seagull
 * @date 2018��3��1��
 */
public class WebTestCaseDebug {
	private static final String ASSIGNMENT_SIGN = "$=";
	private static final String FUZZY_MATCHING_SIGN = "%=";
	private static final String REGULAR_MATCHING_SIGN = "~=";
    protected static final String JSONPATH_SIGN = "$J=";
    private static PostServerApi PostServerAPI;

    /**
     * @param executor
     * @param sign ������WEBҳ���ϵ�������ʱ�ṩ�Ľӿ�
     */
    public static void oneCaseDebug(String caseIdStr, String userIdStr) {
        Map<String, String> variable = new HashMap<>(0);
        String packagename = null;
        String functionname = null;
        String expectedresults = null;
        Integer setcaseresult = 0;
        Object[] getParameterValues = null;
        String testnote = "��ʼ�����Խ��";
        int k = 0;
        Integer caseId = Integer.valueOf(caseIdStr);
        Integer userId = Integer.valueOf(userIdStr);
        ProjectCase testcase = GetServerApi.cGetCaseByCaseId(caseId);

        String sign = testcase.getCaseSign();
        List<ProjectCaseParams> pcplist = GetServerApi.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
        // �ѹ����������뵽MAP��
        for (ProjectCaseParams pcp : pcplist) {
            variable.put(pcp.getParamsName(), pcp.getParamsValue());
        }
        List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
        //����ѭ���������������в���
        for (int i = 0; i < steps.size(); i++) {
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), "888888", null,variable);
            try {
                packagename = casescript.get("PackageName");
                functionname = casescript.get("FunctionName");
            } catch (Exception e) {
                k = 0;
                LogUtil.APP.error("�����������Ƿ����������쳣��",e);
                PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "�����������Ƿ�����ʧ�ܣ����飡",2);
                break;        //ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
            }
            //�������ƽ��������쳣���ǵ���������������쳣
            if ((null != functionname && functionname.contains("�����쳣")) || k == 1) {
                k = 0;
                testnote = "������" + (i + 1) + "��������������";
                break;
            }
            expectedresults = casescript.get("ExpectedResults");
            //�жϷ����Ƿ������
            if (casescript.size() > 4) {
                //��ȡ������������������
                getParameterValues = new Object[casescript.size() - 4];
                for (int j = 0; j < casescript.size() - 4; j++) {
                    if (casescript.get("FunctionParams" + (j + 1)) == null) {
                        k = 1;
                        break;
                    }

                    String parameterValues = casescript.get("FunctionParams" + (j + 1));
                    PostServerApi.cPostDebugLog(userId, caseId, "INFO", "����������" + packagename + " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues, 0);
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            //���ö�̬������ִ�в�������
            try {
                PostServerApi.cPostDebugLog(userId, caseId, "INFO", "��ʼ���÷�����" + functionname + " .....",0);

                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getStepType(), steps.get(i).getExtend());
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
                if (null != expectedresults && !expectedresults.isEmpty()) {
                    // ��ֵ����
                    if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                        PostServerApi.cPostDebugLog(userId, caseId, "INFO", "�����Խ����" + testnote + "����ֵ��������" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "��",0);
                    }
                    // ģ��ƥ��
                    else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                            PostServerApi.cPostDebugLog(userId, caseId, "INFO", "ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + testnote,0);
                            testnote = "������" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......",testcase.getCaseSign(),(i+1));
                                break;
                            } else {
                                LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......",testcase.getCaseSign(),(i+1));
                            }
                        }
                    }
                    // ����ƥ��
                    else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length() && expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
                        Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
                        Matcher matcher = pattern.matcher(testnote);
                        if (matcher.find()) {
                            PostServerApi.cPostDebugLog(userId, caseId, "INFO", "����ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "��" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + testnote,0);
                            testnote = "������" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......",testcase.getCaseSign(),(i+1));
                                break;
                            } else {
                                LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......",testcase.getCaseSign(),(i+1));
                            }
                        }
                    }
                    //jsonpath����
                    else if (expectedresults.length() > JSONPATH_SIGN.length() && expectedresults.startsWith(JSONPATH_SIGN)) {
                        expectedresults = expectedresults.substring(JSONPATH_SIGN.length());
                        String jsonpath = expectedresults.split("=")[0];
                        String exceptResult = expectedresults.split("=")[1];
                        List<String> exceptResultList = Arrays.asList(exceptResult.split("(?<!&),"));
                        List<String> exceptResults = Lists.newArrayList();
                        // ��������ֵ�ﱾ�����Ӣ�Ķ��ŵ����
                        for (String s : exceptResultList) {
                            s = s.replace("&,",",");
                            exceptResults.add(s);
                        }
                        Configuration conf = Configuration.defaultConfiguration();
                        JSONArray datasArray = JSON.parseArray(JSON.toJSONString(JsonPath.using(conf).parse(testnote).read(jsonpath)));
                        List<String> result = JSONObject.parseArray(datasArray.toJSONString(), String.class);
                        if (exceptResults.equals(result)) {
                            setcaseresult = 0;
                            PostServerApi.cPostDebugLog(userId, caseId, "INFO", "jsonpath����Ԥ�ڽ���ɹ���Ԥ�ڽ����" + expectedresults + " ���Խ��: " + result.toString() + "У����: true", 0);
                        } else {
                            setcaseresult = 1;
                            PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "��" + (i + 1) + "����jsonpath����Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + result.toString(), 0);
                            testnote = "������" + (i + 1) + "����jsonpath����Ԥ�ڽ��ʧ�ܣ�";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......",testcase.getCaseSign(),(i+1));
                                break;
                            } else {
                                LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......",testcase.getCaseSign(),(i+1));
                            }
                        }
                    }
                    // ��ȫ���
                    else {
                        if (expectedresults.equals(testnote)) {
                            PostServerApi.cPostDebugLog(userId, caseId, "INFO", "��ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "��" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + testnote,0);
                            testnote = "������" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......",testcase.getCaseSign(),(i+1));
                                break;
                            } else {
                                LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......",testcase.getCaseSign(),(i+1));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                setcaseresult = 1;
                LogUtil.APP.error("����ִ�г����쳣��",e);
                PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "���÷������̳�����������" + functionname + " �����¼��ű����������Լ�������",0);
                testnote = "CallCase���ó���";
                if (testcase.getFailcontinue() == 0) {
                    LogUtil.APP.error("������{}���ڡ�{}������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......",testcase.getCaseSign(),(i+1));
                    break;
                } else {
                    LogUtil.APP.error("������{}���ڡ�{}������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......",testcase.getCaseSign(),(i+1));
                }
            }
        }
        variable.clear();               //��մ���MAP
        //������÷���������δ�����������ò��Խ������
        if (testnote.contains("CallCase���ó���") && testnote.contains("������������")) {
            PostServerApi.cPostDebugLog(userId, caseId, "ERRORover", "���� " + sign + "�������ǵ��ò����еķ�������",1);
        }
        if (0 == setcaseresult) {
            PostServerApi.cPostDebugLog(userId, caseId, "INFOover", "���� " + sign + "����ȫ��ִ����ɣ�",1);
        } else {
            PostServerApi.cPostDebugLog(userId, caseId, "ERRORover", "���� " + sign + "��ִ�й�����ʧ�ܣ����飡",1);
        }
    }

    public static void main(String[] args) throws Exception {

    }
}
