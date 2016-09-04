package com.example.radiationx.tryparse.htmltags;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.radiationx.tryparse.App;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by radiationx on 28.08.16.
 */
public class BaseTag extends LinearLayout {
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = App.getContext().getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    protected int px1 = dpToPx(4), px2 = dpToPx(8), px3 = dpToPx(16), px4 = dpToPx(32), px5 = dpToPx(48);
    public TextView textView;

    protected float size() {
        return 16;
    }

    public BaseTag(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public TextView setHtmlText(String text) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        new FormatTextTask(textView, text).execute();

        textView.setTextSize(size());
        addView(textView);
        return textView;
    }

    private class FormatTextTask extends AsyncTask<Void, Void, Void> {
        private TextView textView;
        private String text;
        private Spanned spanned;

        FormatTextTask(TextView textView, String text) {
            this.textView = textView;
            this.text = text;
        }

        protected Void doInBackground(Void... urls) {
            spanned = Html.fromHtml(text);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (textView != null){
                textView.setText(spanned);
                if(spanned.toString().isEmpty()){
                    textView.setVisibility(GONE);
                }
            }
        }
    }

    public void setImage(String url) {
        ImageView imageView = new ImageView(getContext());
        ImageLoader.getInstance().displayImage(url, imageView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, px2, 0, px2);
        imageView.setLayoutParams(params);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        addView(imageView);
    }

    public TextView getTextView() {
        return textView;
    }
}
