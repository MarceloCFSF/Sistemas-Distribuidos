package peers;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.gson.Gson;

import peers.Mensagem.MensagemType;

/**
 * Classe que define um Peer
 */
public class Peer {
  public boolean init;
  public String IP;
  public int port;
  private String path;
  private Neighbor neigh1;
  private Neighbor neigh2;
  private Set<String> files = new HashSet<String>();
  public static final int NEIGH_NUMBER = 2;
  private List<String> recMsgs = new ArrayList<String>();

  /**
   * Thread responsável por receber e responder requisições
   * enviadas por outros peers
   */
  private class ListenThread extends Thread {
    public void run() {
      try {
        DatagramSocket socket = new DatagramSocket(port);

        while (true) {
          byte[] recBuffer = new byte[1024];
          DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
          socket.receive(recPacket);

          
          String info = new String(recPacket.getData(), recPacket.getOffset(), recPacket.getLength());
          Gson gson = new Gson();
          Mensagem msg = gson.fromJson(info, Mensagem.class);
          if (!recMsgs.contains(msg.uuuid)) {
            recMsgs.add(msg.uuuid);
            System.out.println("Mensagem recebida de "+msg.IP+":"+msg.port);
            if (msg.type == MensagemType.SEARCH) {
              search(msg);
            } else if (msg.type == MensagemType.ANSWER) {
              System.out.println("Arquivo encontrado em "+msg.IP+":"+msg.port);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Carrega o nome de todos os arquivos na pasta
   * do peer e o salva em uma lista.
   */
  private void fetchFiles() {
    files = Stream.of(new File(path).listFiles())
          .filter(file -> !file.isDirectory())
          .map(File::getName)
          .collect(Collectors.toSet());
  }

  /**
   * Thread responsável pelo monitoramento dos arquivos 
   * contidos na pasta. Uma lista de arquivos é atualizada
   * de 30 em 30 segundos com os arquivos e é imprimido na 
   * tela o conteúdo dessa lista.
   */
  private class ListFilesThread extends Thread {
    public static final int delay = 30000;
    public void run() {
      while (true) {
        try {
          Thread.sleep(delay);
          
          fetchFiles();
          
          System.out.print("\nSou peer "+IP+":"+port+" com arquivos ");
          for (String file : files) {
            System.out.print(file+" ");
          }
          System.out.println();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Thread responsável por realizar a busca
   * 
   * @param msg Mensagem com os dados da busca
   */
  private class SearchThread extends Thread {
    public Mensagem msg;

    public SearchThread(Mensagem msg) {
      this.msg = msg;
    }

    public void run() {
      try {
        Random generator = new Random();
        int randomIndex = generator.nextInt(NEIGH_NUMBER);
        Neighbor[] neighs = {neigh1, neigh2};
        Neighbor neigh = neighs[randomIndex];
  
        send(msg, neigh.IP, neigh.port);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Classe que define o vizinho do Peer
   */
  private class Neighbor {
    String IP;
    int port;
    
    /**
     * Define o Vizinho
     * @param ipAndPort IP e porta do vizinho
     */
    public Neighbor(String ipAndPort) {
      String[] parts = ipAndPort.split(":");
      IP = parts[0];
      port = Integer.parseInt(parts[1]);
    }
  }

  /**
   * Inicia a busca de um arquivo começando pelo próprio
   * peer e perguntando para seus vizinhos caso não seja encontrado 
   * 
   * @param file Nome do arquivo a ser pesquisado
   */
  private void search(String file) {
    if (files.contains(file)) {
      System.out.println("Arquivo encontrado na pasta");
      return;
    }

    Mensagem mensagem = new Mensagem(IP, port, file, MensagemType.SEARCH);
    SearchThread sThread = new SearchThread(mensagem);
    sThread.start();
  }

  /**
   * Continua uma busca através de uma mensagem do tipo SEARCH, buscando
   * no peer atual e perguntando para um de seus vizinhos caso não tenha
   * o arquivo.
   * 
   * @param msg
   */
  private void search(Mensagem msg)  {
    if (files.contains(msg.file)) {
      Mensagem answer = new Mensagem(IP, port, msg.file, MensagemType.ANSWER);
      try {
        send(answer, msg.IP, msg.port);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }

    SearchThread sThread = new SearchThread(msg);
    sThread.start();
  }

  /**
   * Função que envia uma mensagem para um ip e porta definidos
   * @param msg Mensagem a ser enviada
   * @param ip IP o qual a mensagem vai ser enviada
   * @param port Porta a qual a mensagem vai ser enviada
   */
  private void send(Mensagem msg, String ip, int port) throws Exception {
    DatagramSocket socket = new DatagramSocket();

    InetAddress IPAddress = InetAddress.getByName(ip);
    
    byte[] sendData = new byte[1024];
    Gson gson = new Gson();
    sendData = gson.toJson(msg).getBytes();

    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

    socket.send(sendPacket);
    socket.close();
  }

  /**
   * Inicialização padrão de um Peer
   */
  public Peer() {
    init = false;
  }

  /**
   * Inicialização do Peer
   * 
   * @param ipAndPort IP e porta do Peer
   * @param path Caminho para a pasta de arquivos
   * @param neigh1 IP e porta do 1º vizinho
   * @param neigh2 IP e porta do 2º vizinho
   */
  public Peer(String ipAndPort, String path, String neigh1, String neigh2) {
    String[] parts = ipAndPort.split(":");
    IP = parts[0];
    port = Integer.parseInt(parts[1]);
    this.path = path;
    this.neigh1 = new Neighbor(neigh1);
    this.neigh2 = new Neighbor(neigh2);

    fetchFiles();

    ListenThread listen = new ListenThread();
    listen.start();

    ListFilesThread list = new ListFilesThread();
    list.start();

    init = true;
  }

  /**
   * Função inicial e responsável pela entrada de dados pelo usuário.
   * @param args
   */
  public static void main(String[] args) {
    Peer peer = new Peer();
    Scanner input = new Scanner(System.in);
    while (true) {
      System.out.print("Digite a função (1: INICIALIZA, 2: SEARCH): ");
      int function = input.nextInt();
      input.nextLine();
      if (function == 1) {
        System.out.print("Digite o IP:porta: ");
        String ipAndPort = input.nextLine();
        System.out.print("Digite a pasta: ");
        String path = input.nextLine();
        System.out.print("Digite o IP:porta do 1° vizinho: ");
        String neigh1 = input.nextLine();
        System.out.print("Digite o IP:porta do 2° vizinho: ");
        String neigh2 = input.nextLine();
        peer = new Peer(ipAndPort, path, neigh1, neigh2);
      } else if (function == 2) {
        if (!peer.init) {
          System.out.println("PEER NÃO FOI INICIALIZADO");
        } else {
          System.out.print("Digite o nome do arquivo: ");
          String file = input.nextLine();
          peer.search(file);
        }
      } else {
        System.out.println("Opção invalida");
      }
    }
  }
}
