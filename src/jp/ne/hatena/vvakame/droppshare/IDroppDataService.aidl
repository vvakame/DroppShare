package jp.ne.hatena.vvakame.droppshare;

import jp.ne.hatena.vvakame.droppshare.AppData;
import jp.ne.hatena.vvakame.droppshare.IDroppServiceCallback;

interface IDroppDataService {
	List<AppData> getAppDataList();
	void registerCallback(IDroppServiceCallback callback);
	void unregisterCallback(IDroppServiceCallback callback);
}
