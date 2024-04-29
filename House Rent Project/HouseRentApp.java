package houserent;

import houserent.view.Houseview;

//创建houseview对象，是整个程序的入口
public class HouseRentApp {
    public static void main(String[] args) {
        new Houseview().mainMenu();//因为是个循环，所以用匿名对象也可以 只要你不退出 它就一直跑。
        System.out.println("你退出了房屋出租系统。");
    }
}

