package com.example.radiationx.tryparse.htmltags;

import android.content.Context;
import android.graphics.Color;
import android.text.Spanned;
import android.text.SpannedString;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by radiationx on 28.08.16.
 */
public class BaseTag extends LinearLayout {
    private final static int red = Color.argb(48, 255, 0, 0);
    private final static int green = Color.argb(48, 0, 255, 0);
    public TextView textView;
    String tag;
    public BaseTag(Context context, String tag) {
        super(context);
        setOrientation(VERTICAL);
        setPadding(6, 4, 4, 12);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 4, 4, 4);
        setLayoutParams(params);
        setBackgroundColor(red);
        this.tag = tag;
        //setHtmlText(tag);
    }

    public void setHtmlText(String text) {
        setHtmlText(new SpannedString(text));
    }

    public void setHtmlText(Spanned text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 4, 4,4);
        textView.setLayoutParams(params);
        textView.setBackgroundColor(green);
        addView(textView);
    }

    public TextView getTextView() {
        return textView;
    }
}
