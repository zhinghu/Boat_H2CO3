/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import org.koishi.launcher.h2co3.core.fakefx.beans.property.BooleanProperty;

public class LogWindow extends ScrollView {

    private boolean autoTint;
    private BooleanProperty visibilityProperty;
    private H2CO3TextView textView;
    private int lineCount;

    public LogWindow(Context context) {
        super(context);
        autoTint = false;
        init(context);
    }

    public LogWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LogWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LogWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.textView = new H2CO3TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(textView);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(15);
        textView.setLineSpacing(0, 1f);
        textView.setEllipsize(null);
    }

    public final boolean getVisibilityValue() {
        return visibilityProperty == null || visibilityProperty.get();
    }

    public void appendLog(String str) {
        if (!getVisibilityValue()) {
            return;
        }
        lineCount++;
        this.post(() -> {
            if (textView != null) {
                if (lineCount < 100) {
                    textView.append(str);
                } else {
                    cleanLog();
                }
                fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void cleanLog() {
        this.textView.setText("");
        lineCount = 0;
    }
}
