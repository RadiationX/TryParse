package com.example.radiationx.tryparse;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by radiationx on 27.08.16.
 */
public class Document {
    private Element root;
    //private final static Pattern nonClosedTags = Pattern.compile("(<(area|base|br|col|colgroup|command|embed|hr|img|input|keygen|link|meta|param|source|track|wbr)([^>]*?)(/|)>)");
    //private final static Pattern unclosedTags = Pattern.compile("(?:area|area|br|col|colgroup|command|embed|hr|img|input|keygen|link|meta|param|source|track|wbr)");
    /*private final static Pattern commentTag = Pattern.compile("<!--[\\s\\S]*?-->");
    private final static Pattern scriptBlock = Pattern.compile("");*/

    private static Pattern mainPattern;
    //private final static Pattern attrPattern = Pattern.compile("([^ \"]*?)=[\"']([^\"']*)[\"']");
    private final static String[] uTags = {"area", "area", "br", "col", "colgroup", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"};

    public static void init() {
        if (mainPattern == null)
            mainPattern = Pattern.compile("(?:<(?:(?:!(?!DOCTYPE)|(?:script|style)[^>]*>)[\\s\\S]*?(?:--|\\/script|\\/style)|([\\/])?([\\w]*)(?: ([^>]*))?\\/?)>)(?:([^<]+))?(?:<\\/(?:script|--|style)>)?");
        ElementHelper.init();
    }

    public static Document parse(String html) {
        ElementsList unclosedTags = new ElementsList();
        Document document = new Document();
        Element tempElem, last = null;
        String tag, text;
        boolean lastNotNull = false;
        int nestingLevel = 0;

        //Более быстрый смособ убрать комментарии и скрипты
        /*StringBuilder sb = new StringBuilder();
        for (String s : html.split("<!--[\\s\\S]*?-->")) {
            sb.append(s);
        }
        html = sb.toString();
        sb = new StringBuilder();
        for (String s : html.split("<script[^>]*>[\\s\\S]*?</script>")) {
            sb.append(s);
        }
        html = sb.toString();*/

        Matcher matcher = mainPattern.matcher(html);
        int i = 0;
        while (matcher.find()) {
            i++;
            if (unclosedTags.size() > 0) {
                last = unclosedTags.get(unclosedTags.size() - 1);
                lastNotNull = true;
            }
            tag = matcher.group(2);
            text = matcher.group(4);
            if (matcher.group(1) == null & tag != null) {
                tempElem = new Element(tag, matcher.group(3));
                if (text != null)
                    tempElem.setText(text);
                tempElem.setLevel(nestingLevel);
                if (lastNotNull)
                    tempElem.setParent(last.getLevel() == tempElem.getLevel() ? last.getParent() : last);

                document.add(tempElem);
                if (!containsInUTag(tempElem.tagName())) {
                    unclosedTags.add(tempElem);
                    nestingLevel++;
                }
            } else {
                if (unclosedTags.size() > 0 && lastNotNull) {
                    if (tag == null) {
                        if (last.getLast() != null) {
                            last.getLast().addAfterText(matcher.group());
                            continue;
                        }
                    } else {
                        if (text != null && last.tagName().equals(tag))
                            last.setAfterText(text);
                    }
                    unclosedTags.remove(unclosedTags.size() - 1);
                }
                if (tag != null)
                    nestingLevel--;
            }
        }
        //TODO nestingLevel > 0 почему-то в некоторых случаях, например при парсинге полной страницы, но в итоге вроде всё валидно
        Log.d("myparser", "QualityControl : " + i + " : " + unclosedTags.size() + " : " + nestingLevel);
        for (Element el : unclosedTags.toArray()) {
            Log.e("UNCLOSED", "" + el.tagName());
        }
        return document;
    }

    private static boolean containsInUTag(String tag) {
        for (String uTag : uTags)
            if (uTag.equals(tag)) return true;
        return false;
    }

    public void add(Element children) {
        if (children.getLevel() == 0) {
            root = children;
            return;
        }
        findToAdd(root, children);
    }

    private void findToAdd(Element root, Element children) {
        if (children.getLevel() - 1 == root.getLevel()) {
            root.add(children);
        } else {
            findToAdd(root.getLast(), children);
        }
    }

    public Element getRoot() {
        return root;
    }

    public String html() {
        return root.html();
    }

    public String htmlNoParent() {
        return root.htmlNoParent();
    }

    public String getAllText() {
        return root.getAllText();
    }
}
