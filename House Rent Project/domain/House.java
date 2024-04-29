package houserent.domain;

/**
 * house的对象表示一个房屋信息
 */
public class House {
    private int id;
    private String name;
    private int tel;
    private String add;
    private int rent;
    private String state;

    public House(int id, String name, int tel, String add, int rent, String state) {
        this.id = id;
        this.name = name;
        this.tel = tel;
        this.add = add;
        this.rent = rent;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public int getRent() {
        return rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return  id +
                "\t\t" + name +
                "\t" + tel +
                "\t\t" + add +
                "\t" + rent +
                "\t" + state;
    }
}
