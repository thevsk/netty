package top.thevsk.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author thevsk
 * @Title: LogKit
 * @ProjectName police-link-netty
 * @date 2018-08-29 14:09
 */
public class LogKit {

    public static ArrayList<LogLevel> logLevel = null;

    public static ArrayList<LogType> logType = null;

    private static String filePath = null;

    static {
        logLevel = new ArrayList<LogLevel>() {{
            add(LogLevel.INFO);
            add(LogLevel.DEBUG);
            add(LogLevel.ERROR);
        }};
        logType = new ArrayList<LogType>() {{
            add(LogType.CONSOLE);
            add(LogType.FILE);
        }};
        filePath = PathKit.getStartPath() + PathKit.separator + "log" + PathKit.separator;
        checkAndCreateDir();
    }

    public enum LogType {
        CONSOLE, FILE
    }

    public enum LogLevel {
        INFO, DEBUG, ERROR
    }

    public static void info(String s) {
        if (LogKit.logLevel.contains(LogLevel.INFO)) {
            out("[INFO] - [" + TimeKit.getCurrentTime() + "] " + s);
        }
    }

    public static void debug(String s) {
        if (LogKit.logLevel.contains(LogLevel.DEBUG)) {
            out("[DEBUG] - [" + TimeKit.getCurrentTime() + "] " + s);
        }
    }

    public static void error(String s) {
        if (LogKit.logLevel.contains(LogLevel.ERROR)) {
            out("[ERROR] - [" + TimeKit.getCurrentTime() + "] " + s);
        }
    }

    public static void error(String s, Throwable e) {
        error(s);
        e.printStackTrace();
    }

    public static void info(String s, Object... os) {
        String[] strS = s.split("\\?");
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < os.length; i++) {
            res.append(strS[i]);
            res.append(os[i].toString());
        }
        res.append(strS[strS.length - 1]);
        info(res.toString());
    }

    private static void out(String s) {
        if (logType.contains(LogType.CONSOLE)) {
            System.out.println(s);
        }
        if (logType.contains(LogType.FILE)) {
            outFile(s);
        }
    }

    private static void outFile(String s) {
        if (filePath == null) {
            return;
        }
        FileKit.append(getFile(checkLogFile()), s);
    }

    private static HashMap<String, Object> staticFiles = new HashMap<>();

    private static File getFile(String path) {
        if (path == null) return null;
        if (staticFiles.get(path) == null) {
            File file = new File(path);
            if (file.exists()) {
                staticFiles.put(path, file);
                return file;
            } else {
                staticFiles.put(path, "NULL");
                return null;
            }
        } else {
            Object file = staticFiles.get(path);
            if (file instanceof File) {
                return (File) file;
            }
            return null;
        }
    }

    private static String checkLogFile() {
        if (filePath == null) {
            return null;
        }
        checkAndCreateDir();
        String fileName = "LOG" + TimeKit.getCurrentDate() + ".log";
        String _filePath = filePath + fileName;
        File file = new File(_filePath);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                LogKit.error("日志文件创建失败，文件名" + fileName, e);
                return null;
            }
        }
        return _filePath;
    }

    private static void checkAndCreateDir() {
        File _path = new File(filePath);
        if (!_path.exists()) {
            if (!_path.mkdir()) {
                filePath = null;
                logType.remove(LogType.FILE);
                LogKit.error("日志文件夹创建失败");
            }
        }
    }
}
