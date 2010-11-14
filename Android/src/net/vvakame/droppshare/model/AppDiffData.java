package net.vvakame.droppshare.model;

/**
 * あるアプリのデータ差分を保持するためのクラス
 * 
 * @author vvakame
 */
public class AppDiffData {
	private AppData srcAppData = null;
	private AppData destAppData = null;

	public AppDiffData(AppData src, AppData dest) {

		if (src == null && dest == null) {
			throw new IllegalArgumentException("src and dest object are null.");
		} else if (src == null) {
			// OK
		} else if (dest == null) {
			// OK
		} else if (!src.getPackageName().equals(dest.getPackageName())) {
			throw new IllegalArgumentException(
					"src and dest object that are not same packages.");
		}

		srcAppData = src;
		destAppData = dest;
	}

	public AppData getSrcAppData() {
		return srcAppData;
	}

	public AppData getDestAppData() {
		return destAppData;
	}

	public boolean hasSrcAppData() {
		return srcAppData != null;
	}

	public boolean hasDestAppData() {
		return destAppData != null;
	}

	public AppData getMasterAppData() {

		if (srcAppData == null) {
			return destAppData;
		} else if (destAppData == null) {
			return srcAppData;
		} else if (srcAppData.getPackageName().compareToIgnoreCase(
				destAppData.getPackageName()) < 0) {
			return destAppData;
		} else {
			return srcAppData;
		}
	}
}
