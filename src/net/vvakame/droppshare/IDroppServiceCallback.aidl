package net.vvakame.droppshare;

import net.vvakame.droppshare.AppData;

oneway interface IDroppServiceCallback {
	void pushAppDataList(in List<AppData> appDataList);
}