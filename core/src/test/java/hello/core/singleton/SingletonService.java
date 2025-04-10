package hello.core.singleton;

public class SingletonService {

    // 자기 자신의 객체 하나 생성(static 하게)
    private static SingletonService instance = new SingletonService();

    //객체를 반환해주는 메서드를 작성
    public static SingletonService getInstance() {
        return instance;
    }

    //생성자를 막음
    private SingletonService() {

    }


    //로직부분
    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");

    }




}
