package com.example.radiationx.tryparse;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.radiationx.tryparse.htmltags.BaseTag;
import com.example.radiationx.tryparse.htmltags.IBaseTag;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FastActivity extends AppCompatActivity {

    String html =
            "<div class=\"msg-content emoticons\">\n" +
                    "    Тест bb кодов\n" +
                    "    <br><b>bold</b>\n" +
                    "    <br><i>italic</i>\n" +
                    "    <br><u>podcherk</u>\n" +
                    "    <br><del>zacherk</del>\n" +
                    "    <br><sub>pod</sub>\n" +
                    "    <br><sup>nad</sup>\n" +
                    "    <br>\n" +
                    "    <div align=\"left\">left</div>\n" +
                    "    <br>\n" +
                    "    <div align=\"center\">center</div>\n" +
                    "    <br>\n" +
                    "    <div align=\"right\">right</div>\n" +
                    "    <br><a title=\"Ссылка\" rel=\"nofollow\" href=\"http://роирир\" target=\"_blank\">link</a>\n" +
                    "    <br>\n" +
                    "    <div class=\"post-block quote\">\n" +
                    "        <div class=\"block-title\"></div>\n" +
                    "        <div class=\"block-body\">олррщри</div>\n" +
                    "    </div>\n" +
                    "    <br><font style=\"font-size:9px;color:gray;\">оопоиср</font>\n" +
                    "    <br>\n" +
                    "    <div class=\"post-block code box\">\n" +
                    "        <div class=\"block-title\"></div>\n" +
                    "        <div class=\"block-body \">орлтиап</div>\n" +
                    "    </div>\n" +
                    "    <br>\n" +
                    "    <div class=\"post-block spoil close\">\n" +
                    "        <div class=\"block-title\">прмгоор</div>\n" +
                    "        <div class=\"block-body\">лрарот</div>\n" +
                    "    </div>\n" +
                    "    <br>[HIDE]ллрммлт[/HIDE]\n" +
                    "    <br>\n" +
                    "    <ul>\n" +
                    "        <li>апср\n" +
                    "            <br>\n" +
                    "        </li>\n" +
                    "        <li>пнпр</li>\n" +
                    "    </ul>\n" +
                    "    <br>\n" +
                    "    <ol type=\"1\">\n" +
                    "        <li>поарото\n" +
                    "            <br>\n" +
                    "        </li>\n" +
                    "        <li>апааа\n" +
                    "            <br>\n" +
                    "        </li>\n" +
                    "        <li>лжппрр</li>\n" +
                    "    </ol>\n" +
                    "    <br><span style=\"color:coral\">ыалди</span>\n" +
                    "    <br><span style=\"background-color:coral\">ьлипс</span>\n" +
                    "    <br><span style=\"font-size:36pt;line-height:100%\">олмсч</span>\n" +
                    "    <br>[CUR]оимссрщ[/CUR]\n" +
                    "</div>";


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
        /*try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        parse(html);
    }

    private void parse(String html) {
        long time = System.currentTimeMillis();
        //Document document = Document.parse(Jsoup.parse(finalHtml).body().html());
        Document document = Document.parse(html);
        Log.d("kek", "html +" + document.getHtml());
        Log.d("kek", "time parse: " + Math.floor((System.currentTimeMillis() - time) * 8.2));
        long time2 = System.currentTimeMillis();
        list.addView(recurseUi(document.getRoot()));
        Log.d("kek", "time add ui:  " + Math.floor((System.currentTimeMillis() - time2) * 8.2));
        Log.d("kek", "time full:  " + Math.floor((System.currentTimeMillis() - time) * 8.2));
        Log.d("kek", "point iterations: " + iterations);
        Log.d("kek", "point iterations views: " + iViews);
        Log.d("kek", "point iterations textviews: " + iTextViews);
    }


    private BaseTag recurseUi(Element element) {
        //Log.d("kek", "element "+element.tagName()+" : "+element.getLevel());
        /*if (element.tagName().equals("br"))
            return null;*/
        BaseTag thisView = new BaseTag(this, element.tagName());
        String html = element.getText();

        boolean text = false;

        for (int i = 0; i < element.getElements().size(); i++) {
            Element child = element.get(i);
            BaseTag newView = recurseUi(child);
            /*if (child.attr("class") != null && child.attr("class").contains("post-block"))
                continue;*/
            if (child.getElements().size() == 0 && child.tagName().matches("^(b|i|u|del|sub|sup|span|a|br)$")) {


                html += child.html();
                Log.d("kek", "padla text " + child.tagName() + " : " + html);
                /*if (i + 1 < element.getSize() && element.get(i + 1).tagName().matches("^(div)$"))*/
                text = true;
                continue;
            } else {
                if (text) {
                    if (!html.isEmpty()) {
                        html = html.trim().replaceFirst("^<br>", "").replaceFirst("<br>$", "");
                        Log.d("kek", "final html " + html);
                        thisView.setHtmlText(Html.fromHtml(html));
                        html = "";
                    }
                }
                html += child.getText();
                newView.setHtmlText(Html.fromHtml(html));
                if (child.attr("align") != null) {
                    if (child.attr("align").equals("center")) {
                        newView.setGravity(Gravity.CENTER_HORIZONTAL);
                    } else if (child.attr("align").equals("right")) {
                        newView.setGravity(Gravity.END);
                    }
                }

                html = "";
                text = false;
            }
            Log.d("kek", "palda child " + child.tagName() + " : " + html);


            if (newView != null)
                thisView.addView(newView);

            iterations++;
        }

        return thisView;
    }


}
