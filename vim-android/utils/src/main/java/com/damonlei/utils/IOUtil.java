package com.damonlei.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * @author damonlei
 * @time 2017/3/3
 * @email danxionglei@foxmail.com
 */
public class IOUtil {
    private static final String TAG = "IOUtil";

    public static BufferedReader bufferReader(InputStream is) {
        try {
            return new BufferedReader(new InputStreamReader(is, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(String.format("bufferReader ERROR [%s]", e));
        }
    }

    public static BufferedWriter bufferedWriter(OutputStream os) {
        try {
            return new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(String.format("bufferedWriter ERROR [%s]",
                    e));
        }
    }

    public static void closeSilently(InputStream is) {
        try {
            is.close();
        } catch (Exception ignored) {
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static void closeSilently(OutputStream os) {
        try {
            os.close();
        } catch (Exception ignored) {
        } finally {
            try {
                os.close();
            } catch (Exception ignored) {
            }
        }
    }


    public static void closeSilently(Reader reader) {
        try {
            reader.close();
        } catch (Exception ignored) {
        } finally {
            try {
                reader.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception ignored) {
        } finally {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

}
