package top.thevsk.netty.websocket;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import top.thevsk.entity.Session;
import top.thevsk.entity.enums.MsgType;
import top.thevsk.msg.BaseVO;
import top.thevsk.msg.BaseVOTemplate;
import top.thevsk.msg.CodeConst;
import top.thevsk.msg.websocket.Sender;
import top.thevsk.netty.PingCheckHandler;
import top.thevsk.start.Start;
import top.thevsk.utils.LogKit;
import top.thevsk.utils.StrKit;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

/**
 * @author thevsk
 * @Title: NettyServerHandler
 * @ProjectName police-link-netty
 * @date 2018-08-30 18:10
 */
public class NettyServerHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        if (o instanceof FullHttpRequest) {
            handleHttpRequest(ctx, ((FullHttpRequest) o));
        } else if (o instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) o);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogKit.info("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session.webSocketRemove(ctx.channel());
        LogKit.info("channelInactive");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Session.webSocketRemove(ctx.channel());
        LogKit.error(cause.getMessage(), cause);
        ctx.close();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if (!req.decoderResult().isSuccess() || !("websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        String wsUrl = "ws://127.0.0.1:" + Start.getConfig().getInt("port.webSocket") + "/";
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(wsUrl, null, false);
        WebSocketServerHandshaker handshake = wsFactory.newHandshaker(req);
        if (null == handshake) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshake.handshake(ctx.channel(), req);
        }
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (!(frame instanceof TextWebSocketFrame)) {
            Sender.writeAndFlush(ctx.channel(), BaseVOTemplate.paramError(StrKit.getRandomUUID()));
            return;
        }
        BaseVO msg;
        try {
            String text = ((TextWebSocketFrame) frame).text();
            msg = JSON.toJavaObject(JSON.parseObject(text), BaseVO.class);
            LogKit.info("+GET WebSocket MSG:-----------------------------------");
            LogKit.info("|type  : ? ", msg.getType().name());
            LogKit.info("|uuid  : ? ", msg.getUuid());
            LogKit.info("|data  : ? ", msg.getData() == null ? "空" : msg.getData().toJSONString());
            LogKit.info("+-----------------------------------------------------");
        } catch (Exception e) {
            Sender.writeAndFlush(ctx.channel(), BaseVOTemplate.paramError(StrKit.getRandomUUID()));
            return;
        }
        // 请求登录
        if (msg.getType() == MsgType.LOGIN) {
            String userId = msg.getData().getString("userId");
            //缺少参数
            if (StrKit.isBlank(userId)) {
                // [SEND] 参数错误
                Sender.writeAndFlush(ctx.channel(), BaseVOTemplate.paramError(msg.getUuid()));
            } else {
                Channel channel = Session.webSocketGetChannel(userId);
                // 已经登录了
                if (channel != null) {
                    // [SEND] 你被踢了
                    Sender.writeAndFlush(ctx.channel(), BaseVOTemplate.offLine("127.0.0.1"));
                    // 踢了前一个
                    Session.webSocketRemove(channel);
                }
                // 新的放进去
                Session.webSocketAdd(userId, ctx.channel());
                // [SEND] 登录成功
                Sender.writeAndFlush(ctx.channel(), BaseVOTemplate.onLine(msg.getUuid()));
            }
        } else { // 请求其他
            String userId = Session.webSocketGetUserId(ctx.channel());
            // 没登录
            if (userId == null) {
                // [SEND] 你没登录
                Sender.writeAndFlush(ctx.channel(), BaseVOTemplate.noLogin(msg.getUuid()));
            } else { // 登录了
                // 是心跳包
                if (msg.getType() == MsgType.PING) {
                    // 心跳
                    PingCheckHandler.getInstance().ping("webSocket", userId);
                    // [SEND] 心跳包
                    Sender.writeAndFlush(ctx.channel(), BaseVOTemplate.ping(msg.getUuid()));
                    return;
                }
                // 是信息
                if (msg.getType() == MsgType.CONTENT) {
                    //TODO: 信息
                    if (CodeConst.SEND_ALL_WEB_SOCKET_CLIENT.equals(msg.getData().getString("state"))) {
                        // [SEND] 给所有的 webSocket 客户端发送信息
                        Session.sendAllWebSocketChannel(msg.getData());
                        // [SEND] 返回成功
                        Sender.writeAndFlush(ctx.channel(), BaseVOTemplate.success(msg.getUuid()));
                    } else if (CodeConst.SEND_ALL_TCP_CLIENT.equals(msg.getData().getString("state"))) {
                        // [SEND] 给所有的 tcp 客户端发送信息
                        Session.sendAllTcpChannel(msg.getData());
                        // [SEND] 返回成功
                        Sender.writeAndFlush(ctx.channel(), BaseVOTemplate.success(msg.getUuid()));
                    }
                }
            }
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture future = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
