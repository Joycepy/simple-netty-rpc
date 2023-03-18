package com.example.nettyrpc.entity;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 自定义解码器，负责处理"入站“消息，它会从ByteBuf中读取到业务对象的字节序列，转换为我们的业务对象
 * @author NKU-DBIS, pichunying
 * @date 2023/3/15
 */
@Slf4j
@AllArgsConstructor
public class NettyKryoDecoder extends ByteToMessageDecoder {
    private final Serializer serializer;
    private final Class<?> genericClass;
    private static final int BODY_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        //1.byteBuf中写入的消息长度所占的字节数已经是4了，所以 byteBuf 的可读字节必须大于 4,
        if (in.readableBytes() >= BODY_LENGTH) {
            //2.标记当前readIndex的位置，以便后面重置readIndex 的时候使
            in.markReaderIndex();
            //3.读取消息的长度，注意: 消息长度是encode的时候我们自己写入的，参见 NettyKryoEncoder 的encode方法
            int dataLength = in.readInt();
            //4.遇到不合理的情况直接 return
            if (dataLength < 0 || in.readableBytes() < 0) {
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            //5.如果可读字节数小于消息长度的话，说明是不完整的消息，重置readIndex
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            // 6.走到这里说明没什么问题了，可以序列化了
            byte[] body = new byte[dataLength];
            in.readBytes(body);
            // 将bytes数组转换为我们需要的对象
            Object obj = serializer.deserialize(body, genericClass);
            out.add(obj);
            log.info("successful decode ByteBuf to Object");
        }
    }
}
