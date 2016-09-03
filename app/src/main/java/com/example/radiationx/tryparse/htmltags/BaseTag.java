package com.example.radiationx.tryparse.htmltags;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radiationx.tryparse.App;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by radiationx on 28.08.16.
 */
public class BaseTag extends LinearLayout {
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = App.getContext().getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    protected int px1 = dpToPx(4), px2 = dpToPx(8), px3 = dpToPx(16), px4 = dpToPx(32), px5 = dpToPx(48);
    /*protected final static int red = Color.argb(48, 255, 0, 0);
    protected final static int green = Color.argb(48, 0, 255, 0);*/
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
        //Log.d("kek", "texta '"+text+"'");
        //setHtmlText(new SpannedString(text));
        /*HtmlTextView textView = new HtmlTextView(getContext());
        textView.setHtml(text, new UILImageGetter(textView, getContext()));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);*/
        //textView.setBackgroundColor(green);
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        //textView.setText(Html.fromHtml(text));
        //textView.setText(text);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                textView.setText(Html.fromHtml(text));
            }
        }).run();*/
        /*textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(Html.fromHtml(text));
            }
        });*/
        new FormatTextTask(textView, text).execute();
        //formatHtml(textView, text);

        textView.setTextSize(size());
        addView(textView);
    }

   /* private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private void formatHtml(final TextView textView, final String text) {
        compositeSubscription.add(
                Observable.create(new Observable.OnSubscribe<Spanned>() {
                    @Override
                    public void call(Subscriber<? super Spanned> subscriber) {
                        try {
                            subscriber.onNext(Html.fromHtml(text));
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Spanned>() {
                    @Override
                    public void call(Spanned spanned) {
                        textView.setText(spanned);
                    }
                }));
    }*/

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
            if (textView != null)
                textView.setText(spanned);
        }
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

    public void setImage(String url) {
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
