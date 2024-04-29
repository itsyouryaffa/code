package com.fanggou.qqserver.service;

import com.fanggou.qqcommon.Message;
import com.fanggou.qqcommon.MessageType;
import com.fanggou.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这是服务器，在监听9999，等待客户端的连接，并保持通信
 */

public class qqServer {
    private ServerSocket ss = null;

    //创建一个集合，存放多个用户，如果是这些用户登录，就认为是合法的
    //这里我们也可以使用ConcurrentHashMap,可以处理并发的集合，没有线程安全问题，是线程同步的
    //HashMap没有处理线程安全问题
    private  static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    //在静态代码块初始化合法用户，在类加载的时候就会初始化
    static {
        validUsers.put("100", new User("100","123456"));
        validUsers.put("200", new User("200","123456"));
        validUsers.put("300", new User("300","123456"));
        validUsers.put("至尊宝", new User("至尊宝","123456"));
        validUsers.put("紫霞仙子", new User("紫霞仙子","123456"));
        validUsers.put("菩提老祖", new User("菩提老祖","123456"));
    }

    //验证用户是否有效的方法

    private boolean checkUser(String userId, String passwd){
        User user = validUsers.get(userId);
        if(user == null){//说明userId不存在
            return false;
        }
        if(!user.getPassword().equals(passwd)){//取反 过关斩将验证方式
            return false;
        }
        return true;
    }

    public qqServer(){
        //注意：端口可以写在配置文件
        try {
            System.out.println("服务器在9999端口监听。。。");
            //启动推送新闻的线程
            new Thread(new SendNewsToAllService()).start();

            ss =  new ServerSocket(9999);

            while (true){//当和某个客户端连接后，会继续监听，因此while

                Socket socket = ss.accept();
                //如果有一个客户端连接到了才往下执行，否则是阻塞在这。

                //得到socket关联的对象输入流
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                //得到socket对象关联的对象输出流，发送message回去
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                //第一次发过来的是user对象,像下转型，读取客户端发送的User对象
                User u = (User) objectInputStream.readObject();

                //创建一个message对象准备回复客户端
                Message messageA = new Message();
                //因为不管成功还是失败都要回复一个message，所以不能放在if里面，这样else就用不了


                //验证
                if(checkUser(u.getUserId(), u.getPassword())){//合法
                    //成功的话是给客户端发送一个message告诉它已经登录成功了
                    messageA.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //将message对象回复客户端
                    objectOutputStream.writeObject(messageA);
                    //创建一个线程，和客户端保持通信，改线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());
                    //启动该线程
                    serverConnectClientThread.start();
                    //把该线程对象，放入到一个集合中，进行管理
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);

                }else {//登录失败
                    System.out.println("用户" + u.getUserId() + "pwd =" +u.getPassword() + "验证失败");
                    messageA.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    objectOutputStream.writeObject(messageA);
                    socket.close();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            //如果服务端退出来while循环，说明服务器不再监听，因此需要关闭ServerSocket
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
