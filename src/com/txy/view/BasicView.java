package com.txy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BasicView extends SurfaceView implements SurfaceHolder.Callback {

	// 该成员变量用于持有对缓冲区访问的能力
	private SurfaceHolder mHolder;
	private RefreshThread mThread = new RefreshThread();

	public BasicView(Context context) {
		super(context);
		// 获取缓冲区的访问
		mHolder = getHolder();

		// 添加一个回调，用于通知参数中传来了类，缓冲区的创建，销毁以及改变
		mHolder.addCallback(this);

	}

	public BasicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取缓冲区的访问
		mHolder = getHolder();

		// 添加一个回调，用于通知参数中传来了类，缓冲区的创建，销毁以及改变
		mHolder.addCallback(this);
	}

	// 线程每次循环，就会执行一次本方法
	protected void runOnce() {
		stepPhysics();

		// 获得画布
		Canvas canvas = mHolder.lockCanvas();
		drawOnce(canvas);

		// 绘制完成后对画布解锁，并刷新到屏幕上
		mHolder.unlockCanvasAndPost(canvas);

	}

	// 计算物理效果
	protected void stepPhysics() {

	}

	// 用于绘图
	public void drawOnce(Canvas canvas) {

	}

	public void stopThread() {
		mThread.stopThread();
	}

	private class RefreshThread implements Runnable {

		private boolean mIsRunning = true;

		@Override
		public void run() {
			// 增加一个循环，让线程能不停刷新屏幕
			while (mIsRunning) {
				runOnce();
			}

		}

		public void stopThread() {
			mIsRunning = false;
		}

	}

	// 缓冲区发生的变化
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	// 缓冲区创建好了
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

		// 实例化一个线程，并且把我们自定义的线程作为参数传进去，作为真正运行的程序片段
		Thread thread = new Thread(mThread);

		// 启动线程
		thread.start();

	}

	// 缓冲区销毁掉了
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

		stopThread();
	}

}
