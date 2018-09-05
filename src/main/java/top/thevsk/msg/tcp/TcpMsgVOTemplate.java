package top.thevsk.msg.tcp;

import com.alibaba.fastjson.JSONObject;
import top.thevsk.msg.BaseVO;
import top.thevsk.msg.BaseVOTemplate;

/**
 * @author thevsk
 * @Title: TcpMsgVOTemplate
 * @ProjectName police-link-netty
 * @date 2018-08-30 16:13
 */
public class TcpMsgVOTemplate {

    public static TcpMsgVO offLine(String ip) {
        return toTcpMsgVo(BaseVOTemplate.offLine(ip));
    }

    public static TcpMsgVO onLine(String uuid) {
        return toTcpMsgVo(BaseVOTemplate.onLine(uuid));
    }

    public static TcpMsgVO noLogin(String uuid) {
        return toTcpMsgVo(BaseVOTemplate.noLogin(uuid));
    }

    public static TcpMsgVO paramError(String uuid) {
        return toTcpMsgVo(BaseVOTemplate.paramError(uuid));
    }

    public static TcpMsgVO ping(String uuid) {
        return toTcpMsgVo(BaseVOTemplate.ping(uuid));
    }

    public static TcpMsgVO content(JSONObject jsonObject) {
        return toTcpMsgVo(BaseVOTemplate.content(jsonObject));
    }

    public static TcpMsgVO success(String uuid) {
        return toTcpMsgVo(BaseVOTemplate.success(uuid));
    }

    public static TcpMsgVO toTcpMsgVo(BaseVO baseVO) {
        return new TcpMsgVO() {{
            setType(baseVO.getType());
            setUuid(baseVO.getUuid());
            setData(baseVO.getData());
        }};
    }
}