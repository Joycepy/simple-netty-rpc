package com.example.nettyrpc;

import com.example.nettyrpc.entity.NettyClient;
import com.example.nettyrpc.entity.NettyServer;
import com.example.nettyrpc.entity.RpcRequest;
import com.example.nettyrpc.entity.RpcResponse;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyRpcApplication {

    public static void main(String[] args) {
        // 客户端
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("interface")
                .methodName("hello").build();
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8889);
        for (int i = 0; i < 10; i++) {
            nettyClient.sendMessage(rpcRequest);
        }
        RpcResponse rpcResponse = nettyClient.sendMessage(rpcRequest);
        System.out.println(rpcResponse.toString());

        // 服务器端
//        new NettyServer(8889).run();

//        SpringApplication.run(NettyRpcApplication.class, args);
    }

}
