package thread;

public class App {
    public static void main(String[] args) throws Exception {
        ThreadDemo td1 = new ThreadDemo("1");
        ThreadDemo td2 = new ThreadDemo("2");

        td1.start();
        td2.start();
    }
}
