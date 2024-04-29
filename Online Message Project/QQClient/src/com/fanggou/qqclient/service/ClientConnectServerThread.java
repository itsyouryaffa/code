package com.fanggou.qqclient.service;

import com.fanggou.qqcommon.Message;
import com.fanggou.qqcommon.MessageType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 *
 *
 *                 //没有线程的话，客户端没办法管理socket。因为客户端会有多个线程，管理起来会很麻烦。
 *                 //socket需要不断的从服务端获取信息。线程的run方法里面不停的读和处理
 *                 //一旦成功的话，客户端启动一个线程，让线程持有一个socket，线程帮助我们和服务器端进行通信
 *                 //创建一个和服务器端保持通讯的线程->创建一个线程类 ClientConnectServerThread
 *
 *在UserClientService里首先会new一个socket，然后再new ClientConnectServerThread(socket)，再把他的socket传进去;
 *                 //启动客户端的线程
 *                 clientConnectServerThread.start();
 */

public class ClientConnectServerThread extends Thread{
    //该线程需要持有socket
    private Socket socket;

    //构造器可以接受一个Socket对象

    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }
    //为了更方便的得到Socket


    //因为Tread需要在后台和服务器通信，因此我们用while循环控制
    @Override
    public void run() {
        while(true){
            try {
                System.out.println("客户端线程，等待读取从服务器端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //如果通道里面没有东西从服务器传输过来，会一直阻塞在readObject这里
                Message message = (Message) ois.readObject();

                //注意后面我们需要去使用message

                //判断这个message类型，然后做相应的业务类型
                //如果读取到到是服务端返回到在线用户列表
                if(message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                    //取出在线列表信息，并显示
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("=======当前在线用户列表=======");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户：" + onlineUsers[i]);
                    }
                } else if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    //把从服务器端转发的消息，显示到控制台即可
                    System.out.println("\n" + message.getSender() + "对" + "你说： " + message.getContent());
                } else if(message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    //显示在客户端的控制台
                    System.out.println("\n" + message.getSender() + "对" + "大家说： " + message.getContent());
                } else if(message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    System.out.println("\n" + message.getSender() +"从" + message.getSrc() + "给我发文件到: " +message.getDest());
                    //取出message的文件字节数组，通过文件输出流写出到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n 保存文件成功～");
                } else if(message.getMesType().equals(MessageType.MESSAGE_OFF_MES)){
                    System.out.println(message.getContent());
                }


                else{
                    System.out.println("是其他类型到message，暂时不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
