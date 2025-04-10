package hello.core.singleton;

public class StatefulService {
    private int price;

    public void order(String name , int price) {
        System.out.println("name = " + name + ", price = " + price);
        this.price = price; // 서비스 내에서 공유필드를 변경하는 영역 == 문제점
    }


    public int getPrice() {
        return price;
    }
}
