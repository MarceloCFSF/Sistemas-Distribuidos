package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
  public static void main(String[] args) throws Exception {
    DatagramSocket clientSocket = new DatagramSocket();

    InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

    byte[] sendData = new byte[1024];
    sendData = "sou o cliente".getBytes();

    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

    clientSocket.send(sendPacket);

    byte[] recBuffer = new byte[1024];
    DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);

    clientSocket.receive(recPacket);

    String information = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());

    System.out.println(information);

    clientSocket.close();
  }
}
