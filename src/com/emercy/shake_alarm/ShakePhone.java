package com.emercy.shake_alarm;

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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.os.Vibrator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class ShakePhone extends Activity
{
	// 感应管理器
	private SensorManager mSensorManager;

	// 震动器
	private Vibrator vibrator;

	private SensorEventListener sensorListener;		// 申明传感器监听事件
	private TextView textView;
	private MediaPlayer mMediaPlayer;
	private Exit exit = new Exit();
	private int alertValue = 0;
	private boolean wakeUp = false;						// 标志是否醒来

	private Chronometer chronometer;

	private void getWidget()
	{
		textView = (TextView) findViewById(R.id.shake_sence_value);
		chronometer = (Chronometer) findViewById(R.id.cm_count);
		chronometer.setFormat("本次起床用了:%s秒");
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

	private void startVibrate()
	{
		long[] vib =
		{ 0, 200, 3000, 500, 2000, 1000 };
		vibrator.vibrate(vib, 4);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setContentView(R.layout.shake_phone);
		getWidget();

		chronometer.setBase(SystemClock.elapsedRealtime());
		chronometer.start();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// 1获得硬件信息

		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

		if (MainActivity.getAlarmStyle())
		{
			playAlarm();
		}
		else
		{
			startVibrate();
		}

		if (MainActivity.getAlarmStyle())
		{
			playAlarm();
		}
		else
		{
			startVibrate();
		}

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
				int shakeSenseValue = Integer.parseInt(MainActivity.shakeSenseValue);
				// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
				float[] values = event.values;

				if (sensorType == Sensor.TYPE_ACCELEROMETER)
				{
					int value = (int) Math.max(Math.abs(values[0]),					// 计算与敏感值的差值
							Math.max(Math.abs(values[1]), Math.abs(values[2])))
							- shakeSenseValue;
					if (value > 0)
					{
						alertValue += value;
						if (alertValue >= 100)
						{
							if (MainActivity.getAlarmStyle())
							{
								mMediaPlayer.stop();
							}
							else
							{
								vibrator.cancel();
							}
							mSensorManager.unregisterListener(sensorListener);
							textView.setTextColor(android.graphics.Color.MAGENTA);
							textView.setText("清醒值:\n100%\n\n成功起床\n☺");

							chronometer.stop();
							chronometer.setVisibility(View.VISIBLE);

							wakeUp = true;
							vibrator.vibrate(2000);
						}
						else
						{
							CharSequence senceValue = Html
									.fromHtml("<big><b>清醒值:\n" + alertValue
											+ "%</b></big>");
							textView.setText(senceValue);
							// vibrator.vibrate(1000); //摇晃振动
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
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			if (wakeUp)
			{
				pressAgainExit();
			}
			else
			{
				Toast.makeText(this, R.string.unablePressBack,
						Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}

	/*
	 * 按两次退出
	 */
	private void pressAgainExit()
	{
		if (exit.isExit())
		{
			finish();
			MainActivity.instance.finish();
		}
		else
		{
			Toast.makeText(getApplicationContext(), R.string.pressBackAgain,
					Toast.LENGTH_SHORT).show();
			exit.doExitInOneSecond();
		}
	}

	class Exit
	{
		private boolean isExit = false;
		private final Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				isExit = false;
			}
		};

		public void doExitInOneSecond()
		{
			isExit = true;
			HandlerThread thread = new HandlerThread("doTask");
			thread.start();
			new Handler(thread.getLooper()).postDelayed(task, 2000);
		}

		public boolean isExit()
		{
			return isExit;
		}

		public void setExit(boolean isExit)
		{
			this.isExit = isExit;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
