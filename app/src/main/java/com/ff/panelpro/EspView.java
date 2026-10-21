package com.ff.panelpro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class EspView extends View {
    private Paint boxPaint;
    private List<Rect> targets = new ArrayList<>();

    public EspView(Context context) {
        super(context);
        boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(4f);
    }

    public void updateBoxes(List<Rect> newTargets) {
        this.targets = newTargets;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Rect r : targets) {
            canvas.drawRect(r, boxPaint);
        }
    }
}￼Enter
