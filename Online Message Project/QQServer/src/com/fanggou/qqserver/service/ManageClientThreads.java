package com.fanggou.qqserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * 该类用于管理和客户端通信的线程
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm= new HashMap<>();
    private static HashMap<String, Vector<String>> offLineMessage = new HashMap<>();

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    public static HashMap<String, Vector<String>> getOffLineMessage(){
        return offLineMessage;
    }


    //添加线程对象到hm集合
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread){
        hm.put(userId, serverConnectClientThread);
    }

    //从集合中删除掉某个线程对象
    public static void removeServerConnectClientThreads(String userId){
        hm.remove(userId);
    }

    //根据userId返回一个线程
    public static ServerConnectClientThread getServerConnectClientThread(String userId){
        return hm.get(userId);
    }

    //这里编写方法可以返回在线用户列表
    public static String getOnlineUser(){
        //集合遍历，遍历hashmap的key
        Iterator<String> iterator = hm.keySet().iterator();
        String onlineUserList = "";
        while (iterator.hasNext()) {
            onlineUserList +=  iterator.next() + " ";
        }
        return onlineUserList;
    }
}
