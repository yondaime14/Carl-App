package com.carllewis14.recyclerview.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Validate Empty text fields here
 */

public class Validation {

    public static boolean validateFields(String name) {
        return !TextUtils.isEmpty(name);
    }


    public static boolean validateEmail(String string) {

        return !(TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches());
    }
}
