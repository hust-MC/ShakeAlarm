package com.emercy.puzzlealarm;

import java.io.IOException;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Service;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class ShakePhone extends Activity
{
	// 感应管理器
	private SensorManager mSensorManager;

	// 震动器
	private Vibrator vibrator;

	SensorEventListener sensorListener;
	private TextView textView;
	MediaPlayer mMediaPlayer;
	private int alertValue = 0;

	private void getWidget()
	{
		textView = (TextView) findViewById(R.id.shake_sence_value);
	}

	private void playAlarm()
	{
		mMediaPlayer = new MediaPlayer();

		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

		try
		{
			mMediaPlayer.setDataSource(this, alert);
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (IllegalStateException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		mMediaPlayer.setLooping(true);    // 循环播放开
		try
		{
			mMediaPlayer.prepare();     // 后面的是try 和catch ，自动添加的
		} catch (IllegalStateException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		mMediaPlayer.start();// 开始播放
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		setContentView(R.layout.shake_phone);
		getWidget();

		playAlarm();

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// 1获得硬件信息

		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

		// 2 判断当前手机是否带加速度感应器，如果不带，直接结束，不启动服务
		List<Sensor> sensors = mSensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors != null)
			if (sensors.size() == 0)
				return;

		// 3生成感应侦听事件
		sensorListener = new SensorEventListener()
		{
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy)
			{
			}

			// 感应器发生改变
			@Override
			public void onSensorChanged(SensorEvent event)
			{
				int sensorType = event.sensor.getType();

				// 读取摇一摇敏感值
				int shakeSenseValue = Integer.parseInt(getResources()
						.getString(R.string.shakeSenseValue));
				// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
				float[] values = event.values;

				if (sensorType == Sensor.TYPE_ACCELEROMETER)
				{
					int value = (int) Math.max(Math.abs(values[0]),					// 计算与敏感值的差值
							Math.max(Math.abs(values[1]), Math.abs(values[2])))
							- shakeSenseValue;
					Log.d("MC", Math.abs(values[0]) + "");
					Log.d("MC", Math.abs(values[1]) + "");
					Log.d("MC", Math.abs(values[2]) + "");
					Log.d("MC", value + "");
					if (value > 0)
					{
						alertValue += value;
						if (alertValue >= 100)
						{
							mMediaPlayer.stop();
							mSensorManager.unregisterListener(sensorListener);
							textView.setText("清醒值:\n100%\n成功起床☺!!!");
						}
						else
						{
							CharSequence senceValue = Html
									.fromHtml("<big><b>清醒值:\n" + alertValue
											+ "%</b></big>");
							textView.setText(senceValue);
							vibrator.vibrate(1000);
						}
					}
				}
			}
		};
		// 4注册侦听事件
		mSensorManager.registerListener(sensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
