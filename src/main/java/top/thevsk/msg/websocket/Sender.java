package top.thevsk.msg.websocket;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import top.thevsk.msg.BaseVO;
import top.thevsk.utils.LogKit;
import top.thevsk.utils.StrKit;

/**
 * @author thevsk
 * @Title: Sender
 * @ProjectName police-link-netty
 * @date 2018-09-04 12:34
 */
public class Sender {

    public static void writeAndFlush(Channel channel, BaseVO baseVO) {
        JSONObject jsonObject = new JSONObject() {{
            put("type", baseVO.getType().getCode());
            put("uuid", StrKit.isBlank(baseVO.getUuid()) ? StrKit.getRandomUUID() : baseVO.getUuid());
            if (baseVO.getData() != null) {
                put("data", baseVO.getData());
            }
        }};
        channel.writeAndFlush(new TextWebSocketFrame(jsonObject.toJSONString()));
        LogKit.info("+SEND WebSocket MSG:----------------------------------");
        LogKit.info("|type  : ? ", baseVO.getType().name());
        LogKit.info("|uuid  : ? ", jsonObject.getString("uuid"));
        LogKit.info("|data  : ? ", baseVO.getData() == null ? "空" : baseVO.getData().toJSONString());
        LogKit.info("+-----------------------------------------------------");
    }
}
