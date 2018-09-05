package top.thevsk.msg;

import com.alibaba.fastjson.JSONObject;
import top.thevsk.entity.enums.MsgType;

import java.io.Serializable;

/**
 * @author thevsk
 * @Title: BaseVO
 * @ProjectName police-link-netty
 * @date 2018-09-04 11:55
 */
public class BaseVO implements Serializable {
    private MsgType type;
    private String uuid;
    private JSONObject data;

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public BaseVO() {
    }

    public BaseVO(MsgType type, String uuid, JSONObject data) {
        this.type = type;
        this.uuid = uuid;
        this.data = data;
    }
}
