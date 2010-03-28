package net.vvakame.droppshare.view;

import net.vvakame.droppshare.R;
import android.app.ProgressDialog;
import android.content.Context;

public class FunnyProgressDialog extends ProgressDialog {
	public FunnyProgressDialog(Context context) {
		super(context, R.style.Theme_FunnyDialog);
	}
}
