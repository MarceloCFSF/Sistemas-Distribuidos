package peers;

import java.util.UUID;

/**
 * Classe que define uma mensagem a ser enviada
 */
public class Mensagem {
  public final String uuuid = UUID.randomUUID().toString();

  /**
   * Tipo de uma mensagem
   */
  public enum MensagemType {
    SEARCH, // Mensagem de busca
    ANSWER // Mensagem de resposta ao encontrar um arquivo
  }

  public MensagemType type;
  public String file;
  public String IP;
  public int port;

  /**
   * Inicialização padrão de uma Mensagem
   */
  public Mensagem() {
    IP = "127.0.0.1";
    port = 8000;
    file = "";
    type = MensagemType.SEARCH;
  }

  /**
   * Inicializa uma mensagem
   * @param ip IP de quem criou a mensagem
   * @param port Porta de quem criou a mensagem
   * @param file Arquivo a ser buscado
   * @param type Tipo de mensagem (SEARCH ou ANSWER)
   */
  public Mensagem(String ip, int port, String file, MensagemType type) {
    this.file = file;
    this.IP = ip;
    this.port = port;
    this.type = type;
  }
}
