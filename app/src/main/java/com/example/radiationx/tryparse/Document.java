package com.example.radiationx.tryparse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by radiationx on 27.08.16.
 */
public class Document {
    private Element root;

    public static Document parse(String html) {
        html = html.replaceAll("(<(area|base|br|col|colgroup|command|embed|hr|img|input|keygen|link|meta|param|source|track|wbr)[^>]*?>)", "$1</$2>");
        html = html.replaceAll("<strong([^>]*?)>", "<b$1>").replaceAll("</strong>", "</b>");
        html = html.replaceAll("<em([^>]*?)>", "<i$1>").replaceAll("</em>", "</i>");
        html = html.replaceAll("<!--[\\s\\S]*?-->", "");
        html = html.replaceAll("<script[^>]*>[\\s\\S]*?</script>", "");
        //html = html.replaceAll("\t","");

        final Pattern mainPattern = Pattern.compile("<([^/!][\\w]*)([^>]*)>([^<]*)|</([\\w]*)>([^<]*)");
        final Pattern attrPattern = Pattern.compile("([^ \"]*?)=\"([^\"]*?)\"");
        int level = 0;
        Document document = new Document();
        final Matcher matcher = mainPattern.matcher(html);
        Matcher attrMatcher;
        Element last = null;
        List<Element> lasts = new ArrayList<>();
        while (matcher.find()) {
            if (lasts.size() > 0) {
                last = lasts.get(lasts.size() - 1);
            }
            if (matcher.group(1) != null) {
                Element element = new Element(matcher.group(1).toLowerCase());

                attrMatcher = attrPattern.matcher(matcher.group(2));
                while (attrMatcher.find())
                    element.addAttr(attrMatcher.group(1), attrMatcher.group(2));

                element.setText(matcher.group(3).replaceAll("\t", "").trim());
                element.setLevel(level);
                if (last != null)
                    element.setParent(last.getLevel() == element.getLevel() ? last.getParent() : last);

                document.add(element);
                lasts.add(element);
                level++;
            } else {
                if (last != null && last.tagName().equals(matcher.group(4)))
                    last.setAfterText(matcher.group(5).replaceAll("\t", "").trim());
                if (lasts.size() > 0)
                    lasts.remove(lasts.size() - 1);
                level--;
            }
        }
        return document;
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

    public String getHtml() {
        return root.html();
    }

    public String getAllText() {
        return root.getAllText();
    }
}
