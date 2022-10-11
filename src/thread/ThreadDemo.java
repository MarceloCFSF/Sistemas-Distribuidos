package thread;
/**
 * ThreadDemo
 */
public class ThreadDemo extends Thread {
  public String threadName;
  public ThreadDemo(String nome) {
    threadName = nome;
  }
  
  public void run() {
    for (int i = 4; i >= 0; i--) {
      System.out.println("T: " + threadName + " " + i);
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
      }
    }
  }
}