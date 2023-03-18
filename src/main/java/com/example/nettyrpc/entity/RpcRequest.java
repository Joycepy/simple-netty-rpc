package com.example.nettyrpc.entity;


import lombok.*;

/**
 * 客户端请求实体类
 *
 * @author NKU-DBIS, pichunying
 * @date 2023/3/14
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class RpcRequest {
    private String interfaceName;
    private String methodName;
}

