inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
@Override
public void onClick(View v)
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
								c.set(Calendar.SECOND, 0); // 设置闹钟的秒数
								c.set(Calendar.MILLISECOND, 0); // 设置闹钟的毫秒数

								btn.setText(sdf.format(new Date(c
										.getTimeInMillis())));
								intent = new Intent(MainActivity.this,
										AlarmReceiver.class);    // 创建Intent对象
								pi = PendingIntent
										.getBroadcast(
												MainActivity.this, 0,
												intent, 0);    // 创建PendingIntent

								alarmManager.setRepeating(
										AlarmManager.RTC,    // 设置闹钟，当前时间就唤醒
										c.getTimeInMillis(),
										24 * 60 * 60 * 1000, pi);

								Toast.makeText(MainActivity.this,
										"闹钟设置成功", Toast.LENGTH_LONG)
										.show();// 提示用户
							}
						}).setNegativeButton("取消", null).show();
	
	
}