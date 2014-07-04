package com.zy.utils;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

/**
 * 简单线程池<br>
 * 所有工作线程共享该类成员变量<br>
 * 初始化需要在{@link #init()} 中进行<br>
 * {@link #excute()}是所有工作线程的执行函数
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2014年3月7日
 */
public abstract class SimpleThreadPool extends Thread {
	private static final Logger logger = Logger.getLogger(SimpleThreadPool.class);
	private int poolSize;
	private CountDownLatch countDownLatch;

	/**
	 * 
	 * @param poolSize
	 *            线程池大小
	 */
	public SimpleThreadPool(int poolSize) {
		this.poolSize = poolSize;
	}

	/**
	 * 线程池执行体
	 */
	public abstract void excute();

	/**
	 * 等待线程池所有的线程执行完成
	 * 
	 * @throws InterruptedException
	 */
	public void await() throws InterruptedException {
		countDownLatch.await();
	}

	/**
	 * 线程池执行前动作，比如数据库初始化等
	 */
	public abstract void init();

	/**
	 * 执行线程
	 */
	public final void run() {
		init();
		countDownLatch = new CountDownLatch(poolSize);
		for (int i = 0; i < poolSize; i++) {
			Runnable r = new Worker();
			new Thread(r, "ThreadPool-Worker-" + i).start();
		}
	}

	/**
	 * 工作线程
	 * 
	 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
	 * @date 2014年3月7日
	 */
	private class Worker implements Runnable {

		public void run() {
			try {
				excute();
			} catch (Exception e) {
				logger.error("", e);
			}
			countDownLatch.countDown();
		}
	}
}
