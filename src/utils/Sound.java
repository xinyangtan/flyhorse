package utils;

import com.txy.flyhorse.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class Sound {

	private static MediaPlayer mPlayer;
	private static SoundPool mSoundPool;
	private static int[] mSoundId = new int[10];
	public static final int MOVE = 1;
	public static final int VICTORY = 2;
	public static final int KILL = 3;
	public static final int DOUBLEKILL = 4;
	public static final int TRIPLEKILL = 5;
	public static final int QUATREKILL = 6;

	public Sound(Context context) {
		// mPlayer = MediaPlayer.create(context, R.raw.move);
		// mPlayer.setLooping(true);
		// mPlayer.start();
		// 创建SoundPool
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
		mSoundId[0] = mSoundPool.load(context, R.raw.move, 1);
		mSoundId[1] = mSoundPool.load(context, R.raw.victory, 1);
		mSoundId[2] = mSoundPool.load(context, R.raw.kill, 1);
		mSoundId[3] = mSoundPool.load(context, R.raw.double_kill, 1);
		mSoundId[4] = mSoundPool.load(context, R.raw.triple_kill, 1);
		mSoundId[5] = mSoundPool.load(context, R.raw.quatre_kill, 1);
	}

	// 播放声音
	public void playSound(int what) {
		if (what >= 1 && what <= 6) {
			mSoundPool.play(mSoundId[what - 1], 1, 1, 0, 0, 1);
		}
	}
}