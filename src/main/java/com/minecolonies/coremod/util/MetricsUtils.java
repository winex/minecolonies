package com.minecolonies.coremod.util;

public class MetricsUtils
{
	public static long getExecutionMilliTime(Runnable task)
	{
		long start = System.currentTimeMillis();
		task.run();
		return System.currentTimeMillis() - start;
	}
	
	public static long getExecutionNanoTime(Runnable task)
	{
		long start = System.nanoTime();
		task.run();
		return System.nanoTime() - start;
	}
}