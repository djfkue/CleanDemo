package com.argonmobile.cleandemo.util;

import java.text.DecimalFormat;

/**
 * Created by sean on 4/13/15.
 */
public class MemorySizeFormatter {
    public static String formatMemorySize(long size) {
        if(size <= 0) return "0B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
