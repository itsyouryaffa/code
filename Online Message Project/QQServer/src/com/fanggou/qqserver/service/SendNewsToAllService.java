package com.fanggou.qqserver.service;

import com.fanggou.qqcommon.Message;
import com.fanggou.qqcommon.MessageType;
import com.fanggou.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 做了一个线程
 */
public class SendNewsToAllService implements Runnable{

    @Override
    public void run() {
        while (true) {
            //如果没有加while的话，这个线程发了一次就退出了

            System.out.println("请输入服务器要推送的消息[输入exit表示退出推送服务]：");
            String news = Utility.readString(1000);

            if("exit".equals(news)){
                break;
            }
            //构建一个消息
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setSendTime(new Date().toString());
            System.out.println("服务器推送消息给所有人 说： " + news);

            //遍历所有的通讯线程，得到所有的socket并发送message
            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {
                String onLineUserId = iterator.next();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(hm.get(onLineUserId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
