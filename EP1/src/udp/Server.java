package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
  public static void main(String[] args) throws Exception {
    DatagramSocket serverSocket = new DatagramSocket(9876);

    while(true) {
      byte[] recBuffer = new byte[1024];
      DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
      serverSocket.receive(recPacket);

      
      byte[] sendData = new byte[1024];
      sendData = "sou o servidor".getBytes();
      
      InetAddress IPAddress = recPacket.getAddress();
      int port = recPacket.getPort();
      
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
      
      serverSocket.send(sendPacket);

      System.out.println("mensagem enviada");
    }
  }
}
