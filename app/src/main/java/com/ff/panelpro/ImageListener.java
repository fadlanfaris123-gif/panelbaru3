package com.ff.panelpro;

import android.media.ImageReader;

public class ImageListener implements ImageReader.OnImageAvailableListener {
    private ScreenCaptureHelper helper;

    public ImageListener(ScreenCaptureHelper helper) {
        this.helper = helper;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        if (helper != null) {
            helper.processImage(reader);
        }
    }
}￼Enter
