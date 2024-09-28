package net.hwyz.iov.idcm.launcher;

import android.app.Application;
import android.content.Intent;

import com.tencent.mmkv.MMKV;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主应用程序
 *
 * @author hwyz_leo
 */
public class MainApplication extends Application {

    private static MainApplication mMainApplication;
    private ExecutorService mSingleThreadPool;

    public static MainApplication getApplication() {
        if (mMainApplication == null) {
            mMainApplication = new MainApplication();
        }
        return mMainApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sendAccountBroadcast();
        init();
        MMKV.initialize(this);
    }

    private void init() {
        mMainApplication = this;
        mSingleThreadPool = Executors.newSingleThreadExecutor(r -> new Thread(r, "singleThread-" + r.hashCode()));
        InitThread initThread = new InitThread("mainApplication");
        initThread.start();
    }

    private void initAccount() {

    }

    private void sendAccountBroadcast() {
        try {
            Intent intent = new Intent();
            intent.setPackage("net.hwyz.iov.idcm.account");
            intent.setAction("net.hwyz.iov.idcm.account.action.active.OPEN_ACTIVE_PAGE");
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class InitThread extends Thread {
        public InitThread(String threadName) {
            super(threadName);
        }

        @Override
        public void run() {
            super.run();
            initAccount();
        }
    }

}
