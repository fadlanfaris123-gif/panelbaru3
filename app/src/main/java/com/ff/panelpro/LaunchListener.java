package com.ff.panelpro;

import android.content.Intent;
import android.view.View;

public class LaunchListener implements View.OnClickListener {
    private OverlayService service;
    public LaunchListener(OverlayService service) { this.service = service; }
    @Override public void onClick(View v) {
        Intent launchIntent = service.getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            service.startActivity(launchIntent);
        }
    }
}￼Enter
