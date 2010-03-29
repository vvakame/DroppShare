package net.vvakame.droppshare.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InstallLogModel {

	private static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";

	private Long rowId = null;
	private String packageName;
	private String versionName;
	private String actionType;
	private Date processDate;

	protected void setProcessDate(String processDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		try {
			this.processDate = sdf.parse(processDate);
		} catch (ParseException e) {
		}
	}

	protected String getProcessDateString() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		return sdf.format(processDate);
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	public static String getDatePattern() {
		return DATE_PATTERN;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append(InstallLogModel.class.getSimpleName()).append("=");
		stb.append("{");
		stb.append("rowId=").append(rowId).append(", ");
		stb.append("packageName=").append(packageName).append(", ");
		stb.append("versionName=").append(versionName).append(", ");
		stb.append("actionType=").append(actionType).append(", ");
		stb.append("processDate=").append(processDate).append("");
		stb.append("}");

		return stb.toString();
	}
}
