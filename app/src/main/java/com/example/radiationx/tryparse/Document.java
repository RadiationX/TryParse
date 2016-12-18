package com.example.radiationx.tryparse;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
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
            mainPattern = Pattern.compile("(?:<(?:!--[\\s\\S]*?--|(?:(script|style)(?: )?([^>]*)>)([\\s\\S]*?)(?:<\\/\\1)|([\\/])?([\\w]*)(?: ([^>]*))?\\/?)>)(?:([^<]+))?");
        ElementHelper.init();
    }

    public static Document parse(String html) {
        Document doc = new Document();
        ElementsList openedTags = new ElementsList();
        ArrayList<String> errorTags = new ArrayList<>();
        Element lastOpened = null, lastClosed = null, newElement;
        //ssTag - блоки script/style, т.к в них может быть всякая дичь
        String tag, text, afterText, ssTag;
        boolean ssTagNull;
        Matcher m = mainPattern.matcher(html);
        int tags = 0, comments = 0, resolvedErrors = 0, openTagsCount = 0, closeTagsCount = 0;
        while (m.find()) {
            tags++;
            //Более удобное обращение к последнему открытому тегу
            if (openedTags.size() > 0)
                lastOpened = openedTags.get(openedTags.size() - 1);

            //Выбор название тега
            ssTag = m.group(1);
            ssTagNull = ssTag == null;
            tag = m.group(5);

            //true в том случае, когда попадается комментарии
            if (tag == null && ssTagNull) {
                comments++;
                //Если нет последнего закрытого элемента, то попавшийся текст добавляется в тело элемента
                //Иначе добавляется как текст после элемента
                if (lastClosed == null) {
                    if (lastOpened != null)
                        lastOpened.addText(m.group());
                } else {
                    lastClosed.addAfterText(m.group());
                }
                //Нет смысла продолжать выполнение
                continue;
            }

            //Выбор текста
            afterText = m.group(7);
            text = ssTagNull ? afterText : m.group(3);

            //null в том случае, когда тег открывается
            if (m.group(4) == null | !ssTagNull) {
                openTagsCount++;
                //Группа 3 - аттрибуты тега, добавляются сразу для уменьшения времени парсинга
                newElement = new Element(ssTagNull ? tag : ssTag, m.group(ssTagNull ? 6 : 2));

                //Уровень вложенности элемента. Совпадает с кол-вом открытых тегов
                newElement.setLevel(openedTags.size());

                if (lastOpened != null) {
                    //Устанавливается родитель элемента.
                    //Истинно, когда в одном родителе идёт несколько последовательно вложенных элементов
                    //<div>
                    //  Последовательно выложенные:
                    //  <div></div>
                    //  <div></div>
                    //  <div></div>
                    //</div>
                    newElement.setParent(lastOpened.getLevel() == openedTags.size() ? lastOpened.getParent() : lastOpened);
                }

                doc.add(newElement);

                //Проверка на теги, которые можно не закрывать
                if (!ssTagNull | containsInUTag(newElement.tagName())) {

                    //Добавляем текст внутри script/style
                    if (!ssTagNull) {
                        newElement.addText(text);
                    }
                    //Т.к. у незакрывающегося тега нет тела, то текст добавляется после него
                    newElement.addAfterText(afterText);

                    //Т.к. тег неявно закрывающийся, то последний закрытый тег это он
                    lastClosed = newElement;
                    closeTagsCount++;
                } else {
                    //Есть тело элемента, добавляем текст
                    newElement.addText(text);

                    //Добавляем в список открытых элементов
                    openedTags.add(newElement);

                    //Делаем null, потому-что иначе будет неверно добавляться script/sty
                    lastClosed = null;
                }
            } else {
                if (lastOpened != null) {

                    lastClosed = lastOpened;

                    //На случай, если допущена ошибка и есть лишний закрывающий тег
                    //<span><b></span></b>
                    //<span></b></span>
                    //<span><b></span>
                    if (!lastClosed.tagName().equals(tag)) {
                        boolean resolved = false;

                        //Исправление ошибки с переносом элемента внутрь
                        for (int i = errorTags.size() - 1; i >= 0; i--) {
                            String errorTag = errorTags.get(i);
                            if (errorTag.equals(tag)) {
                                //Если последний в последнем или последний в последнем в последнем тег равен тегу по разметке
                                //На случай когда есть в errorTags, но уже исправлено
                                if (lastClosed.getLast() != null && (lastClosed.getLast().getLast() != null && lastClosed.getLast().getLast().tagName().equals(tag) | lastClosed.getLast().tagName().equals(tag))) {
                                    errorTags.remove(i);
                                    resolved = true;
                                    break;
                                }
                                //Создаем элемент и вставляем в последний, с переносом текста
                                //Так решается ошибка в хроме например
                                Element resolveElement = new Element(errorTag);
                                resolveElement.setLevel(lastClosed.getLevel() + 1);
                                resolveElement.setParent(lastClosed);
                                resolveElement.setText(lastClosed.getText());
                                lastClosed.setText("");
                                doc.add(resolveElement);
                                lastClosed = null;
                                resolved = true;
                                errorTags.remove(i);
                                break;
                            }
                        }
                        //Если ошибка исправлена, то продолжать смысла нет
                        if (resolved) {
                            resolvedErrors++;
                            continue;
                        }

                        //Добавляем в список ошибок
                        errorTags.add(lastClosed.tagName());

                        //Закрываем тег
                        openedTags.remove(openedTags.size() - 1);
                        closeTagsCount++;

                        //Исправление ошибки с правильным выносом элемента
                        if (openedTags.size() > 0) {
                            if (openedTags.get(openedTags.size() - 1).tagName().equals(tag)) {
                                openedTags.remove(openedTags.size() - 1);
                                closeTagsCount++;
                                lastClosed = null;
                                resolvedErrors++;
                                continue;
                            }
                        }
                        continue;
                    }


                    //Добавляем текст после закрывающего тега
                    lastClosed.addAfterText(afterText);

                    //Удаляем/"закрываем" тег
                    openedTags.remove(openedTags.size() - 1);
                    closeTagsCount++;
                }
            }
        }

        Log.d("QualityControl", "Main Info {AllTags: " + tags + "; ErrorTags: " + errorTags.size() + "; ResolvedErrors : " + resolvedErrors + "; UnclosedTags: " + openedTags.size() + "}");
        Log.d("QualityControl", "More Info {Comments: " + comments + "; OpenedTags: " + openTagsCount + "; ClosedTags: " + closeTagsCount + "}");
        for (Element el : openedTags.toArray()) {
            Log.e("QualityControl", "Unclosed Tag: " + el.tagName());
        }
        for (String el : errorTags) {
            Log.e("QualityControl", "Error Tag: " + el);
        }
        return doc;
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
