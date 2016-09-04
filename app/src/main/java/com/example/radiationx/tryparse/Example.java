package com.example.radiationx.tryparse;

import android.util.Log;

/**
 * Created by radiationx on 27.08.16.
 */
public class Example {

    String html =
            "<div class=\"message\">\n" +
                    "\t<h1 class=\"title\">Attention!</h1>\n" +
                    "\t<div class=\"content\">This site is under construction.\n" +
                    "\t\t<p>Real content coming soon.</p>\n" +
                    "\t\tcontent after\n" +
                    "\t\t<p>Real content coming soon.</p>\n" +
                    "\t</div>\n" +
                    "</div>";

    private void func2() {
        Document document = Document.parse(html);
        Log.d("kek", "recurse\n" + document.getAllText());

        //Log.d("kek", "recurse\n" + document.getRoot().get(1).get(1).getAllText());
    }
}
