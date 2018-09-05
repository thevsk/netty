package top.thevsk.utils;

/**
 * @author thevsk
 * @Title: PathKit
 * @ProjectName police-link-netty
 * @date 2018-09-05 14:06
 */
public class PathKit {

    public static String separator = "/";

    public static boolean isJar() {
        return PathKit.class.getProtectionDomain().getCodeSource().getLocation().getFile().contains(".jar");
    }

    public static String getStartPath() {
        String path = PathKit.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        if (PathKit.isJar()) {
            path = path.substring(0, path.lastIndexOf(separator));
        } else {
            path = path.substring(0, path.length() - 1);
            path = path.substring(0, path.lastIndexOf(separator));
        }
        return path;
    }
}
