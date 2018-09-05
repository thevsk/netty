package top.thevsk.netty.tcp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import top.thevsk.entity.enums.MsgType;
import top.thevsk.msg.tcp.TcpMsgVO;
import top.thevsk.utils.LogKit;

import java.util.List;

/**
 * @author thevsk
 * @Title: MessageDecoder
 * @ProjectName police-link-netty
 * @date 2018-08-29 17:20
 */
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readInt();
        byte[] text = new byte[length];
        byteBuf.readBytes(text);
        JSONObject jsonObject = JSON.parseObject(new String(text, "utf-8"));
        TcpMsgVO requestVO = new TcpMsgVO() {
            {
                setLength(length);
                setType(MsgType.valueOfCode(jsonObject.getInteger("type")));
                setUuid(jsonObject.getString("uuid"));
                setData(jsonObject.getJSONObject("data"));
            }
        };
        list.add(requestVO);
        LogKit.info("+GET TCP MSG:-----------------------------------------");
        LogKit.info("|length: ? ", requestVO.getLength());
        LogKit.info("|type  : ? ", requestVO.getType().name());
        LogKit.info("|uuid  : ? ", requestVO.getUuid());
        LogKit.info("|data  : ? ", requestVO.getData() == null ? "空" : requestVO.getData().toJSONString());
        LogKit.info("+-----------------------------------------------------");
    }
}
