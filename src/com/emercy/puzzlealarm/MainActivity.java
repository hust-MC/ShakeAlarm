package com.emercy.puzzlealarm;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private Button btn = null;
	private AlarmManager alarmManager = null;
	Calendar c = Calendar.getInstance();

	private LayoutInflater inflater;
	private LinearLayout setAlarm;
	private TimePicker timePicker;
	private PendingIntent pi;
	private Intent intent;
	final int DIALOG_TIME = 0;    // 设置对话框id
	static MainActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		instance = this;

		String timeOnBtn = "";
		final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		timeOnBtn = sdf.format(new Date(c.getTimeInMillis()));

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		btn = (Button) findViewById(R.id.btn_setClock);
		btn.setText(timeOnBtn);
		btn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				setAlarm = (LinearLayout) inflater.inflate(
						R.layout.alarm_dialog, null);
				timePicker = (TimePicker) setAlarm
						.findViewById(R.id.timepicker);
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
										alarmManager.cancel(pi);
										c.set(Calendar.HOUR_OF_DAY,
												timePicker.getCurrentHour());        // 设置闹钟小时数
										c.set(Calendar.MINUTE,
												timePicker.getCurrentMinute());            // 设置闹钟的分钟数
//										c.set(Calendar.SECOND, 0);                // 设置闹钟的秒数
//										c.set(Calendar.MILLISECOND, 0);            // 设置闹钟的毫秒数

										btn.setText(sdf.format(new Date(c
												.getTimeInMillis())));
										intent = new Intent(MainActivity.this,
												AlarmReceiver.class);    // 创建Intent对象
										pi = PendingIntent
												.getBroadcast(
														MainActivity.this, 0,
														intent, 0);    // 创建PendingIntent

										alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,    // 设置闹钟，当前时间就唤醒
												c.getTimeInMillis()-System.currentTimeMillis(),
												24*60*60*1000, pi);
										
										Toast.makeText(MainActivity.this,
												"闹钟设置成功", Toast.LENGTH_LONG)
												.show();// 提示用户
									}
								}).setNegativeButton("取消", null).show();
			}
		});
	}
}