package com.regan.saata.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * User: regan
 * Date: 2020-02-28
 * Time: 12:08
 */

public class ExceptionsUtils {
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
