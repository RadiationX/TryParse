package com.example.radiationx.tryparse;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radiationx.tryparse.htmltags.BaseTag;
import com.example.radiationx.tryparse.htmltags.CodePostBlock;
import com.example.radiationx.tryparse.htmltags.H1Tag;
import com.example.radiationx.tryparse.htmltags.H2Tag;
import com.example.radiationx.tryparse.htmltags.LiTag;
import com.example.radiationx.tryparse.htmltags.PTag;
import com.example.radiationx.tryparse.htmltags.PostBlock;
import com.example.radiationx.tryparse.htmltags.QuotePostBlock;
import com.example.radiationx.tryparse.htmltags.SpoilerPostBlock;
import com.example.radiationx.tryparse.htmltags.UlTag;
import com.nostra13.universalimageloader.utils.L;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FastActivity extends AppCompatActivity {

    String html =
            "<div>tdivt<b>tbt<span>tspant<i tit=\"\"></i></span></b></div>\n" +
                    "<div>tdivt<b>tbt<span>tspant</span></b></div>";


    private final static int green = Color.argb(48, 0, 255, 0);
    private final static int red = Color.argb(48, 255, 0, 0);
    private final static int blue = Color.argb(255, 0, 0, 255);
    private final OkHttpClient client = new OkHttpClient();

    private int iViews = 0;
    private int iTextViews = 0;
    int iterations = 0;


    private LinearLayout list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (LinearLayout) findViewById(R.id.list);
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //parse(html);
    }

    private Pattern pattern = Pattern.compile("(<div class=\"article-entry\"[^>]*?>[\\s\\S]*?</div>)[^<]*?<footer");
    private Pattern pattern2 = Pattern.compile("(<div[^>]*?post_body[^>]*>[\\s\\S]*?</div>)</div><div data-post|(<div[^>]*?postcolor[^>]*?>[\\s\\S]*?<\\/div>)[^<]*?<\\/td>[^<]*?<\\/tr>[^<]*?<tr id=\"pb");
    //private Pattern pattern = Pattern.compile("group-item([^\"]*?)\" data-message-id=\"145532\"[^>]*?data-unread-status=\"([^\"]*?)\">[\\s\\S]*?</b> ([^ <]*?) [\\s\\S]*?src=\"([^\"]*?)\"[\\s\\S]*?(<div[^>]*?msg-content[^>]*?>[\\s\\S]*?</div>)([^<]*?</div>[^<]*?<div (class=\"list|id=\"threa|class=\"date))");

    float coef = 1;

    private void parse(String html) {
        Log.d("kek", "loaded html " + html);
        //coef = 8.2f;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setTitle("loaded");
            }
        });

        final Matcher matcher = pattern.matcher(html);
        Log.d("kek", "check 3");
        if (matcher.find()) {

            final String finalHtml = matcher.group(1) == null ? matcher.group(2) : matcher.group(1);
            Log.d("kek", "final html " + finalHtml);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final long time = System.currentTimeMillis();
                    //Document document = Document.parse(Jsoup.parse(finalHtml).body().html());
                    Document document = Document.parse(finalHtml);
                    Log.d("kek", "time parse: " + Math.floor((System.currentTimeMillis() - time) * coef));
                    final long time2 = System.currentTimeMillis();
                    list.addView(recurseUi(document.getRoot()));
                    Log.d("kek", "time recurse ui:  " + Math.floor((System.currentTimeMillis() - time2) * coef));
                    Log.d("kek", "point iterations: " + iterations);
                    Log.d("kek", "point iterations views: " + iViews);
                    Log.d("kek", "point iterations textviews: " + iTextViews);
                    Log.d("kek", "time full:  " + Math.floor((System.currentTimeMillis() - time) * coef));
                    getSupportActionBar().setTitle("ui " + Math.floor((System.currentTimeMillis() - time) * coef));
                }
            });

        }
    }


    private final static Pattern p2 = Pattern.compile("^(b|i|u|del|s|strike|sub|sup|span|a|br)$");

    public Context getContext() {
        return this;
    }

    private BaseTag recurseUi(final Element element) {
        BaseTag thisView = null;
        String elClassios = element.attr("class");
        if (elClassios != null && elClassios.contains("post-block")) {
            if (elClassios.contains("quote")) {
                thisView = new QuotePostBlock(getContext());
            } else if (elClassios.contains("code")) {
                thisView = new CodePostBlock(getContext());
                element.getLast().fixSpace();
            } else if (elClassios.contains("spoil")) {
                thisView = new SpoilerPostBlock(getContext());
            } else {
                thisView = new PostBlock(getContext());
            }
            if (!element.get(0).htmlNoParent().trim().equals(""))
                ((PostBlock) thisView).setTitle(Html.fromHtml(element.get(0).htmlNoParent().trim()));
            else
                ((PostBlock) thisView).hideTitle();
            ((PostBlock) thisView).addBody(recurseUi(element.getLast()));
            return thisView;
        } else {
            thisView = getViewByTag(element.tagName());
        }
        if (element.tagName().equals("img")) {
            thisView.setImage("http://beardycast.com/".concat(element.attr("src")));
            thisView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), element.attr("src"), Toast.LENGTH_SHORT).show();
                }
            });
            if (element.attr("alt") != null) {
                TextView textView = thisView.setHtmlText(element.attr("alt"));
                thisView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setTextSize(12);
            }

            return thisView;
        }

        String html = element.getText();

        boolean text = true;

        for (int i = 0; i < element.getElements().size(); i++) {
            Element child = element.get(i);
            BaseTag newView = null;
            if (p2.matcher(child.tagName()).matches()) {
                html = html.concat(child.html());
                text = true;
                continue;
            } else {
                newView = recurseUi(child);
                if (text) {
                    html = startBreakTag.matcher(html).replaceFirst("");
                    html = endBreakTag.matcher(html).replaceFirst("");
                    html = html.trim();
                    if (!html.isEmpty()) {
                        thisView.setHtmlText(html);
                        iTextViews++;
                        html = "";
                    }
                }
                html = "";
                text = false;
            }
            if (newView != null)
                thisView.addView(newView);

            iterations++;
        }
        html = html.trim();
        if (!html.isEmpty()) {
            html = startBreakTag.matcher(html).replaceFirst("");
            html = endBreakTag.matcher(html).replaceFirst("");
            html = html.trim();
            html = html.concat(element.getAfterText());
            thisView.setHtmlText(html);
            iTextViews++;
            html = "";
        }
        return thisView;
    }

    private final static Pattern startBreakTag = Pattern.compile("^([ ]*|)<br>");
    private final static Pattern endBreakTag = Pattern.compile("<br>([ ]*|)$");

    private BaseTag getViewByTag(String tag) {
        switch (tag) {
            case "h1":
                return new H1Tag(this);
            case "h2":
                return new H2Tag(this);
            case "ul":
                return new UlTag(this);
            case "li":
                return new LiTag(this);
            case "p":
                return new PTag(this);
            default:
                return new BaseTag(this);
        }
    }

    public void run() throws Exception {
        final Request request = new Request.Builder()
                //.url("http://beardycast.com/2016/09/13/EDC/edc-7-brizitsky/")
                .url("http://4pda.ru/forum/index.php?showtopic=84979/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                final Matcher matcher = pattern2.matcher(response.body().string());
                if (matcher.find()) {
                    loadedHtml = matcher.group(1);
                    if (loadedHtml == null) {
                        loadedHtml = matcher.group(2);
                    }
                }
                parseHtmlTest(loadedHtml);
                //parse(response.body().string());
            }
        });
    }

    String loadedHtml;

    private void parseHtmlTest(String s) {
        //loadedHtml = s;
        /*long time = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            Document.parse(loadedHtml);
        }
        Log.d("myparser", "test average: " + ((System.currentTimeMillis() - time) / 20f));
        time = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            OldDocument.parse(loadedHtml);
        }
        Log.d("myparser", "test average: " + ((System.currentTimeMillis() - time) / 20f));*/
        //Document.parse(loadedHtml);
        Log.d("myparser", "start");
        long time = 0;
        time = System.currentTimeMillis();
        Document.parse(s);
        Log.d("myparser", "test new: " + ((System.currentTimeMillis() - time)));

        time = System.currentTimeMillis();
        OldDocument.parse(s);
        Log.d("myparser", "test old: " + ((System.currentTimeMillis() - time)));

        time = System.currentTimeMillis();
        Jsoup.parseBodyFragment(s);
        Log.d("myparser", "test jsoup: " + ((System.currentTimeMillis() - time)));
        //Log.d("myparser", "parsed : "+document.html());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("reparse").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                parseHtmlTest(loadedHtml);
                return false;
            }
        }).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }
}
