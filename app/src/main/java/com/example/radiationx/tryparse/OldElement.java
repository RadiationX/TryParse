package com.example.radiationx.tryparse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by radiationx on 03.12.16.
 */

public class OldElement {

    private List<OldElement> OldElements = new ArrayList<>();
    private OldElement parent;
    private HashMap<String, String> attributes = new HashMap<>();
    private String text = "";
    private String afterText = "";
    private String tagName = "";
    private int level = 0;

    public OldElement(String tagName, int level) {
        this.tagName = tagName;
        this.level = level;
    }

    public OldElement(String tagName) {
        this.tagName = tagName;
    }

    public int getSize() {
        return OldElements.size();
    }

    public OldElement get(int i) {
        return OldElements.get(i);
    }

    public OldElement getLast() {
        if (OldElements.size() == 0)
            return null;
        return OldElements.get(OldElements.size() - 1);
    }

    public List<OldElement> getOldElements(){
        return OldElements;
    }

    public void add(OldElement OldElement) {
        OldElements.add(OldElement);
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

    public void addAttr(String key, String value) {
        attributes.put(key, value);
    }

    public String attr(String key) {
        return attributes.get(key);
    }

    public HashMap<String, String> getAttributes() {
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

    public String getAfterText() {
        return afterText;
    }

    public void setParent(OldElement parent) {
        this.parent = parent;
    }

    public OldElement getParent() {
        return parent;
    }

    public String html() {
        return html(this, true);
    }
    public String htmlNoParent() {
        return html(this, false);
    }

    Pattern pattern = Pattern.compile("br|img|meta");
    Matcher matcher;
    public String html(OldElement OldElement, boolean withParent) {
        String html = "";
        /*if (!OldElement.tagName().matches("a|meta|p|span|img")) {
            html = html.concat("\n");
            for (int k = 0; k < OldElement.getLevel(); k++)
                html = html.concat("\t");
        }*/

        if(withParent){
            html = html.concat("<").concat(OldElement.tagName());
            for(Map.Entry<String, String> entry : OldElement.getAttributes().entrySet()) {
                html = html.concat(" ").concat(entry.getKey()).concat("=\"").concat(entry.getValue()).concat("\"");
            }
            html = html.concat(">");
        }

        if (!OldElement.getText().isEmpty()) {
            //html = html.concat(probel);
            html = html.concat(OldElement.getText());
        }

        for (int i = 0; i < OldElement.getSize(); i++) {
            html = html.concat(html(OldElement.get(i), true));
        }

        /*if (!OldElement.tagName().matches("br|meta|a|p|span|img")) {
            html = html.concat("\n");
            for (int k = 0; k < OldElement.getLevel(); k++)
                html = html.concat("\t");
        }*/
        if(withParent){
            matcher = pattern.matcher(OldElement.tagName());
            if (!matcher.matches()) {
                html = html.concat("</").concat(OldElement.tagName()).concat(">");
            }
            if (!OldElement.getAfterText().isEmpty()) {
                html = html.concat(probel);
                html = html.concat(OldElement.getAfterText());
            }
        }

        html = html.concat(probel);
        return html;
    }

    public String getAllText() {
        return getAllText(this);
    }

    private final static String probel = " ";

    public String getAllText(OldElement OldElement) {
        String text = "";
        //text+=" "+OldElement.getText();
        text = text.concat(probel).concat(OldElement.getText());

        for (int i = 0; i < OldElement.getSize(); i++) {
            text = text.concat(getAllText(OldElement.get(i)));
        }
        text = text.concat(probel).concat(OldElement.getAfterText());

        return text;
    }

    public static void fixSpace(OldElement OldElement){
        OldElement.setText(OldElement.getText().replaceAll(" ", "&nbsp;"));
        for (int i = 0; i < OldElement.getSize(); i++)
            fixSpace(OldElement.get(i));
        OldElement.setAfterText(OldElement.getAfterText().replaceAll(" ", "&nbsp;"));
    }


    public OldElement selectLink(){
        for(OldElement OldElement:OldElements){
            if(OldElement.tagName().equals("a"))
                return OldElement;
        }
        return null;
    }


    public String ownText(){
        String text = getText();
        for(OldElement OldElement:OldElements){
            text+=" "+OldElement.getAfterText();
        }
        return text.trim();
    }
}
