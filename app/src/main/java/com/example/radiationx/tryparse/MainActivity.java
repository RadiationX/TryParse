package com.example.radiationx.tryparse;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String html =
            "<ul>\n" +
                    "    <li><a href=\"#гаджеты\">Гаджеты</a>\n" +
                    "        <ul>\n" +
                    "            <li><a href=\"#macbook-12\">Macbook (12’’)</a></li>\n" +
                    "            <li><a href=\"#флэшка\">Флэшка</a></li>\n" +
                    "            <li><a href=\"#apple-watch-sport-42mm\">Apple Watch Sport (42mm)</a></li>\n" +
                    "            <li><a href=\"#iphone-6\">iPhone 6</a></li>\n" +
                    "            <li><a href=\"#кардхолдер-mywalit\">Кардхолдер Mywalit</a></li>\n" +
                    "            <li><a href=\"#victorinox-swiss-army-tourist-pocket-knife\">Victorinox Swiss Army Tourist Pocket Knife</a></li>\n" +
                    "            <li><a href=\"#чехол-handwers-portside\">Чехол Handwers Portside</a></li>\n" +
                    "            <li><a href=\"#iqos\">iQOS</a></li>\n" +
                    "        </ul>\n" +
                    "    </li>\n" +
                    "    <li><a href=\"#приложения\">Приложения</a>\n" +
                    "        <ul>\n" +
                    "            <li>\n" +
                    "                <a href=\"#яндекскартыhttpsitunesapplecomruappandekskartyid313877526mt8\"></a><a href=\"https://itunes.apple.com/ru/app/andeks.karty/id313877526?mt=8\" target=\"_blank\" rel=\"external\">Яндекс.Карты</a></li>\n" +
                    "            <li>\n" +
                    "                <a href=\"#ia-writerhttpsitunesapplecomruappia-writerid775737172mt8\"></a><a href=\"https://itunes.apple.com/ru/app/ia-writer/id775737172?mt=8\" target=\"_blank\" rel=\"external\">iA Writer</a></li>\n" +
                    "            <li>\n" +
                    "                <a href=\"#tweetbothttpsitunesapplecomruapptweetbot-4-for-twitterid1018355599mt8\"></a><a href=\"https://itunes.apple.com/ru/app/tweetbot-4-for-twitter/id1018355599?mt=8\" target=\"_blank\" rel=\"external\">Tweetbot</a></li>\n" +
                    "            <li>\n" +
                    "                <a href=\"#beamerhttpsbeamer-appcom\"></a><a href=\"https://beamer-app.com\" target=\"_blank\" rel=\"external\">Beamer</a></li>\n" +
                    "        </ul>\n" +
                    "    </li>\n" +
                    "</ul>\n";

    private final static int green = Color.argb(48, 0, 255, 0);
    private final static int red = Color.argb(48, 255, 0, 0);
    private final static int blue = Color.argb(255, 0, 0, 255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout list = (LinearLayout) findViewById(R.id.list);

        //Парсим список и выбираем корневой ul
        Element element = Jsoup.parse(html).select("ul").first();

        //Рекурсивной функцией создаем и заполняем список
        long date = new Date().getTime();
        Ul ul = recurseUi(element);
        Log.d("kek", "executing " + (new Date().getTime() - date) + "ms");
        //...
        list.addView(ul);
    }


    private Ul recurseUi(Element ul) {
        Ul ulElem = new Ul(this);
        if (ul == null)
            return ulElem;
        for (Element li : ul.select(">li")) {
            Li liElem = new Li(this);
            Elements links = li.select(">a");
            if (links != null) {
                //Правильно находит ссылки для списков такого вида: http://beardycast.com/2016/07/26/Vladimir_P/edc-1-vladimir/
                Element link = links.select("[href*='#']").first();
                String linkTitle = link.text().trim();
                final String linkHref = link.attr("href").trim().replaceFirst("(#.*?)http.*", "$1");
                if (linkTitle.isEmpty()) {
                    link = links.select("[rel='external']").first();
                    if (link != null)
                        linkTitle = link.text();
                    else
                        linkTitle = "PARSE ERROR";
                }

                //Создаем textview
                TextView textView = new TextView(this);
                textView.setText(linkTitle);
                textView.setTextColor(blue);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, linkHref, Toast.LENGTH_SHORT).show();
                    }
                });
                liElem.addView(textView);
            }

            //Рекурсивно продолжаем парсить ul и добавляем в текущий корневой li
            liElem.addView(recurseUi(li.select(">ul").first()));

            //Получившийся li пхаем в текущий корневой ul
            ulElem.addView(liElem);
        }
        return ulElem;
    }


    class Li extends LinearLayout {

        public Li(Context context) {
            super(context);
            setBackgroundColor(green);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 4, 4, 4);
            setLayoutParams(params);
            setOrientation(VERTICAL);
        }
    }

    class Ul extends LinearLayout {

        public Ul(Context context) {
            super(context);
            setBackgroundColor(red);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 8, 8, 8);
            setLayoutParams(params);
            setOrientation(VERTICAL);
        }
    }

    //Тренировка на стрингах. Изначально level указать 0
    private String recurse(Element ul, int level) {
        level++;
        String result = "";
        if (ul == null)
            return result;
        for (Element li : ul.select(">li")) {
            result += "\n";
            boolean isHead = li.select(">ul").size() != 0;
            if (isHead) {
                Log.d("kek", level + " level");
            }
            String margin = "";
            for (int i = 0; i < level; i++) {
                margin += "@";
            }
            Log.d("kek", "margin '" + margin + "'");
            Elements links = li.select(">a");
            if (links != null) {
                result += margin + links.last().text()/*+ "("+links.last().attr("href")+")"*/;
            }
            result += recurse(li.select(">ul").first(), level);
        }
        return result;
    }
}
