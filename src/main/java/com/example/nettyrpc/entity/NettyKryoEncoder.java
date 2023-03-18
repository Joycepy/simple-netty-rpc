package com.example.nettyrpc.entity;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义编码器，负责处理“出站”信息，将消息格式转换为字节数组然后写入到字节数据的容器ByteBuf中
 *
 * @author NKU-DBIS, pichunying
 * @date 2023/3/15
 */

@Slf4j
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    private final Serializer serializer;
    private final Class<?> genericClass;
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf){
        if(genericClass.isInstance(o)){
            // 1.将对象转换为byte
            byte[] body = serializer.serialize(o);
            // 2.读取消息的长度
            int dataLength = body.length;
            // 3.写入消息对应的字节数组长度,writerIndex 加 4
            byteBuf.writeInt(dataLength);
            //4.将字节数组写入 ByteBuf 对象中
            byteBuf.writeBytes(body);
        }
    }
}
