package top.thevsk.entity;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import top.thevsk.msg.BaseVOTemplate;
import top.thevsk.msg.tcp.TcpMsgVOTemplate;
import top.thevsk.msg.websocket.Sender;
import top.thevsk.utils.LogKit;

import java.util.HashMap;

/**
 * @author thevsk
 * @Title: Session
 * @ProjectName police-link-netty
 * @date 2018-08-30 16:04
 */
public class Session {

    private static HashMap<String, Channel> tcpMap = new HashMap<>();

    public static Channel tcpGetChannel(String userId) {
        return tcpMap.get(userId);
    }

    public static void tcpAdd(String key, Channel channel) {
        LogKit.info("TCP 用户 ? 上线", key);
        tcpMap.put(key, channel);
    }

    public static String tcpGetUserId(Channel channel) {
        for (String userId : tcpMap.keySet()) {
            if (channel.equals(tcpMap.get(userId))) {
                return userId;
            }
        }
        return null;
    }

    public static void tcpRemove(Channel channel) {
        String userId = tcpGetUserId(channel);
        if (userId != null) {
            LogKit.info("TCP 用户 ? 离线", userId);
            tcpMap.remove(userId);
        }
    }

    private static HashMap<String, Channel> webSocketMap = new HashMap<>();

    public static Channel webSocketGetChannel(String userId) {
        return webSocketMap.get(userId);
    }

    public static void webSocketAdd(String key, Channel channel) {
        LogKit.info("WebSocket 用户 ? 上线", key);
        webSocketMap.put(key, channel);
    }

    public static String webSocketGetUserId(Channel channel) {
        for (String userId : webSocketMap.keySet()) {
            if (channel.equals(webSocketMap.get(userId))) {
                return userId;
            }
        }
        return null;
    }

    public static void webSocketRemove(Channel channel) {
        String userId = webSocketGetUserId(channel);
        if (userId != null) {
            LogKit.info("WebSocket 用户 ? 离线", userId);
            webSocketMap.remove(userId);
        }
    }

    public static void sendAllTcpChannel(JSONObject jsonObject) {
        for (Channel channel : tcpMap.values()) {
            channel.writeAndFlush(TcpMsgVOTemplate.content(jsonObject));
        }
    }

    public static void sendAllWebSocketChannel(JSONObject jsonObject) {
        for (Channel channel : webSocketMap.values()) {
            Sender.writeAndFlush(channel, BaseVOTemplate.content(jsonObject));
        }
    }

    public static HashMap<String, Channel> getTcpMap() {
        return tcpMap;
    }

    public static HashMap<String, Channel> getWebSocketMap() {
        return webSocketMap;
    }
}
