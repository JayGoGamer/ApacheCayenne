package example;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.datasource.DataSourceBuilder;
import org.apache.cayenne.query.ObjectSelect;
import example.cayenne.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Application {
    private final ServerRuntime cayenneRuntime;

    public static void main(String[] args) {
        Application app = new Application();
        app.insertReiziger();
        app.insertAdres();
        app.insertOvChipkaart();
        app.insertProduct();
        app.selectOvChipkaarten();
        app.selectEersteKlasOvChipkaart();
        app.updateOvChipkaartProduct();
        app.deleteProduct();
    }

    public Application() {
        cayenneRuntime = ServerRuntime.builder()
                .addConfig("cayenne-cayenne.xml") // Dit is het bestand in je recourse genaam cayenne-*.xml
                .dataSource(DataSourceBuilder.url("jdbc:postgresql://localhost:5432/ovchip") // Je database adres
                        .driver(org.postgresql.Driver.class.getName()) // De postgresql driver
                        .userName("jdbc").password("Hallo") // De inloggegevens van je database
                        .pool(1, 3) // Aantal toegestaande connecties
                        .build())
                .build();
    }

    public void insertReiziger() {
        ObjectContext context = cayenneRuntime.newContext(); // Een nieuwe ObjectContext aanmaken

        Reiziger reiziger1 = context.newObject(Reiziger.class); // Een nieuwe reiziger defineren
        reiziger1.setAchternaam("Huissen"); // De not null variabele invullen
        reiziger1.setVoorletters("J.J.");

        reiziger1.setReizigerId(6); // De primary key een waarde geven

        context.commitChanges(); // Alles commiten naar de database
    }

    public void insertAdres() {
        ObjectContext context = cayenneRuntime.newContext();

        // Selecteer de reiziger met de reiziger_id 6
        Reiziger reiziger1 = ObjectSelect.query(Reiziger.class, Reiziger.REIZIGER_ID.eq(6))
                .selectOne(context); // Selecteer 1 van de resultaten. (In dit geval kan het er maar 1 zijn)

        Adres adres1 = context.newObject(Adres.class); // Maak een nieuw adres object aan
        adres1.setHuisnummer("14");
        adres1.setPostcode("2141AT");
        adres1.setStraat("Spieringhof");
        adres1.setWoonplaats("Vijfhuizen");
        adres1.setAdresId(6);

        adres1.setReiziger(reiziger1); // Geef de reiziger mee aan het adres

        context.commitChanges();
    }

    public void insertOvChipkaart() {
        ObjectContext context = cayenneRuntime.newContext();

        // Selecteer de reiziger met de reiziger_id 6
        Reiziger reiziger1 = ObjectSelect.query(Reiziger.class, Reiziger.REIZIGER_ID.eq(6))
                .selectOne(context); // Selecteer 1 van de resultaten. (In dit geval kan het er maar 1 zijn)

        OvChipkaart ovChipkaart1 = context.newObject(OvChipkaart.class); // Maak een nieuwe OVChipkaart aan in de context
        ovChipkaart1.setGeldigTot(LocalDate.of(2024, 10, 10)); // Geef de not null variabele een waarde
        ovChipkaart1.setKlasse(BigDecimal.valueOf(1));
        ovChipkaart1.setSaldo(BigDecimal.valueOf(25));
        ovChipkaart1.setKaartNummer(1234);

        OvChipkaart ovChipkaart2 = context.newObject(OvChipkaart.class); // Maak een nieuwe OVChipkaart aan in de context
        ovChipkaart2.setGeldigTot(LocalDate.of(2024, 10, 10)); // Geef de not null variabele een waarde
        ovChipkaart2.setKlasse(BigDecimal.valueOf(2));
        ovChipkaart2.setSaldo(BigDecimal.valueOf(12.5));
        ovChipkaart2.setKaartNummer(4321);

        ovChipkaart1.setReiziger(reiziger1); // Geef de reizger mee aan de OVChipkaarten
        ovChipkaart2.setReiziger(reiziger1);

        context.commitChanges(); // Alles commiten naar de database
    }

    public void insertProduct() {
        ObjectContext context = cayenneRuntime.newContext();

        OvChipkaart ovChipkaart1 = ObjectSelect.query(OvChipkaart.class, OvChipkaart.KAART_NUMMER.eq(1234))
                .selectOne(context);

        Product product1 = context.newObject(Product.class);
        product1.setBeschrijving("Een hele dag onbeperkt reizen in de 1e klas");
        product1.setNaam("Dagkaart 1e klas");
        product1.setPrijs(BigDecimal.valueOf(100));
        product1.setProductNummer(7);

        Product product2 = context.newObject(Product.class);
        product2.setBeschrijving("De hele week onbeperkt reizen in de 2e klas");
        product2.setNaam("Weekkaart 2e klas");
        product2.setPrijs(BigDecimal.valueOf(300));
        product2.setProductNummer(8);

        OvChipkaartProduct ovChipkaartProduct1 = context.newObject(OvChipkaartProduct.class);
        ovChipkaartProduct1.setStatus("actief");
        ovChipkaartProduct1.setLastUpdate(LocalDate.now());

        ovChipkaartProduct1.setProduct(product1);
        ovChipkaartProduct1.setOvChipkaart(ovChipkaart1);

        OvChipkaartProduct ovChipkaartProduct2 = context.newObject(OvChipkaartProduct.class);
        ovChipkaartProduct2.setStatus("betaald");
        ovChipkaartProduct2.setLastUpdate(LocalDate.now());

        ovChipkaartProduct2.setProduct(product2);
        ovChipkaartProduct2.setOvChipkaart(ovChipkaart1);

        context.commitChanges();
    }

    public void selectOvChipkaarten() {
        ObjectContext context = cayenneRuntime.newContext();

        List<OvChipkaart> ovchipkaarten = ObjectSelect.query(OvChipkaart.class).select(context); // Alle OVChipkaarten ophalen

        ovchipkaarten.forEach(System.out::println);
    }

    public void selectEersteKlasOvChipkaart() {
        ObjectContext context = cayenneRuntime.newContext();

        List<OvChipkaart> ovChipkaarten = ObjectSelect.query(OvChipkaart.class,
                OvChipkaart.KLASSE.eq(BigDecimal.valueOf(1))).select(context);

        ovChipkaarten.forEach(System.out::println);
    }

    public void updateOvChipkaartProduct() {
        ObjectContext context = cayenneRuntime.newContext();

        Product product1 = ObjectSelect.query(Product.class, Product.PRODUCT_NUMMER.eq(8)).selectOne(context);
        OvChipkaart ovChipkaart1 = ObjectSelect.query(OvChipkaart.class, OvChipkaart.KAART_NUMMER.eq(1234)).selectOne(context);

        OvChipkaartProduct ovChipkaartProduct = ObjectSelect.query(OvChipkaartProduct.class)
                .where(OvChipkaartProduct.PRODUCT.eq(product1))
                .where(OvChipkaartProduct.OV_CHIPKAART.eq(ovChipkaart1))
                .selectOne(context);

        ovChipkaartProduct.setLastUpdate(LocalDate.now());
        ovChipkaartProduct.setStatus("actief");

        context.commitChanges();
    }

    public void deleteProduct() {
        ObjectContext context = cayenneRuntime.newContext();

        Product product1 = ObjectSelect.query(Product.class, Product.PRODUCT_NUMMER.eq(8)).selectOne(context);

        context.deleteObjects(product1);
        context.commitChanges();
    }
}