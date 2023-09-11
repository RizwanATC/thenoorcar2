package com.noor.thenoorcar.Function;

import android.content.Context;
import android.graphics.Typeface;

import com.noor.thenoorcar.R;

public class FontHelper {
    public static Typeface getCustomTypeface(Context context) {
        // Load the custom font file from the resources
        return Typeface.createFromAsset(context.getAssets(), String.valueOf(R.font.avenirblack));
    }
}

