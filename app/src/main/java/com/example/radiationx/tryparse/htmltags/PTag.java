package com.example.radiationx.tryparse.htmltags;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by radiationx on 15.09.16.
 */
public class PTag extends BaseTag {
    public PTag(Context context) {
        super(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, px2, 0, px2);

        setLayoutParams(params);
    }
}
