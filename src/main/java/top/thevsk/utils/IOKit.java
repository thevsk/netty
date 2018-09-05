package top.thevsk.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author thevsk
 * @Title: IOKit
 * @ProjectName police-link-netty
 * @date 2018-09-05 14:48
 */
public class IOKit {

    public static void close(Object o) {
        if (o != null) {
            if (o instanceof Closeable) {
                try {
                    ((Closeable) o).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
