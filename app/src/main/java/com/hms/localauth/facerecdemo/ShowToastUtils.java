/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */
package com.hms.localauth.facerecdemo;
import android.content.Context;
import android.widget.Toast;
/**
 * Tool class for displaying popups
 *
 * @since 2019-06-27
 */
public class ShowToastUtils {
    private Toast toast = null;
    /**
     * Display the message
     *
     * @param context : The context to use. Usually your Application or Activity object.
     * @param text : The text to show. Can be formatted text.
     * @param duration : How long to display the message. Either LENGTH_SHORT or
     * LENGTH_LONG Value is LENGTH_SHORT, or LENGTH_LONG
     */
    public void showToast(Context context, String text, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
    }
}