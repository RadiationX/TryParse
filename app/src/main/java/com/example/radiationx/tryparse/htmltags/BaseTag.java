package com.example.radiationx.tryparse.htmltags;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.radiationx.tryparse.App;
import com.example.radiationx.tryparse.UILImageGetter;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.sufficientlysecure.htmltextview.HtmlTextView;

/**
 * Created by radiationx on 28.08.16.
 */
public class BaseTag extends LinearLayout {
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = App.getContext().getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    protected int px1 = dpToPx(4), px2 = dpToPx(8), px3 = dpToPx(16), px4 = dpToPx(32), px5 = dpToPx(48);
    protected final static int red = Color.argb(48, 255, 0, 0);
    protected final static int green = Color.argb(48, 0, 255, 0);
    public TextView textView;
    protected float size() {
        return 16;
    }
    public BaseTag(Context context) {
        super(context);
        setOrientation(VERTICAL);
        /*setPadding(6, 4, 4, 12);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 4, 4, 4);
        setLayoutParams(params);*/
        //setBackgroundColor(red);
        //setHtmlText(tag);
    }

    public void setHtmlText(String text) {
        //setHtmlText(new SpannedString(text));
        /*HtmlTextView textView = new HtmlTextView(getContext());
        textView.setHtml(text, new UILImageGetter(textView, getContext()));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);*/
        //textView.setBackgroundColor(green);
        TextView textView = new TextView(getContext());
        textView.setText(Html.fromHtml(text));
        textView.setTextSize(size());
        addView(textView);
    }

    /*public void setHtmlText(Spanned text) {
        HtmlTextView textView = new HtmlTextView(getContext());
        textView.setText(text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setBackgroundColor(green);
        addView(textView);
    }*/

    public void setImage(String url){
        ImageView imageView = new ImageView(getContext());
        ImageLoader.getInstance().displayImage(url, imageView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, px2, 0, px2);
        imageView.setLayoutParams(params);
        //imageView.setBackgroundColor(green);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        addView(imageView);
    }

    public TextView getTextView() {
        return textView;
    }
}
