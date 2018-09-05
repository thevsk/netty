package top.thevsk.msg.tcp;

import top.thevsk.msg.BaseVO;

import java.io.Serializable;

/**
 * @author thevsk
 * @Title: TcpMsgVO
 * @ProjectName police-link-netty
 * @date 2018-08-29 16:09
 */
public class TcpMsgVO extends BaseVO implements Serializable {
    private int length;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public TcpMsgVO() {
        super();
    }
}
