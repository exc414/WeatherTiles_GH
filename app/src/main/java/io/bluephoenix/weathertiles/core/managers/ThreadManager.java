package io.bluephoenix.weathertiles.core.managers;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Create threads as need it by the application. Set a maximum number
 * of threads based on the number of processors on the device.
 *
 * Read the below link learn the difference between
 * setPriority() and Process.setThreadPriority.
 *
 * @author Carlos A. Perez Zubizarreta
 * @see <a href="http://stackoverflow.com/questions/5198518/">Thread Priority</a>
 */
public class ThreadManager
{
    private static ThreadManager instance = null;
    private final ExecutorService executorService;
    //private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static int NUMBER_OF_CORES = 2;

    static { instance = new ThreadManager(); }

    private ThreadManager()
    {
        executorService = Executors.newScheduledThreadPool(
                NUMBER_OF_CORES, new BackgroundThreadFactory());
    }

    public static ThreadManager getInstance() { return instance; }

    public void addTask(Runnable runnable)
    {
        executorService.submit(runnable);
    }

    private static class BackgroundThreadFactory implements ThreadFactory
    {
        @Override
        public Thread newThread(@NonNull Runnable runnable)
        {
            Thread thread = new Thread(runnable);
            thread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    }
}
