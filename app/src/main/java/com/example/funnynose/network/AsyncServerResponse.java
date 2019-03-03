package com.example.funnynose.network;

import android.util.Log;

import com.example.funnynose.constants.Session;

/**
 * @author Alex
 * Ассинхронное ожидание ответа от сервера
 * NOT CHANGE THIS FILE! NEVER!
 */
public class AsyncServerResponse extends ThreadGroup {
    private static final String GROUP_NAME = "Response async group";
    private static final long PAUSE = 5000;

    private AsyncTask callback;
    private AsyncTask failResponse;
    private AsyncTask failSuccessful;

    private boolean response = false;
    private boolean successful;
    private long timeout;

    private DThread main;
    private Thread waiter;

    private AsyncServerResponse() {
        super(GROUP_NAME);
    }

    public AsyncServerResponse(AsyncTask func) {
        this();
        callback = func;
    }

    /**
     * @param r - response, есть соединение с сервером
     * @param s - successful, данные верны
     * @param func - callback в случае верного ответа от сервера (successful = true)
     */
    public AsyncServerResponse(boolean r, boolean s, AsyncTask func) {
        this(func);
        response = r;
        successful = s;
        timeout = PAUSE;
    }


    /**
     * @param wait - максимальное время ожидания ответа
     * @param func - callback в случае верного ответа от сервера
     */
    public AsyncServerResponse(long wait, AsyncTask func) {
        this(func);
        if (wait < 0) {
            throw new IllegalArgumentException("timeout < 0");
        }
        timeout = wait;
    }

    /**
     * @param r - response, есть соединение с сервером
     * @param s - successful, данные верны
     * @param wait - максимальное время ожидания ответа
     * @param func - callback в случае верного ответа от сервера
     */
    public AsyncServerResponse(boolean r, boolean s, long wait, AsyncTask func) {
        this(func);
        response = r;
        successful = s;
        if (wait < 0) {
            throw new IllegalArgumentException("timeout < 0");
        }
        timeout = wait;
    }

    /**
     * Запуск асинхронного ожидания сервера
     */
    public void start() {
        if (main != null && main.mThread != null && waiter != null) {
            if (main.mThread.isAlive() || waiter.isAlive()) {
                return;
            }
        }
        response = false;
        mainThread();
        waiterThread().start();
    }

    /**
     * Остановка всех потоков в группе
     */
    public void interruptAll() {
        interrupt();
    }

    /**
     * @param response - true в случае ответа от сервера
     *                 - false в случае, если сервер не дал ответ
     */
    public void setResponse(boolean response) {
        this.response = response;
        if (response)
            main.responseTrue();
    }

    /**
     * @param successful - true в случае необходимости выполнения callback
     *                   - false иначе
     */
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    /**
     * @return имя группы потоков
     */
    public static String getGroupName() {
        return GROUP_NAME;
    }


    /**
     * @param failResponse - callback в случае, если сервер не дал ответ
     */
    public void setFailResponse(AsyncTask failResponse) {
        this.failResponse = failResponse;
    }


    /**
     * @param failSuccessful - callback в случае неверного ответа от сервера
     */
    public void setFailSuccessful(AsyncTask failSuccessful) {
        this.failSuccessful = failSuccessful;
    }

    /**
     * Функциональный интерфейс callback.
     */
    public interface AsyncTask {
        void call();
    }

    private void mainThread() {
        main = new DThread(this);
    }

    private Thread waiterThread() {
        waiter = new Thread(this, new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(AsyncServerResponse.this.timeout);
                    if (!AsyncServerResponse.this.response) {
                        if (!main.mThread.isInterrupted()) {
                            main.mThread.interrupt();
                            failResponse.call();
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d(Session.TAG, "waiter stopped - " + e.getMessage());
                }
            }
        });
        return waiter;
    }


    /**
     * Реализует поток, с возможностью прерывания ожидания
     */
    class DThread implements Runnable {
        Thread mThread;

        DThread(ThreadGroup tg) {
            mThread = new Thread(tg, this);
            mThread.start();
        }

        @Override
        public void run() {
            synchronized (this) {
                while (!response) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Log.d(Session.TAG, "main stopped without response - " + e.getMessage());
                        interrupt();
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                }

                if (successful) {
                    callback.call();
                } else if (failSuccessful != null) {
                    failSuccessful.call();
                }

                if (!waiter.isInterrupted()) {
                    waiter.interrupt();
                }

                Log.d(Session.TAG, "main thread end");
            }
        }

        synchronized void responseTrue() {
            notify();
        }
    }
}
