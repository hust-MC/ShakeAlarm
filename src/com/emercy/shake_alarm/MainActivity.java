package com.emercy.shake_alarm;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.emercy.shake_alarm.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */

	private Button btn;									// 申明设置时钟按钮
	private ToggleButton btn_enClk;						// 申明开启\关闭按钮
	private ToggleButton togbtn_AlarmStyle;

	private SharedPreferences sharedData;
	private static boolean alarmStyle = true;			// 闹钟提示方式 (true:铃声;false:振动)

	Calendar c = Calendar.getInstance();

	final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	static MainActivity instance;

	public static void setAlarmStyle(boolean style)
	{
		alarmStyle = style;
	}

	public static boolean getAlarmStyle()
	{
		return alarmStyle;
	}

	private void loadData()
	{
		sharedData = getSharedPreferences("main_activity", MODE_PRIVATE);

		btn.setText(sharedData.getString("time",sdf.format(new Date(c.getTimeInMillis()))));
		btn_enClk.setChecked(sharedData.getBoolean("on_off", false));
	}

	private void saveData()
	{
		SharedPreferences.Editor edit = sharedData.edit();
		edit.putString("time", btn.getText().toString());
		edit.putBoolean("on_off", btn_enClk.isChecked());
		edit.putBoolean("style", togbtn_AlarmStyle.isChecked());
		edit.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		instance = this;								// 用于在ShakeAlarm窗口中关闭此activity

		String timeOnBtn = "";

		timeOnBtn = sdf.format(new Date(c.getTimeInMillis()));

		ButtonListener buttonListener = new ButtonListener();	// 注册设置时间按钮监听事件
		btn = (Button) findViewById(R.id.btn_setClock);
		btn.setText(timeOnBtn);
		btn.setOnClickListener(buttonListener);

		btn_enClk = (ToggleButton) findViewById(R.id.btn_enClk); // 注册开启关闭按钮监听事件
		btn_enClk.setOnClickListener(buttonListener);

		loadData();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		saveData();
	}

	class ButtonListener implements OnClickListener
	{
		private TimePicker timePicker;

		private PendingIntent pi;
		private Intent intent;
		AlarmManager alarmManager;
		LayoutInflater inflater;
		LinearLayout setAlarmLayout;

		public ButtonListener()
		{
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			setAlarmLayout = (LinearLayout) inflater.inflate(
					R.layout.alarm_dialog, null);
		}

		private void enableClk()
		{
			timePicker = (TimePicker) setAlarmLayout
					.findViewById(R.id.timepicker);
			c.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());        // 设置闹钟小时数
			c.set(Calendar.MINUTE, timePicker.getCurrentMinute());            // 设置闹钟的分钟数
			c.set(Calendar.SECOND, 0); // 设置闹钟的秒数
			c.set(Calendar.MILLISECOND, 0); // 设置闹钟的毫秒数

			if (c.getTimeInMillis() - System.currentTimeMillis() < 0)
			{
				c.roll(Calendar.DATE, 1);
			}

			btn.setText(sdf.format(new Date(c.getTimeInMillis())));
			intent = new Intent(MainActivity.this, AlarmReceiver.class);    // 创建Intent对象
			pi = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);    // 创建PendingIntent

			alarmManager.setRepeating(AlarmManager.RTC,    // 设置闹钟，当前时间就唤醒
					c.getTimeInMillis(), 24 * 60 * 60 * 1000, pi);
		}

		private void disableClk()
		{
			alarmManager.cancel(pi);
		}

		@Override
		public void onClick(View v)
		{

			switch (v.getId())
			{
			case R.id.btn_setClock:

				setAlarmLayout = (LinearLayout) inflater.inflate(
						R.layout.alarm_dialog, null);

				togbtn_AlarmStyle = (ToggleButton) setAlarmLayout
						.findViewById(R.id.togbtn_alarm_style);
				togbtn_AlarmStyle.setChecked(sharedData.getBoolean("style",
						false));
				timePicker = (TimePicker) setAlarmLayout
						.findViewById(R.id.timepicker);
				timePicker.setIs24HourView(true);

				new AlertDialog.Builder(MainActivity.this)
						.setView(setAlarmLayout)
						.setTitle("设置闹钟时间")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog,
											int which)
									{
										disableClk();
										enableClk();
										if (togbtn_AlarmStyle.isChecked())
										{
											MainActivity.setAlarmStyle(true);
										}
										else
										{
											MainActivity.setAlarmStyle(false);
										}
										btn_enClk.setChecked(true);
										Toast.makeText(MainActivity.this,
												"闹钟设置成功", Toast.LENGTH_LONG)
												.show();// 提示用户
									}
								}).setNegativeButton("取消", null).show();
				break;

			case R.id.btn_enClk:
				if (btn_enClk.isChecked())
				{
					enableClk();
				}
				else
				{
					disableClk();
				}
				break;
			}
		}
	}
}
