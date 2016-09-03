package com.example.radiationx.tryparse.htmltags;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by radiationx on 03.09.16.
 */
public class H2Tag extends BaseTag {
    @Override
    protected float size() {
        return super.size() * 1.5f;
    }

    public H2Tag(Context context) {
        super(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, px4, 0, px3);

        setLayoutParams(params);
    }
}
