package houserent.view;

import houserent.domain.House;
import houserent.service.HouseService;
import houserent.utils.Utility;

import javax.rmi.CORBA.Util;
import java.util.Scanner;

/**
 * 显示界面
 * 接收用户的输入
 * 调用house service完成对房屋信息的操作
 */
public class Houseview {
    private boolean loop = true;//控制菜单的
    private char key = ' '; // 接收用户输入的，key和scanner next 绑定，在switch(key),case可以调用不同的栏目
    private HouseService houseService = new HouseService(10); //刚才的hosue数组的容量。

    //根据id修改房屋信息
    public void update() {
        System.out.println("-------------------修改房屋信息-------------------");
        System.out.print("请选择待修改房屋编号(-1表示退出)：");
        int updateId = Utility.readInt();
        if (updateId == -1) {
            System.out.println("放弃修改。");
            return;
        }
        House house = houseService.findById(updateId);//houseService里的方法，找到到一个house对象，然后接收到。在下面修改
        if (house == null) {
            System.out.println("修改的房屋信息编号" + updateId + "不存在，无法修改。");
        }
        System.out.print("姓名(" + house.getName() + "):");
        String name = Utility.readString(9, "");//默认值不是一个输出值，默认空接下来判断不修改，不是默认原名字
        if (!"".equals(name)) {//如果name不是空的话
            house.setName(name);//用setname修改
        }
        System.out.print("电话(" + house.getTel() + ")：");
        int tel = Utility.readInt(-1);
        if (tel != -1) {
            house.setTel(tel);
        }
        System.out.print("地址(" + house.getAdd() + ")：");
        String add = Utility.readString(10, "");
        if (!"".equals(add)) {
            house.setAdd(add);
        }
        System.out.print("租金(" + house.getRent() + ")：");
        int rent = Utility.readInt(-1);
        if (rent != -1) {
            house.setRent(rent);
        }
        System.out.println("状态(" + house.getState() + ")：");
        String state = Utility.readString(3, "");
        if (!"".equals(state)) {
            house.setState(state);
        }
        System.out.println("-------------------修改房屋信息成功-------------------");


    }


    //根据id查找房屋信息
    public void findHouse() {
        System.out.println("-------------------查找房屋信息-------------------");
        System.out.print("请输入要查找的id：");
        int findId = Utility.readInt();
        //调用方法,用house接收
        House house = houseService.findById(findId);
        if (house != null) {
            System.out.println(house);
        } else {
            System.out.println("-------------------查找的房屋不存在------------------");
        }
    }

    //退出确认
    public void exit() {
        char c = Utility.readConfirmSelection();
        if (c == 'Y') {
            loop = false;
        }
    }

    //编写delHouse() 接收输入的id，调用Service的del方法
    public void delHouse() {
        System.out.println("-------------------删除房屋-------------------");
        System.out.print("请选择待删除的房屋编号(-1表示退出删除)：");
        int delId = Utility.readInt(2);
        if (delId == -1) {
            System.out.println("（放弃删除房屋信息）");
            return;//return表示结束一个方法
        }
        char choice = Utility.readConfirmSelection();//该方法本身就有循环判断的逻辑，必须输入y或者n
        if (choice == 'Y') {
            if (houseService.del(delId)) {
                System.out.println("----------------删除房屋信息成功-----------------");
            } else {
                System.out.println("-----------房屋编号不存在，删除房屋信息失败--------------");
            }
        } else {
            System.out.println("（放弃删除房屋信息）");
        }

    }


    //编写addHouse() "接收！！输入"，创建house对象，调用add方法
    public void addHouse() {
        System.out.println("-------------------添加房屋-------------------");
        System.out.print("姓名：");
        String name = Utility.readString(8);//这个工具就是相当于scanner接受了信息
        System.out.print("电话：");
        int tel = Utility.readInt(12);
        System.out.print("地址：");
        String add = Utility.readString(12);
        System.out.print("月租：");
        int rent = Utility.readInt(6);
        System.out.print("状态：");
        String state = Utility.readString(3);
        //创建一个新的house对象，注意id是系统分配的 House
        House newHouse = new House(0, name, tel, add, rent, state);//上面接收的信息
        //调用add(newHouse) 1是判断是否还能添加 2能添加之后 把让newhouse传入之前的数组 3分配一个id重新setid
        if (houseService.add(newHouse)) {
            System.out.println("----------------------添加房屋成功-----------------------");
        } else {
            System.out.println("----------------------添加房屋失败-----------------------");
        }
    }

    public void listHouse() { //跟之后的switch绑定的，在这里写中文。真正的list方法在houseservice
        System.out.println("-------------------房屋列表-------------------");
        System.out.println("编号\t\t房主\t\t电话\t\t地址\t\t月租\t\t状态(未出租/已出租)");
        House[] houseList = houseService.list();//得到所有的房屋信息。因为刚才的list return的是house数组。前面分配了数组空间
        for (int i = 0; i < houseList.length; i++) {
            if (houseList[i] == null) {
                break;
            }
            System.out.println(houseList[i]);//之前已经用tostring写完了输出的
        }
        System.out.println("-----------------房屋列表显示完毕-----------------");

    }


    //显示主菜单，houseview类里的一个方法，do while循环
    public void mainMenu() {
        do {
            System.out.println("================房屋出租系统菜单=================");
            System.out.println("\t\t\t1 新 增 房 源");
            System.out.println("\t\t\t2 查 找 房 源");
            System.out.println("\t\t\t3 删 除 房 屋 信 息");
            System.out.println("\t\t\t4 修 改 房 屋 信 息");
            System.out.println("\t\t\t5 房 屋 列 表");
            System.out.println("\t\t\t6 退      出");
            System.out.print("请输入你的选择(1-6)：");
            key = Utility.readChar();
            switch (key) {
                case '1':
                    addHouse();
                    break;
                case '2':
                    findHouse();
                    break;
                case '3':
                    delHouse();
                    break;
                case '4':
                    update();
                    break;
                case '5':
                    listHouse();
                    break;
                case '6':
                    exit();
                    break;
            }
        } while (loop);
    }
}
