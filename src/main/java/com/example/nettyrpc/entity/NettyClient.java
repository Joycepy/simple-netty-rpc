package com.example.nettyrpc.entity;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端
 *
 * @author NKU-DBIS, pichunying
 * @date 2023/3/14
 */
public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final String host;
    private final int port;
    /**
     * Bootstrap类是Netty提供的一个工厂类，作用：Netty组件的组装，Netty程序的初始化。
     */
    private static final Bootstrap b;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 初始化相关资源比如EventLoopGroup, Bootstrap
    static {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 1. 首先初始化了一个Bootstrap
     * 2. 通过 Bootstrap对象连接服务端
     * 3. 通过 Channel 向服务端发送消息 RpcRequest
     * 4. 发送成功后，阻塞等待，直到 channel 关闭
     * 5. 拿到服务端返回的结果 RpcResponse
     */
    public RpcResponse sendMessage(RpcRequest rpcRequest){
        try{
            ChannelFuture f = b.connect(host, port).sync();
            logger.info("client connect {}", host + ":"+port);
            Channel futureChannel = f.channel();
            logger.info("send message");
            if(futureChannel != null){
                futureChannel.writeAndFlush(rpcRequest).addListener(future -> {
                    if(future.isSuccess()){
                        logger.info("client send message: [{}]", rpcRequest.toString());
                    }else{
                        logger.info("Send failed: ", future.cause());
                    }
                });
                // 阻塞等待，直到Channel关闭
                futureChannel.closeFuture().sync();
                // 将服务端返回的数据也就是RpcResponse对象取出
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return futureChannel.attr(key).get();
            }
        } catch (InterruptedException e) {
            logger.error("occur exception when connect server: ", e);
        }
        return null;
    }
}
