package com.example.findshava.customClass;

import android.os.Looper;

public class SafeRunnable implements Runnable {

    private Runnable runnable;

    public SafeRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(this.runnable).start();
        } else {
            runnable.run();
        }
    }
}
