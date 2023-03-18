package com.example.nettyrpc.entity;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义 ChannelHandler 处理服务端消息
 * NettyClientHandler 用于读取服务端发送过来的 RpcResponse 消息对象，并将 RpcResponse 消息对象
 * 保存到 AttributeMap 上， AttributeMap 可以看作是一个 channel 的共享数据源。
 *
 * @author NKU-DBIS, pichunying
 * @date 2023/3/15
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        try{
            RpcResponse rpcResponse = (RpcResponse) msg;
            logger.info("client receive msg: []{}", rpcResponse.toString());
            // 声明一个 AttributeKey 对象
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            // 将服务端的返回结果保存到 AttributeMap 上，AttributeMap 可以看作是一个channel的共享数据源，类似于 Map 数据结构
            // AttributeMap的key是AttributeKey，value是Attribute
            // Channel 实现了 AttributeMap 接口，这样也就表明它存在了 AttributeMap 相关的属性。每个 Channel 上的 AttributeMap 属于共享数据。
            ctx.channel().attr(key).set(rpcResponse);
            ctx.channel().close();
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("client caught exception", cause);
        ctx.close();
    }
}
