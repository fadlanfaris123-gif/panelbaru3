package com.ff.panelpro;

import android.widget.CompoundButton;

public class AimbotListener implements CompoundButton.OnCheckedChangeListener {
    private OverlayService service;
    public AimbotListener(OverlayService service) { this.service = service; }
    @Override public void onCheckedChanged(CompoundButton v, boolean isChecked) { service.aimbotEnabled = isChecked; }
}￼Enter
