package com.evan.quickscan.util;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

public class KeyboardUtils {
    public static void hideKeyboard(TextInputEditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
