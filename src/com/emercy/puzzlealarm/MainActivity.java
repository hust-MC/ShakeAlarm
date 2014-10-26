package com.emercy.puzzlealarm;

import java.util.Calendar;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity
{
	private Button btn = null;
	private AlarmManager alarmManager = null;
	Calendar cal = Calendar.getInstance();
	
	LinearLayout setAlarm;
	final int DIALOG_TIME = 0;    // 设置对话框id

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setAlarm = (LinearLayout)inflater.inflate(R.layout.alarm_dialog, null);
		
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				new AlertDialog.Builder(MainActivity.this)
						.setView(setAlarm)
						.setTitle("设置闹钟时间")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog,
											int which)
									{

									}
								}).setNegativeButton("取消",null).show();
			}
		});
	}
}