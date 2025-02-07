package luckyclient.serverapi.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 测试用例步骤实体
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年4月13日
 */
public class ProjectCaseSteps extends BaseEntity
{
	private static final long serialVersionUID = 1L;
	
	/** 步骤ID */
	private Integer stepId;
	/** 用例ID */
	private Integer caseId;
	/** 项目ID */
	private Integer projectId;
	/** 步骤序号 */
	private Integer stepSerialNumber;
	/** 包路径|定位路径 */
	private String stepPath;
	/** 方法名|操作 */
	private String stepOperation;
	/** 参数 */
	private String stepParameters;
	/** 步骤动作 */
	private String action;
	/** 预期结果 */
	private String expectedResult;
	/** 0 API接口 1 Web UI 2 HTTP接口 3移动端 */
	private Integer stepType;
	/** 扩展字段，可用于备注、存储HTTP模板等 */
	private String extend;

	public void setStepId(Integer stepId) 
	{
		this.stepId = stepId;
	}

	public Integer getStepId() 
	{
		return stepId;
	}
	public void setCaseId(Integer caseId) 
	{
		this.caseId = caseId;
	}

	public Integer getCaseId() 
	{
		return caseId;
	}
	public void setProjectId(Integer projectId) 
	{
		this.projectId = projectId;
	}

	public Integer getProjectId() 
	{
		return projectId;
	}
	public void setStepSerialNumber(Integer stepSerialNumber) 
	{
		this.stepSerialNumber = stepSerialNumber;
	}

	public Integer getStepSerialNumber() 
	{
		return stepSerialNumber;
	}
	public void setStepPath(String stepPath) 
	{
		this.stepPath = stepPath;
	}

	public String getStepPath() 
	{
		return stepPath;
	}
	public void setStepOperation(String stepOperation) 
	{
		this.stepOperation = stepOperation;
	}

	public String getStepOperation() 
	{
		return stepOperation;
	}
	public void setStepParameters(String stepParameters) 
	{
		this.stepParameters = stepParameters;
	}

	public String getStepParameters() 
	{
		return stepParameters;
	}
	public void setAction(String action) 
	{
		this.action = action;
	}

	public String getAction() 
	{
		return action;
	}
	public void setExpectedResult(String expectedResult) 
	{
		this.expectedResult = expectedResult;
	}

	public String getExpectedResult() 
	{
		return expectedResult;
	}
	public void setStepType(Integer stepType) 
	{
		this.stepType = stepType;
	}

	public Integer getStepType() 
	{
		return stepType;
	}
	public void setExtend(String extend) 
	{
		this.extend = extend;
	}

	public String getExtend() 
	{
		return extend;
	}

    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("stepId", getStepId())
            .append("caseId", getCaseId())
            .append("projectId", getProjectId())
            .append("stepSerialNumber", getStepSerialNumber())
            .append("stepPath", getStepPath())
            .append("stepOperation", getStepOperation())
            .append("stepParameters", getStepParameters())
            .append("action", getAction())
            .append("expectedResult", getExpectedResult())
            .append("stepType", getStepType())
            .append("extend", getExtend())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
