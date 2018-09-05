package top.thevsk.start;

import top.thevsk.netty.PingCheckHandler;
import top.thevsk.netty.tcp.NettyTcpServerBootstrap;
import top.thevsk.netty.websocket.NettyWebSocketBootstrap;
import top.thevsk.utils.LogKit;
import top.thevsk.utils.PathKit;
import top.thevsk.utils.Prop;
import top.thevsk.utils.PropKit;

import java.io.File;

public class Start {

    private static Prop config;

    public static void main(String[] args) {
        try {
            if (PathKit.isJar()) {
                String filePath = PathKit.getStartPath();
                filePath += PathKit.separator + "resources" + PathKit.separator + "config.properties";
                config = PropKit.use(new File(filePath));
                LogKit.info("is jar start");
            } else {
                config = PropKit.use("config.properties");
                LogKit.info("is idea start");
            }
            new NettyTcpServerBootstrap(config.getInt("port.tcp"));
            new NettyWebSocketBootstrap(config.getInt("port.webSocket"));
            PingCheckHandler.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Prop getConfig() {
        return config;
    }
}