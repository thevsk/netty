package top.thevsk.netty.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.thevsk.entity.Session;
import top.thevsk.entity.enums.MsgType;
import top.thevsk.msg.CodeConst;
import top.thevsk.msg.tcp.TcpMsgVO;
import top.thevsk.msg.tcp.TcpMsgVOTemplate;
import top.thevsk.netty.PingCheckHandler;
import top.thevsk.utils.LogKit;
import top.thevsk.utils.StrKit;

import java.net.InetSocketAddress;

/**
 * @author thevsk
 * @Title: NettyServerHandler
 * @ProjectName police-link-netty
 * @date 2018-08-29 17:20
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<TcpMsgVO> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TcpMsgVO msg) throws Exception {
        // 请求登录
        if (msg.getType() == MsgType.LOGIN) {
            String userId = msg.getData().getString("userId");
            //缺少参数
            if (StrKit.isBlank(userId)) {
                // [SEND] 参数错误
                ctx.channel().writeAndFlush(TcpMsgVOTemplate.paramError(msg.getUuid()));
            } else {
                Channel channel = Session.tcpGetChannel(userId);
                // 已经登录了
                if (channel != null) {
                    // 获得客户端ip
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
                    String clientIP = inetSocketAddress.getAddress().getHostAddress();
                    // [SEND] 你被踢了
                    channel.writeAndFlush(TcpMsgVOTemplate.offLine(clientIP));
                    // 踢了前一个
                    Session.tcpRemove(channel);
                }
                // 新的放进去
                Session.tcpAdd(userId, ctx.channel());
                // [SEND] 登录成功
                ctx.channel().writeAndFlush(TcpMsgVOTemplate.onLine(msg.getUuid()));
            }
        } else { // 请求其他
            String userId = Session.tcpGetUserId(ctx.channel());
            // 没登录
            if (userId == null) {
                // [SEND] 你没登录
                ctx.channel().writeAndFlush(TcpMsgVOTemplate.noLogin(msg.getUuid()));
            } else { // 登录了
                // 是心跳包
                if (msg.getType() == MsgType.PING) {
                    // 心跳
                    PingCheckHandler.getInstance().ping("tcp", userId);
                    // [SEND] 心跳包
                    ctx.channel().writeAndFlush(TcpMsgVOTemplate.ping(msg.getUuid()));
                    return;
                }
                // 是信息
                if (msg.getType() == MsgType.CONTENT) {
                    //TODO: 信息
                    if (CodeConst.SEND_ALL_WEB_SOCKET_CLIENT.equals(msg.getData().getString("state"))) {
                        // [SEND] 给所有的 webSocket 客户端发送信息
                        Session.sendAllWebSocketChannel(msg.getData());
                        // [SEND] 返回成功
                        ctx.channel().writeAndFlush(TcpMsgVOTemplate.success(msg.getUuid()));
                    } else if (CodeConst.SEND_ALL_TCP_CLIENT.equals(msg.getData().getString("state"))) {
                        // [SEND] 给所有的 tcp 客户端发送信息
                        Session.sendAllTcpChannel(msg.getData());
                        // [SEND] 返回成功
                        ctx.channel().writeAndFlush(TcpMsgVOTemplate.success(msg.getUuid()));
                    }
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 链接异常：下线用户
        Session.tcpRemove(ctx.channel());
        LogKit.error(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 链接被关闭：下线用户
        Session.tcpRemove(ctx.channel());
        LogKit.info("channelInactive");
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // 链接被关闭：下线用户
        Session.tcpRemove(ctx.channel());
        LogKit.info("channelUnregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogKit.info("channelActive");
    }
}
