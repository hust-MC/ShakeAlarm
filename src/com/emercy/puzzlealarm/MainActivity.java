package com.emercy.puzzlealarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
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
	Calendar cal = Calendar.getInstance();

	LayoutInflater inflater;
	LinearLayout setAlarm;
	TimePicker timePicker;
	final int DIALOG_TIME = 0;    // 设置对话框id

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		btn = (Button) findViewById(R.id.btn);
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

										Calendar c = Calendar.getInstance();// 获取日期对象
										c.set(Calendar.HOUR_OF_DAY,
												timePicker.getCurrentHour());        // 设置闹钟小时数
										c.set(Calendar.MINUTE,
												timePicker.getCurrentMinute());            // 设置闹钟的分钟数
										c.set(Calendar.SECOND, 0);                // 设置闹钟的秒数
										c.set(Calendar.MILLISECOND, 0);            // 设置闹钟的毫秒数
										Intent intent = new Intent(
												MainActivity.this,
												AlarmReceiver.class);    // 创建Intent对象
										PendingIntent pi = PendingIntent
												.getBroadcast(
														MainActivity.this, 0,
														intent, 0);    // 创建PendingIntent

										Log.d("MC", c.get(Calendar.YEAR) + " ");
										Log.d("MC", c.get(Calendar.MONTH) + " ");
										Log.d("MC", c.get(Calendar.DATE) + " ");
										Log.d("MC",
												timePicker.getCurrentHour()
														+ " "
														+ c.get(Calendar.HOUR_OF_DAY));
										Log.d("MC",
												timePicker.getCurrentMinute()
														+ " "
														+ c.get(Calendar.MINUTE));
										Log.d("MC", c.getTimeInMillis() + "");
										Log.d("MC", System.currentTimeMillis()
												+ "");
										alarmManager.set(
												AlarmManager.RTC_WAKEUP,
												c.getTimeInMillis(), pi);        // 设置闹钟，当前时间就唤醒
										Toast.makeText(MainActivity.this,
												"闹钟设置成功", Toast.LENGTH_LONG)
												.show();// 提示用户

									}
								}).setNegativeButton("取消", null).show();
			}
		});
	}
}