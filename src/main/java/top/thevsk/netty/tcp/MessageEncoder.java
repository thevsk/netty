package top.thevsk.netty.tcp;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.thevsk.msg.tcp.TcpMsgVO;
import top.thevsk.utils.LogKit;
import top.thevsk.utils.StrKit;

/**
 * @author thevsk
 * @Title: MessageEncoder
 * @ProjectName police-link-netty
 * @date 2018-08-29 17:20
 */
public class MessageEncoder extends MessageToByteEncoder<TcpMsgVO> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, TcpMsgVO msg, ByteBuf byteBuf) throws Exception {
        JSONObject jsonObject = new JSONObject(true) {
            {
                put("type", msg.getType().getCode());
                put("uuid", StrKit.isBlank(msg.getUuid()) ? StrKit.getRandomUUID() : msg.getUuid());
                if (msg.getData() != null) {
                    put("data", msg.getData());
                }
            }
        };
        byte[] bodyByte = jsonObject.toJSONString().getBytes("utf-8");
        byteBuf.writeInt(bodyByte.length);
        byteBuf.writeBytes(bodyByte);
        LogKit.info("+SEND TCP MSG:----------------------------------------");
        LogKit.info("|length: ? ", bodyByte.length);
        LogKit.info("|type  : ? ", msg.getType().name());
        LogKit.info("|uuid  : ? ", jsonObject.getString("uuid"));
        LogKit.info("|data  : ? ", msg.getData() == null ? "空" : msg.getData().toJSONString());
        LogKit.info("+-----------------------------------------------------");
    }
}
