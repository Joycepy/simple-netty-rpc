package com.example.nettyrpc.entity;

import lombok.*;

/**
 * 服务端响应实体类
 *
 * @author NKU-DBIS, pichunying
 * @date 2023/3/14
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class RpcResponse {
    private String message;
}
