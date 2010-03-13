package net.vvakame.droppshare;

import android.app.ProgressDialog;
import android.content.Context;

public class FunnyProgressDialog extends ProgressDialog {
	public FunnyProgressDialog(Context context) {
		super(context, R.style.Theme_FunnyDialog);
	}
}
