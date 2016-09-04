package com.example.radiationx.tryparse;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Pattern pattern = Pattern.compile("<div class=\"article-entry\"[^>]*?>([\\s\\S]*?)</div>[^<]*?<footer");

    private final static int green = Color.argb(48, 0, 255, 0);
    private final static int red = Color.argb(48, 255, 0, 0);
    private final static int blue = Color.argb(255, 0, 0, 255);
    private final OkHttpClient client = new OkHttpClient();

    public void run() throws Exception {
        Request request = new Request.Builder()
                .url("http://beardycast.com/2016/08/08/Beardygram/beardygram-5/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                parse(response.body().string());
            }
        });
    }

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
    }
    float coef = 1;
    private void parse(String html) {
        //coef = 8.2f;
        Log.d("kek", "check 1");
        //html = StringEscapeUtils.unescapeHtml4(html);
        Log.d("kek", "check 2");
        final Matcher matcher = pattern.matcher(html);
        Log.d("kek", "check 3");
        if (matcher.find()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis();
                    //Document document = Document.parse(Jsoup.parse(finalHtml).body().html());
                    Elements document = Jsoup.parse(matcher.group(1)).body().children();
                    Log.d("kek", "time parse: " + Math.floor((System.currentTimeMillis() - time) * coef));
                    long time2 = System.currentTimeMillis();
                    list.addView(recurseContent(document));
                    Log.d("kek", "time add ui:  " + Math.floor((System.currentTimeMillis() - time2) * coef));
                    Log.d("kek", "time full:  " + Math.floor((System.currentTimeMillis() - time) * coef));
                    Log.d("kek", "point iterations: " + iterations);
                }
            });
        }

    }
int iterations = 0;
    private HTMLTAG recurseContent(Elements elements) {
        HTMLTAG root = new HTMLTAG(this);
        for (Element thisElement : elements) {
            String ownText = thisElement.ownText();
            Element tempLinkElem = thisElement.select(">a").first();
            HTMLTAG thisView = getViewByTag(thisElement.tagName());
            boolean doRecurse = true;

            if (thisElement.tagName().equals("a")) {
                ((A) thisView).setUrl(thisElement.attr("href"));
                thisView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, ((A) view).getUrl(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (thisElement.tagName().equals("img")) {
                ((IMAGE) thisView).setImage(thisElement.attr("src"));
                thisView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, ((IMAGE) view).getUrl(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (!ownText.isEmpty()) {
                if (thisElement.tagName().matches("h1|h2|h3|h4|h5|h6")) {
                    thisView.setHtmlText(ownText);
                    if (tempLinkElem != null)
                        thisView.setTag(tempLinkElem.attr("href"));
                } else {
                    thisView.setHtmlText(Html.fromHtml(thisElement.html()));
                }
            }

            if (!thisElement.tagName().equals("li") && tempLinkElem != null)
                doRecurse = false;
            if (thisElement.tagName().equals("li")) {
                for (Element temp : thisElement.children()) {
                    if (!temp.tagName().matches("li|ul|a")) {
                        doRecurse = false;
                        break;
                    }
                }
            }

            if (doRecurse)
                thisView.addView(recurseContent(thisElement.children()));

            iterations++;
            root.addView(thisView);
        }
        return root;
    }

    private HTMLTAG getViewByTag(String tag) {
        switch (tag) {
            case "p":
                return new P(this);
            case "h1":
                return new H1(this);
            case "h2":
                return new H2(this);
            case "h3":
                return new H2(this);
            case "h4":
                return new H2(this);
            case "h5":
                return new H2(this);
            case "h6":
                return new H2(this);
            case "ul":
                return new Ul(this);
            case "li":
                return new Li(this);
            case "blockquote":
                return new BLOCKQUOTE(this);
            case "iframe":
                return new IFRAME(this);
            case "img":
                return new IMAGE(this);
            case "a":
                return new A(this);
            default:
                return new UNDEFINED(this);
        }
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

    class HTMLTAG extends LinearLayout {
        public HTMLTAG(Context context) {

            super(context);
            setOrientation(VERTICAL);
        }

        public void setHtmlText(String text) {
            setHtmlText(new SpannedString(text));
        }

        public void setHtmlText(Spanned text) {
            TextView textView = new TextView(getContext());
            textView.setText(text);
            addView(textView);
        }
    }

    class Li extends HTMLTAG {

        public Li(Context context) {
            super(context);
            setBackgroundColor(green);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 4, 4, 4);
            setLayoutParams(params);
        }
    }

    class Ul extends HTMLTAG {
        public Ul(Context context) {
            super(context);
            setBackgroundColor(red);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 8, 8, 8);
            setLayoutParams(params);
        }
    }

    class IFRAME extends HTMLTAG {
        public IFRAME(Context context) {
            super(context);
            setBackgroundColor(Color.DKGRAY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 24);
            setLayoutParams(params);
        }
    }

    class BLOCKQUOTE extends HTMLTAG {
        public BLOCKQUOTE(Context context) {
            super(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            setPadding(32, 8, 8, 24);
            setLayoutParams(params);
            setBackgroundColor(Color.LTGRAY);
        }
    }

    class UNDEFINED extends HTMLTAG {
        public UNDEFINED(Context context) {
            super(context);
            setBackgroundColor(Color.CYAN);
        }
    }

    class A extends HTMLTAG {
        private String url;

        public A(Context context) {
            super(context);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    class P extends HTMLTAG {
        public P(Context context) {
            super(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 16, 16, 0);
            setLayoutParams(params);
            setOrientation(VERTICAL);
        }
    }


    class IMAGE extends HTMLTAG {
        private String url;

        public IMAGE(Context context) {
            super(context);
            setBackgroundColor(red);
        }

        public void setImage(String url) {
            this.url = url;
            setHtmlText("IMAGE " + url);
        }

        public String getUrl() {
            return url;
        }
    }

    class H extends HTMLTAG {
        protected float size() {
            return 16;
        }

        public H(Context context) {
            super(context);
        }

        @Override
        public void setHtmlText(Spanned text) {
            TextView textView = new TextView(getContext());
            textView.setText(text);
            textView.setTextSize(size());
            addView(textView);
        }
    }

    class H2 extends H {
        @Override
        protected float size() {
            return super.size() * 1.5f;
        }

        public H2(Context context) {
            super(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 32, 16, 0);
            setLayoutParams(params);
        }
    }

    class H1 extends H {
        @Override
        protected float size() {
            return super.size() * 2.0f;
        }

        public H1(Context context) {
            super(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 48, 32, 0);
            setLayoutParams(params);
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
