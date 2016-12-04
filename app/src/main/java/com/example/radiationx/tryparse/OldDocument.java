package com.example.radiationx.tryparse;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by radiationx on 03.12.16.
 */

public class OldDocument {
    private OldElement root;
    private final static Pattern nonClosedTags = Pattern.compile("(<(area|base|br|col|colgroup|command|embed|hr|img|input|keygen|link|meta|param|source|track|wbr)([^>]*?)(/|)>)");
    private final static Pattern strongOpenTag = Pattern.compile("<strong([^>]*?)>");
    private final static Pattern strongCloseTag = Pattern.compile("</strong>");
    private final static Pattern emOpenTag = Pattern.compile("<em([^>]*?)>");
    private final static Pattern emCloseTag = Pattern.compile("</em>");
    private final static Pattern delOpenTag = Pattern.compile("<del([^>]*?)>");
    private final static Pattern delCloseTag = Pattern.compile("</del>");
    private final static Pattern commentTag = Pattern.compile("<!--[\\s\\S]*?-->");
    private final static Pattern scriptBlock = Pattern.compile("<script[^>]*>[\\s\\S]*?</script>");

    private final static Pattern mainPattern = Pattern.compile("<([^/!][\\w]*)([^>]*)>([^<]*)|</([\\w]*)>([^<]*)");
    private final static Pattern attrPattern = Pattern.compile("([^ \"]*?)=\"([^\"]*?)\"");

    public static OldDocument parse(String html) {
        /*html = html.replaceAll("(<(area|base|br|col|colgroup|command|embed|hr|img|input|keygen|link|meta|param|source|track|wbr)([^>]*?)(/|)>)", "<$2$3></$2>");
        html = html.replaceAll("<strong([^>]*?)>", "<b$1>").replaceAll("</strong>", "</b>");
        html = html.replaceAll("<em([^>]*?)>", "<i$1>").replaceAll("</em>", "</i>");
        html = html.replaceAll("<del([^>]*?)>", "<strike$1>").replaceAll("</del>", "</strike>");
        html = html.replaceAll("<!--[\\s\\S]*?-->", "");
        html = html.replaceAll("<script[^>]*>[\\s\\S]*?</script>", "");*/

        html = nonClosedTags.matcher(html).replaceAll("<$2$3></$2>");
        /*html = strongOpenTag.matcher(html).replaceAll("<b$1>");
        html = strongCloseTag.matcher(html).replaceAll("</b>");
        html = emOpenTag.matcher(html).replaceAll("<i$1>");
        html = emCloseTag.matcher(html).replaceAll("</i>");
        html = delOpenTag.matcher(html).replaceAll("<strike$1>");
        html = delCloseTag.matcher(html).replaceAll("</strike>");*/
        html = commentTag.matcher(html).replaceAll("");
        html = scriptBlock.matcher(html).replaceAll("");
        int level = 0;
        OldDocument OldDocument = new OldDocument();
        Matcher matcher = mainPattern.matcher(html);

        Matcher attrMatcher;
        OldElement last = null;
        ArrayList<OldElement> lasts = new ArrayList<>(25);
        OldElement OldElement;
        while (matcher.find()) {
            if (lasts.size() > 0) {
                last = lasts.get(lasts.size() - 1);
            }
            if (matcher.group(1) != null) {
                OldElement = new OldElement(matcher.group(1).toLowerCase());

                if (!matcher.group(2).isEmpty()) {
                    attrMatcher = attrPattern.matcher(matcher.group(2));
                    while (attrMatcher.find())
                        OldElement.addAttr(attrMatcher.group(1), attrMatcher.group(2));
                }

                OldElement.setText(matcher.group(3));
                OldElement.setLevel(level);
                if (last != null)
                    OldElement.setParent(last.getLevel() == OldElement.getLevel() ? last.getParent() : last);

                OldDocument.add(OldElement);
                lasts.add(OldElement);
                level++;
            } else {
                if(lasts.size()>0){
                    assert last != null;
                    if(last.tagName().compareTo(matcher.group(4)) == 0)
                        last.setAfterText(matcher.group(5));
                    lasts.remove(last);
                }
                level--;
            }
        }
        return OldDocument;
    }

    public void add(OldElement children) {
        if (children.getLevel() == 0) {
            root = children;
            return;
        }
        findToAdd(root, children);
    }

    private void findToAdd(OldElement root, OldElement children) {
        if (children.getLevel() - 1 == root.getLevel()) {
            root.add(children);
        } else {
            findToAdd(root.getLast(), children);
        }
    }

    public OldElement getRoot() {
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
