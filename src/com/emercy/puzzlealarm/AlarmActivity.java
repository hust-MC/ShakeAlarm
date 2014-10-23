package com.emercy.puzzlealarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class AlarmActivity extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 显示对话框
		new AlertDialog.Builder(AlarmActivity.this).setTitle("闹钟").// 设置标题
				setMessage("时间到了！").// 设置内容
				setPositiveButton("知道了", new OnClickListener()
				{// 设置按钮
							public void onClick(DialogInterface dialog,
									int which)
							{
								AlarmActivity.this.finish();// 关闭Activity
							}
						}).create().show();

	}

}