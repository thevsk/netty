package top.thevsk.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * @author thevsk
 * @Title: FileKit
 * @ProjectName police-link-netty
 * @date 2018-09-05 14:48
 */
public class FileKit {

    public static void append(File file, String s) {
        if (file != null && file.exists() && !file.isDirectory()) {
            FileWriter fw = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(file, true);
                pw = new PrintWriter(fw);
                pw.println(s);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOKit.close(pw);
                IOKit.close(fw);
            }
        }
    }
}
