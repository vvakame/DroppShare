package net.vvakame.droppshare;

import net.vvakame.droppshare.AppData;
import net.vvakame.droppshare.IDroppServiceCallback;

interface IDroppDataService {
	List<AppData> getAppDataList();
	void registerCallback(IDroppServiceCallback callback);
	void unregisterCallback(IDroppServiceCallback callback);
}
