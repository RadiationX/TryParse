package com.example.radiationx.tryparse;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by radiationx on 03.12.16.
 */

public class ElementHelper {
    //private static Pattern pattern;
    private static Pattern attrPattern;
    private final static String[] tags = {"br", "img", "meta"};

    public static void init() {
        /*if (pattern == null)
            pattern = Pattern.compile("br|img|meta");*/
        if (attrPattern == null)
            attrPattern = Pattern.compile("([^ \"]*?)\\s*?=\\s*?[\"']([^\"']*)[\"']");
    }

    private static boolean containsInUTag(String tag) {
        for (String uTag : tags)
            if (uTag.equals(tag)) return true;
        return false;
    }

    public static StringBuilder html(Element element, boolean withParent) {
        StringBuilder html = new StringBuilder();
        if (withParent) {
            html.append("<").append(element.tagName()).append(" ");
            /*for (Pair<String, String> pair : element.getAttributes()) {
                html = html.append(" ").append(pair.first).append("=\"").append(pair.second).append("\"");
            }*/
            html.append(element.getAttrsSource());
            html.append(">");
        }

        if (!element.getText().isEmpty()) {
            html.append(element.getText());
        }

        for (Element el : element.getElements())
            html.append(html(el, true));

        if (withParent) {
            if (containsInUTag(element.tagName()))
                html.append("</").append(element.tagName()).append(">");

            if (!element.getAfterText().isEmpty())
                html.append(" ").append(element.getAfterText());
        }

        html.append(" ");
        return html;
    }


    public static StringBuilder getAllText(Element element) {
        StringBuilder text = new StringBuilder();
        text.append(" ").append(element.getText());

        for (Element el : element.getElements())
            text.append(getAllText(el));

        text.append(" ").append(element.getAfterText());
        return text;
    }

    public static void fixSpace(Element element) {
        //element.setText(element.getText().replaceAll(" ", "&nbsp;"));
        for (Element el : element.getElements())
            fixSpace(el);
        //element.setAfterText(element.getAfterText().replaceAll(" ", "&nbsp;"));
    }


    public static List<Pair<String, String>> parseAttrs(String source, List<Pair<String, String>> attrs) {
        if (!source.isEmpty()) {
            Matcher attrMatcher = attrPattern.matcher(source);
            while (attrMatcher.find())
                attrs.add(new Pair<>(attrMatcher.group(1), attrMatcher.group(2)));
        }
        return attrs;
    }
}
