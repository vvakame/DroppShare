package net.vvakame.droppshare.model;

public class AppRecommendModel {

	private Long rowId = null;
	private String packageName;
	private int versionCode = -1;
	private String versionName;
	private String goodThing;
	private String improvement;

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

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getGoodThing() {
		return goodThing;
	}

	public void setGoodThing(String goodThing) {
		this.goodThing = goodThing;
	}

	public String getImprovement() {
		return improvement;
	}

	public void setImprovement(String improvement) {
		this.improvement = improvement;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append(AppRecommendModel.class.getSimpleName()).append("=");
		stb.append("{");
		stb.append("rowId=").append(rowId).append(", ");
		stb.append("packageName=").append(packageName).append(", ");
		stb.append("versionCode=").append(versionCode).append(", ");
		stb.append("versionName=").append(versionName).append(", ");
		stb.append("goodThing=").append(goodThing).append(", ");
		stb.append("improvement=").append(improvement).append("");
		stb.append("}");

		return stb.toString();
	}
}
