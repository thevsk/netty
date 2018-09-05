package top.thevsk.netty;

import top.thevsk.entity.Session;
import top.thevsk.start.Start;
import top.thevsk.utils.LogKit;

import java.util.HashMap;

/**
 * @author thevsk
 * @Title: PingCheckHandler
 * @ProjectName police-link-netty
 * @date 2018-09-04 16:32
 */
public class PingCheckHandler {

    private static int timeOut = 1000;

    private static int sleep = 1000;

    private static int times = 0;

    private static HashMap<String, Long> tcpUser = new HashMap<>();

    private static HashMap<String, Long> webSocketUser = new HashMap<>();

    private static PingCheckHandler pingCheckHandler;

    private PingCheckHandler() {
        PingCheckHandler.timeOut = PingCheckHandler.timeOut * Start.getConfig().getInt("ping.timeout");
        PingCheckHandler.sleep = PingCheckHandler.sleep * Start.getConfig().getInt("ping.sleep");
    }

    public static PingCheckHandler getInstance() {
        if (pingCheckHandler == null) {
            pingCheckHandler = new PingCheckHandler();
        }
        return pingCheckHandler;
    }

    public void ping(String type, String userId) {
        if ("tcp".equals(type)) {
            PingCheckHandler.tcpUser.put(userId, System.currentTimeMillis());
        } else if ("webSocket".equals(type)) {
            PingCheckHandler.webSocketUser.put(userId, System.currentTimeMillis());
        }
    }

    public void start() {
        LogKit.info("启动心跳检测");
        LogKit.info("心跳检测超时时长：? 秒", Start.getConfig().getInt("ping.timeout"));
        LogKit.info("心跳检测时间间隔：? 秒", Start.getConfig().getInt("ping.sleep"));
        new Thread(() -> PingCheckHandler.getInstance().check()).start();
    }

    private void sync() {
        for (String userId : Session.getWebSocketMap().keySet()) {
            if (PingCheckHandler.webSocketUser.get(userId) == null) {
                PingCheckHandler.webSocketUser.put(userId, System.currentTimeMillis());
            }
        }
        for (String userId : Session.getTcpMap().keySet()) {
            if (PingCheckHandler.tcpUser.get(userId) == null) {
                PingCheckHandler.tcpUser.put(userId, System.currentTimeMillis());
            }
        }
        for (String userId : PingCheckHandler.webSocketUser.keySet()) {
            if (Session.getWebSocketMap().get(userId) == null) {
                PingCheckHandler.webSocketUser.remove(userId);
            }
        }
        for (String userId : PingCheckHandler.tcpUser.keySet()) {
            if (Session.getTcpMap().get(userId) == null) {
                PingCheckHandler.tcpUser.remove(userId);
            }
        }
    }

    private void check() {
        // LogKit.info("心跳检测次数：? ", PingCheckHandler.times);
        PingCheckHandler.times += 1;
        sync();
        for (String userId : PingCheckHandler.webSocketUser.keySet()) {
            Long time = PingCheckHandler.webSocketUser.get(userId);
            if (System.currentTimeMillis() - time > PingCheckHandler.timeOut) {
                timeOut("webSocket", userId);
            }
        }
        for (String userId : PingCheckHandler.tcpUser.keySet()) {
            Long time = PingCheckHandler.tcpUser.get(userId);
            if (System.currentTimeMillis() - time > PingCheckHandler.timeOut) {
                timeOut("tcp", userId);
            }
        }
        try {
            Thread.sleep(PingCheckHandler.sleep);
            check();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void timeOut(String type, String userId) {
        if ("webSocket".equals(type)) {
            LogKit.info("webSocket 用户 ? 由于心跳包超时而被服务器断开连接", userId);
            Session.webSocketRemove(Session.webSocketGetChannel(userId));
        } else if ("tcp".equals(type)) {
            LogKit.info("TCP 用户 ? 由于心跳包超时而被服务器断开连接", userId);
            Session.tcpRemove(Session.tcpGetChannel(userId));
        }
    }
}
