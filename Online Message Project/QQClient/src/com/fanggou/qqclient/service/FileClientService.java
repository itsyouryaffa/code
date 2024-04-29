package com.fanggou.qqclient.service;

import com.fanggou.qqcommon.Message;
import com.fanggou.qqcommon.MessageType;

import java.io.*;

/**
 * 该类完成文件的传输服务
 *
 */
public class FileClientService {
    /**
     *
     * @param src 源文件路径
     * @param dest 把该文件发送到对方到哪个路径
     * @param senderId 发送用户id
     * @param getterId 接收用户id
     */
    public void sendFileToOne(String src, String dest, String senderId, String getterId){
        //读取src文件 --> message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setDest(dest);
        message.setSrc(src);

        //将文件从磁盘中读取
        //要把文件读入到客户端的字节数组里去，要读磁盘上的文件就需要new文件输入流
        FileInputStream fileInputStream = null;
        //fileBytes就是从磁盘读入到内存的字节数组
        byte[] fileBytes = new byte[(int)new File(src).length()];

        //拿到输入流
        try {
            //将src读入到到字符输入流，再读到filebytes里面
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);
            //将文件对应的字节数组社知道message
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //提示信息
        System.out.println("\n" + "你给" + senderId + "发送了文件：" + src + " 到对方电脑的" + dest);

        //发送
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
