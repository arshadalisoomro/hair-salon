import java.util.List;
import org.sql2o.*;

public class Client {
  private int id;
  private String name;

  public Client(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}