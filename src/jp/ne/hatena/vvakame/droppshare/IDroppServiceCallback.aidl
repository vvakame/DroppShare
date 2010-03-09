package jp.ne.hatena.vvakame.droppshare;

import jp.ne.hatena.vvakame.droppshare.AppData;

oneway interface IDroppServiceCallback {
	void pushAppDataList(in List<AppData> appDataList);
}