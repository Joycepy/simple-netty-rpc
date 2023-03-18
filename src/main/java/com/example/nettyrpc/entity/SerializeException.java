package com.example.nettyrpc.entity;

/**
 * 一句话概括这个文件/类的功能，如有必要，可以详细说明
 *
 * @author NKU-DBIS, pichunying
 * @date 2023/3/15
 */
public class SerializeException extends RuntimeException{
    public SerializeException(String msg){
        super(msg);
    }
}
