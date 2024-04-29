package houserent.service;

import houserent.domain.House;

public class HouseService {
    private House[] houses;//创建一个数组来保存House对象
    private int houseNums = 0;//记录当前有多少个房屋信息
    private int idCounter = 0;//记录id增长


    public HouseService(int size){//构造器用来为house分配大小
        houses = new House[size];
    }
    //list方法，因为要返回数组，形式就写的是house[].
    public House[] list(){
        return houses;
    }
    //添加add方法，添加新对象，返回boolean？？看不懂
    public boolean add(House newHouse){
        //判断是否还可以继续添加
        if(houseNums == houses.length){
            System.out.println("数组已满，不能再添加了");
            return false;
        }
        //把newHouse对象加入到数组
        houses[houseNums++] = newHouse;//先赋值再自增
        newHouse.setId(++idCounter); //更新newhouse的id，先自增再赋值
        return true;
    }

    public boolean del(int delId){
        //找到删除的是哪个， 不能单纯的拿delid-1得到下标，因为万一删了几个的话，就会不准
        //遍历找id
        int index = -1;
        for (int i = 0; i < houseNums; i++) {//不写house.length的原因是house最开始分配了10个空间，后面的是置空的。
            if(delId == houses[i].getId()){
                index = i;//记录id
            }
        }

        if(index == -1){ //别人乱输的防错机制
            return false;
        }

        for (int i = index; i < houseNums - 1 ; i++) {//houseNums - 1是数组中最大的那个了，小于的话遍历到前一个
            houses[i] = houses[i+1];
        }
        houses[--houseNums] = null;//先减减再赋值
        return true;
    }

    public House findById(int findId){
        for (int i = 0; i < houseNums; i++) {
            if(findId == houses[i].getId()){
                return houses[i];
            }
        }
        return null;
    }
}
