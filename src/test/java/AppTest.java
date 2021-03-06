import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.sql2o.*;
import org.junit.*;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Before
  public void setUp() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/hair_salon_test", null, null);
  }

  @After
  public void tearDown() {
    try(Connection con = DB.sql2o.open()) {
      String deleteClientsQuery = "DELETE FROM clients *;";
      String deleteStylistsQuery = "DELETE FROM stylists *;";
      con.createQuery(deleteClientsQuery).executeUpdate();
      con.createQuery(deleteStylistsQuery).executeUpdate();
    }
  }

  @Override
  public WebDriver getDefaultDriver() {
    return webDriver;
  }

  @ClassRule
  public static ServerRule server = new ServerRule();

  @Test
  public void rootTest() {
    goTo("http://localhost:4567/");
    assertThat(pageSource()).contains("Death By Glamour Salon");
  }

  @Test
  public void stylistIsCreatedAndDisplayedTest() {
    goTo("http://localhost:4567/");
    fill("#name").with("Bobby");
    submit(".btn");
    assertThat(pageSource()).contains("Bobby");
  }

  @Test
  public void clientIsCreatedAndDisplayedTest() {
    goTo("http://localhost:4567/");
    fill("#name").with("Bob");
    submit(".btn");
    click("a", withText("Bob"));
    fill("#client").with("Al");
    assertThat(pageSource()).contains("Al");
  }


  @Test
  public void allClientsDisplayNameOnStylistPage() {
    Stylist myStylist = new Stylist("Jimbo");
    myStylist.save();
    Client firstClient = new Client("Mark", myStylist.getId());
    firstClient.save();
    Client secondClient = new Client("Joe", myStylist.getId());
    secondClient.save();
    String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
    goTo(stylistPath);
    assertThat(pageSource()).contains("Mark");
    assertThat(pageSource()).contains("Joe");
  }

}
