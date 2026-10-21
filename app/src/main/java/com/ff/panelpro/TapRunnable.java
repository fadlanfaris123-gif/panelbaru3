package com.ff.panelpro;

public class TapRunnable implements Runnable {
    private AimService service;

    public TapRunnable(AimService service) {
        this.service = service;
    }

    @Override
    public void run() {
        if (service != null) {
            service.doTap();
        }
    }
}
