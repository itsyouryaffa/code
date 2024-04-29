package com.fanggou.qqclient.service;

import java.util.HashMap;

/**
 * 该类管理客户端连接到服务器端的线程的类
 */
public class ManageClientConnectServerThread {
    //我们把多个线程放入一个HashMap集合，key是用户id，val是线程
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();
    //将某个线程加入到集合中
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread){
        hm.put(userId, clientConnectServerThread);
    }
    //通过userId可以得到对应的线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId){
        return hm.get(userId);
    }

}

