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
            mainPattern = Pattern.compile("(?:<(?:(?:!(?!DOCTYPE)|(?:script|style)[^>]*>)[\\s\\S]*?(?:--|\\/script|\\/style)|([\\/])?([\\w]*)(?: ([^>]*))?\\/?)>)(?:([^<]+))?(?:<\\/(?:script|--|style)>[^<]*)?");
        ElementHelper.init();
    }

    public static Document parse(String html) {
        Document doc = new Document();
        ElementsList openedTags = new ElementsList();
        ArrayList<Pair<String, String>> errorTags = new ArrayList<>();
        Element lastOpened = null, lastClosed = null, newElement = null;
        String tag = null, text = null;
        Matcher m = mainPattern.matcher(html);
        int tags = 0, otherText = 0, errorTagsCount = 0, openTagsCount = 0, closeTagsCount = 0;
        while (m.find()) {
            tags++;
            //Более удобное обращение к последнему открытому тегу
            if (openedTags.size() > 0)
                lastOpened = openedTags.get(openedTags.size() - 1);

            //Выбор название тега
            tag = m.group(2);

            //null в том случае, когда попадается script/style/comment
            if (tag == null) {
                otherText++;
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
            text = m.group(4);

            //null в том случае, когда тег открывается
            if (m.group(1) == null) {
                openTagsCount++;
                //Группа 3 - аттрибуты тега, добавляются сразу для уменьшения времени парсинга
                newElement = new Element(tag, m.group(3));

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
                if (containsInUTag(newElement.tagName())) {
                    //Т.к. у незакрывающегося тега нет тела, то текст добавляется после него
                    newElement.addAfterText(text);

                    //Т.к. тег неявно закрывающийся, то последний закрытый тег это он
                    lastClosed = newElement;
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
                    closeTagsCount++;
                    lastClosed = lastOpened;

                    //На случай, если допущена ошибка и есть лишний закрывающий тег
                    Log.d("SUKA", "CLOSE " + lastClosed.tagName() + " : " + tag);
                    if (!lastClosed.tagName().equals(tag)) {
                        Log.e("SUKA", "ERROR ^^^^^^^^^^^^^^^^^^^^");
                        boolean contains = false;
                        for (Element element : openedTags.toArray()) {
                            if (element.tagName().equals(tag)) {
                                errorTags.add(new Pair<>(lastClosed.tagName(), tag));
                                contains = true;
                                break;
                            }
                        }
                        errorTagsCount++;
                        continue;
                    }


                    //Добавляем текст после закрывающего тега
                    lastClosed.addAfterText(text);

                    //Удаляем/"закрываем" тег
                    openedTags.remove(openedTags.size() - 1);
                    boolean contains = false;
                    for (Pair<String, String> errorTag : errorTags) {
                        if (errorTag.first.equals(tag)) {
                            Log.e("SUKA", "RESOLVE " + errorTag.first + " : " + errorTag.second + " --------- " + (openedTags.size() > 0 ? openedTags.get(openedTags.size() - 1).tagName() : "NO REMOVE"));
                            if (openedTags.size() > 0) {
                                /*if(!openedTags.get(openedTags.size()-1).tagName().equals(errorTag.second)){
                                    continue;
                                }*/
                                openedTags.remove(openedTags.size() - 1);
                            }
                            errorTags.remove(errorTag);
                            contains = true;
                            break;
                        }
                    }
                }
            }
        }

        Log.d("QualityControl", "Main Info {AllTags: " + tags + "; ErrorTags: " + errorTags.size() + "; UnclosedTags: " + openedTags.size() + "}");
        Log.d("QualityControl", "More Info {OtherText: " + otherText + "; OpenedTags: " + openTagsCount + "; ClosedTags: " + closeTagsCount + "}");
        for (Element el : openedTags.toArray()) {
            Log.e("QualityControl", "Unclosed Tag: " + el.tagName());
        }
        for (Pair<String, String> el : errorTags) {
            Log.e("QualityControl", "Unclosed Tag: " + el.first + ":" + el.second);
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
