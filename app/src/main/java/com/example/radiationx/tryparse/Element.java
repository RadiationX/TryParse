package com.example.radiationx.tryparse;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radiationx on 27.08.16.
 */
public class Element {
    private ElementsList elements = new ElementsList();
    private Element parent;
    private List<Pair<String, String>> attributes = null;
    private String text = "";
    private String afterText = "";
    private String tagName = "ERROR-TAG";
    private int level = 0;
    private String attrsSource = "";

    public Element(String tagName, int level) {
        this.tagName = tagName;
        this.level = level;
    }

    public Element(String tagName) {
        this.tagName = tagName;
    }

    public Element(String tagName, String attrs) {
        if (tagName != null)
            this.tagName = tagName;
        if (attrs != null)
            this.attrsSource = attrs;
    }

    public String getAttrsSource() {
        return attrsSource;
    }

    public int getSize() {
        return elements.size();
    }

    public Element get(int i) {
        return elements.get(i);
    }

    public Element getLast() {
        if (elements.size() == 0)
            return null;
        return elements.get(elements.size() - 1);
    }

    public ElementsList getElements() {
        return elements;
    }

    public void add(Element element) {
        elements.add(element);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String tagName() {
        return tagName;
    }

    /*public void addAttr(String key, String value) {
        attributes.add(new Pair<>(key, value));
    }*/

    public String attr(String key) {
        for (Pair<String, String> p : getAttributes()) {
            if (p.first.equals(key)) {
                return p.second;
            }
        }
        return null;
    }

    public List<Pair<String, String>> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<>();
            ElementHelper.parseAttrs(attrsSource, attributes);
        }
        return attributes;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setAfterText(String afterText) {
        this.afterText = afterText;
    }
    public void addAfterText(String afterText){
        this.afterText = this.afterText.concat(afterText);
    }

    public String getAfterText() {
        return afterText;
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public Element getParent() {
        return parent;
    }

    public String html() {
        return ElementHelper.html(this, true).toString();
    }

    public String htmlNoParent() {
        return ElementHelper.html(this, false).toString();
    }

    public String getAllText() {
        return ElementHelper.getAllText(this).toString();
    }

    public void fixSpace() {
        ElementHelper.fixSpace(this);
    }

    public String ownText() {
        StringBuilder text = new StringBuilder();
        for (Element element : elements.toArray())
            text.append(" ").append(element.getAfterText());
        return text.toString().trim();
    }
}

