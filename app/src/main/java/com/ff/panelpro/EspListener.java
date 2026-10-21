package com.ff.panelpro;

import android.widget.CompoundButton;

public class EspListener implements CompoundButton.OnCheckedChangeListener {
    private OverlayService service;
    public EspListener(OverlayService service) { this.service = service; }
    @Override public void onCheckedChanged(CompoundButton v, boolean isChecked) { service.espEnabled = isChecked; }
}￼Enter
