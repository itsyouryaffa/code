package com.fanggou.qqserver.service;

import com.fanggou.qqcommon.Message;
import com.fanggou.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * 该类的一个对象和某个客户端保持通信
 */
public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userId;//连接到服务端的用户id
    private ManageClientThreads manageClientThreads = new ManageClientThreads();
    private Vector<String> offlineMesList = new Vector<>();
    private String offLineMes = "";

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {//这里线程处于run状态，可以发送和接受消息
        //不停的run肯定是while循环
        while(true){
            try {
                System.out.println("服务端和客户端" + userId + "保持通信。。。");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();
                //后面会使用Message,根据message的类型做相应的业务处理
                if(message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //客户端要在线客户列表
                    System.out.println(message.getSender() + " 要在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    //构建一个message对象，返回给客户端
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //返回给客户端
                    //获得关联到输出流
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                } else if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    //普通的聊天，只需要转发给指定的客户端
                    //在和发送端的线程里面根据getterid拿到了 在服务器端里的 和接受者通讯的线程
                    //如果用户在线
                    if(manageClientThreads.getHm().containsKey(message.getGetter())) {
                        ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                        //在线程里拿到socket，再拿到输出流
                        ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        oos.writeObject(message);//转发；如果客户不在线，可以保存在数据库，这样就可以实现离线留言
                    } else{//如果用户不在线
                        if(!manageClientThreads.getOffLineMessage().containsKey(message.getGetter())){
                            manageClientThreads.getOffLineMessage().put(message.getGetter(), offlineMesList);
                            manageClientThreads.getOffLineMessage().get(message.getGetter()).add(message.getSender() +"在" + message.getSendTime() +  "对你说" + message.getContent());
                        } else{
                            manageClientThreads.getOffLineMessage().get(message.getGetter()).add(message.getSender() +"在" + message.getSendTime() +  "对你说" + message.getContent());
                        }
                    }

                } else if(message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)){//客户端要退出
                    System.out.println(message.getSender() + "要退出系统了");
                    //将客户端对应的线程从集合中删除
                    ManageClientThreads.removeServerConnectClientThreads(message.getSender());
                    //关闭socket,关闭连接
                    socket.close();
                    //退出while循环，run方法结束，也就是退出线程
                    break;
                } else if(message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    //需要遍历管理线程的集合，得到hashmap里面除了它自己之外的线程
                    //再把所有线程的socket得到，然后把message通过服务器的socket转发给所有的socket
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()) {
                        //取出在线用户的id
                        String onLineUserId = iterator.next();
                        if(!onLineUserId.equals(message.getSender())){//排除群发消息给本人
                            //进行转发message
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onLineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);


                        }
                    }

                } else if(message.getMesType().equals(MessageType.MESSAGE_OFF_MES)){

                    //登录成功后，查找是否有离线消息
                    if(manageClientThreads.getOffLineMessage().containsKey(message.getSender())){
                        Vector<String> mesArr = manageClientThreads.getOffLineMessage().get(message.getSender());
                        Iterator<String> iterator = mesArr.iterator();
                        while (iterator.hasNext()) {
                            offLineMes += iterator.next() + "\n";
                        }
                        message.setContent(offLineMes);
                    } else{
                        message.setContent("您没有离线消息");
                    }
                    //根据id获取到对应的线程，将message对象转发
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);

                    manageClientThreads.getOffLineMessage().remove(message.getSender());

                }

                else{
                    System.out.println("其他类型消息，先暂时不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
