package com.fanggou.qqclient.service;

import com.fanggou.qqcommon.Message;
import com.fanggou.qqcommon.MessageType;
import com.fanggou.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成用户登录验证和用户注册等功能
 */
public class UserClientService {
    //因为我们可能在其他地方会使用user信息，因此作出成员属性
    private User u = new User();
    //因为Socket在其他地方也可能使用，因此作为属性
    private Socket socket;
    //根据userId和pwd搭配服务器去验证该用户是否合法

    // 如果进入到if代码，说明消息传送成功，b最后就return的true
    public boolean checkUser(String userId, String pwd){
        boolean b = false;
        //创建一个user对象
        u.setUserId(userId);
        u.setPassword(pwd);
        //连接到服务端发送u对象 网络编程学的东西

        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            //得到ObjectOutputStream对象 字节输出流 对象流
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//发送user对象

            //服务器验证

            //读取从服务器回复的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();

            //如果登录成功的话

            if(ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){

                //没有线程的话，客户端没办法管理socket。因为客户端会有多个线程，管理起来会很麻烦。
                //socket需要不断的从服务端获取信息。线程的run方法里面不停的读和处理
                //一旦成功的话，客户端启动一个线程，让线程持有一个socket，线程帮助我们和服务器端进行通信
                //创建一个和服务器端保持通讯的线程->创建一个线程类 ClientConnectServerThread

                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端的线程
                clientConnectServerThread.start();

                //这里为了后面客户端的扩展，我们将线程放入到集合管理
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                b = true;

            } else {
                //如果登录失败，我们就不能启动和服务器通信的线程，关闭socket
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

    //向服务器端请求在线用户列表
    public void onlineFriendList(){
        //发送一个Message 4
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        //发送给服务器
        //从管理线程的集合里面得到userid对应的线程，从线程里面得到socket，再从socket得到输出流
        //因为客户端可能维护多个socket。现在只有一个socket就可以直接socket.getOutputStream
        try {
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);//发送一个Message对象，向服务端要求在线用户列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //编写方法，退出客户端，并给服务端发送一个退出系统的message对象
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指定我是哪个客户端
        //发送message
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId() + " 退出系统 ");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
