package com.example.findshava.isReady;

public interface IsReady {
    void setIsReadyListener(IsReadyListener isReadyListener);

    interface IsReadyListener {
        void ready();
    }
}
