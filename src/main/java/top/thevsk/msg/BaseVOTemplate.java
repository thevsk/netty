package top.thevsk.msg;

import com.alibaba.fastjson.JSONObject;
import top.thevsk.entity.enums.MsgType;

/**
 * @author thevsk
 * @Title: BaseVOTemplate
 * @ProjectName police-link-netty
 * @date 2018-09-04 11:56
 */
public class BaseVOTemplate {

    public static BaseVO offLine(String ip) {
        return new BaseVO() {{
            setType(MsgType.LOGIN);
            setData(new JSONObject() {{
                put("state", CodeConst.LOGIN_STATE_OFFLINE);
                put("msg", "您在其他设备登录，您已经被踢下线，登录ip:" + ip);
            }});
        }};
    }

    public static BaseVO onLine(String uuid) {
        return new BaseVO() {{
            setUuid(uuid);
            setType(MsgType.LOGIN);
            setData(new JSONObject() {{
                put("state", CodeConst.LOGIN_STATE_ONLINE);
                put("msg", "登录成功");
            }});
        }};
    }

    public static BaseVO noLogin(String uuid) {
        return new BaseVO() {{
            setUuid(uuid);
            setType(MsgType.ERROR);
            setData(new JSONObject() {{
                put("state", CodeConst.LOGIN_STATE_NO_LOGIN);
                put("msg", "您未登录");
            }});
        }};
    }

    public static BaseVO paramError(String uuid) {
        return new BaseVO() {{
            setUuid(uuid);
            setType(MsgType.ERROR);
            setData(new JSONObject() {{
                put("state", CodeConst.ERROR_PARAM);
                put("msg", "参数缺失");
            }});
        }};
    }

    public static BaseVO ping(String uuid) {
        return new BaseVO() {{
            setUuid(uuid);
            setType(MsgType.PING);
            setData(new JSONObject() {{
                put("time", System.currentTimeMillis());
            }});
        }};
    }

    public static BaseVO content(JSONObject jsonObject) {
        return new BaseVO() {{
            setType(MsgType.CONTENT);
            setData(jsonObject);
        }};
    }

    public static BaseVO success(String uuid) {
        return new BaseVO() {{
            setUuid(uuid);
            setType(MsgType.CONTENT);
            setData(new JSONObject(){{
                put("state", CodeConst.SUCCESS);
            }});
        }};
    }
}
