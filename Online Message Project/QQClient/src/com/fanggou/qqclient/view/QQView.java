package com.fanggou.qqclient.view;

import com.fanggou.qqclient.service.FileClientService;
import com.fanggou.qqclient.service.MessageClientService;
import com.fanggou.qqclient.service.UserClientService;
import com.fanggou.qqclient.utils.Utility;

/**
 * 客户端的菜单界面
 */
public class QQView {
    private boolean loop = true; //控制是否显示菜单
    private String key = ""; // 接收用户的键盘输入
    private UserClientService userClientService = new UserClientService();//对象是用于登录服务器/注册用户
    private MessageClientService messageClientService = new MessageClientService();
    private FileClientService fileClientService = new FileClientService();//该对象用于传送文件 之后调用其中的方法

    public static void main(String[] args) {
        QQView qqView = new QQView();
        qqView.mainMenu();
    }

    //显示主菜单
    private void mainMenu(){
        while(loop){
            System.out.println("===============欢迎登陆网络通信系统==============");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择：");
            key = Utility.readString(1);
            //根据用户的输入，来处理不同的逻辑
            switch (key){
                case "1":
                    System.out.print("请输入用户号：");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密  码：");
                    String pwd = Utility.readString(50);
                    //这里比较麻烦，需要做一个user用户发送到服务器查找是否有这个用户



                    //这里有很多代码，我们这里编写一个类UserClientService【用户登录/注册】
                    if(userClientService.checkUser(userId, pwd)){//先写逻辑
                        System.out.println("============欢迎（用户 " + userId + " 登录成功）============");
                        //进入到二级菜单
                        while(loop){
                            System.out.println("\n========网络通信系统二级菜单（用户 " + userId + " ）==========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 5 查看离线信息");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("请输入你的选择：");
                            key = Utility.readString(1);
                            switch (key){
                                case "1":
                                    //这里准备写一个方法来获取在线用户列表
                                    userClientService.onlineFriendList();

                                    break;
                                case "2":
                                    System.out.println("请输入想对大家谁的话：");
                                    String s = Utility.readString(100);
                                    //调用一个方法，将消息封装成message对象
                                    messageClientService.sendMessageToAll(s, userId);
                                    break;
                                case "3":
                                    System.out.print("请输入想聊天的用户号：");
                                    String getterId = Utility.readString(50);
                                    System.out.println("请输入想说的话：");
                                    String content = Utility.readString(100);
                                    //编写一个方法，发送给服务端
                                    messageClientService.sendMessageToOne(content, userId, getterId);
                                    break;
                                case "4":
                                    System.out.println("请输入你想把文件发送给的用户（在线用户）：");
                                    getterId = Utility.readString(50);
                                    System.out.println("请输入发送文件的路径：");
                                    String src = Utility.readString(50);
                                    System.out.println("请输入把文件发送到对应的路径：");
                                    String dest = Utility.readString(50);
                                    fileClientService.sendFileToOne(src,dest,userId,getterId);
                                    break;
                                case "5":
                                    messageClientService.getOffLineMes(userId);
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "9":
                                    //调用方法，给服务器发送一个退出系统的message
                                    userClientService.logout();
                                    loop = false;
                                    break;

                            }
                        }
                    } else{
                        System.out.println("==========登录失败==========");
                    }
                    break;
                case "9":
                    loop = false;
                    break;

            }

        }
    }
}
