package com.fanggou.qqclient.service;

import com.fanggou.qqcommon.Message;
import com.fanggou.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * 该对象，提供和消息相关的服务方法
 */

public class MessageClientService {
    /**
     *
     * @param content 内容
     * @param senderId 发送者
     */
    public void sendMessageToAll(String content, String senderId){
        //构建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(new Date().toString());//发送时间社知道message对象
        System.out.println("我" + senderId + "对大家说： " + content);



        //发送给服务端，要先拿到自己对socket
        //
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    /**
     *
     * @param content 发送的内容
     * @param senderId 发送用户的id
     * @param getterId 接收用户id
     */

    public void sendMessageToOne(String content, String senderId, String getterId){
        //构建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new Date().toString());//发送时间社知道message对象
        System.out.println("我对" + getterId + "说： " + content);

        //发送给服务端，要先拿到自己对socket
        //
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getOffLineMes(String senderId){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_OFF_MES);
        message.setSender(senderId);
        System.out.println(senderId + "获取离线信息");
        //
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
