package top.thevsk.entity.enums;

/**
 * @author thevsk
 * @Title: MsgType
 * @ProjectName police-link-netty
 * @date 2018-08-28 15:46
 */
public enum MsgType {
    PING(0),
    LOGIN(1),
    CONTENT(2),
    ERROR(3);

    private int code;

    MsgType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MsgType valueOfCode(int code) {
        for (MsgType msgType : MsgType.values()) {
            if (msgType.code == code) return msgType;
        }
        throw new RuntimeException("unknown code");
    }
}