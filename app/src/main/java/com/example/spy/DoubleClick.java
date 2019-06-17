package com.example.spy;

import android.os.Handler;
import android.view.View;

import java.util.logging.LogRecord;

public class DoubleClick implements View.OnClickListener {
    private int DOUBLE_CLICK_WAIT;
    private final Handler handler = new Handler();
    private final DoubleClickListener doubleClickListener;

    private int clicks;

    private boolean isClicked; // check the handler is busy or not;

    public DoubleClick(final DoubleClickListener doubleClickListener) {
        this(doubleClickListener, 200);
        DOUBLE_CLICK_WAIT = 200; // default time to wait the second click.
    }


    public DoubleClick(final DoubleClickListener doubleClickListener, int DOUBLE_CLICK_WAIT) {
        this.doubleClickListener = doubleClickListener;
        this.DOUBLE_CLICK_WAIT = DOUBLE_CLICK_WAIT; // specified time to wait the second click.
    }

    @Override
    public void onClick(final View v) {
        if (!isClicked) {
            //  Prevent multiple click in this short time
            isClicked = true;

            // Increase clicks count
            clicks++;

            handler.postDelayed(new Runnable() {
                public final void run() {

                    if (clicks >= 2) {  // Double tap.
                        doubleClickListener.onDoubleClick(v);
                    }

                    if (clicks == 1) {  // Single tap
                        doubleClickListener.onSingleClick(v);
                    }

                    // we need to  restore clicks count
                    clicks = 0;
                }
            }, DOUBLE_CLICK_WAIT);
            isClicked = false;
        }
    }
}
