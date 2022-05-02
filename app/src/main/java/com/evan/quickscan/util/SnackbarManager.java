package com.evan.quickscan.util;

import android.content.Context;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.evan.quickscan.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackbarManager {
    public static Snackbar createIndefinite(Context context, ViewGroup parentLayout, String msg) {
        return Snackbar.make(parentLayout, msg, Snackbar.LENGTH_INDEFINITE)
                .setAction("Dismiss", v -> {
                })
                .setActionTextColor(
                        ContextCompat.getColor(context, R.color.gradientStartColor200)
                );
    }

    public static Snackbar createExpirable(ViewGroup parentLayout, String msg) {
        return Snackbar.make(parentLayout, msg, Snackbar.LENGTH_LONG);
    }
}
