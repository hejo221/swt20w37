package wineshop.wine;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import static wineshop.wine.Wine.WineType.*;
import static org.salespointframework.core.Currencies.EURO;


@Component
@Order(20)
class WineDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(WineDataInitializer.class);

	private final WineCatalog wineCatalog;

	WineDataInitializer(WineCatalog wineCatalog) {
		Assert.notNull(wineCatalog, "Wine catalog must not be null!");

		this.wineCatalog = wineCatalog;
	}

	@Override
	public void initialize() {

		if (wineCatalog.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("The default Wine Catalog is being created");
		wineCatalog.deleteAll();
		wineCatalog.save(new Wine(1, "Mas Martinet Clos Martinet", RED, "mas", Money.of(9.99, EURO), Money.of(15.99, EURO), "details"));
		wineCatalog.save(new Wine(2, "Acentaudo Tempranillo",  RED, "acentuado", Money.of(14.95, EURO), Money.of(14.95, EURO), "details"));
		wineCatalog.save(new Wine(3, "Hauswein Nr. 1", RED, "hauswein", Money.of(3.50, EURO), Money.of(5.50, EURO), "details" ));
		wineCatalog.save(new Wine(4, "Baron de Ley Reserva 2015", RED, "baron", Money.of(9.95, EURO),  Money.of(11.95, EURO), "details"));
		wineCatalog.save(new Wine(5, "MESA/4.9 Blanco", WHITE, "mesa", Money.of(6.99, EURO), Money.of(8.99, EURO), "details"));
		wineCatalog.save(new Wine(6, "Quietus Verdejo 2019", WHITE, "quietus", Money.of(19.99, EURO), Money.of(21.99, EURO), "details"));
		wineCatalog.save(new Wine(7, "Nembus Blanco 2019", WHITE, "nembus", Money.of(19.99, EURO), Money.of(21.99, EURO), "details"));
		wineCatalog.save(new Wine(8, "Intuicion Sauvignon Blanc 2019", WHITE, "intuicion", Money.of(24.99, EURO), Money.of(49.99, EURO), "details"));
		wineCatalog.save(new Wine(9, "Tutejšaje", FRUITWINE, "tut", Money.of(1.99, EURO), Money.of(2.99, EURO), "unglaublich lecker der Wein, nu!"));
		wineCatalog.save(new Wine(10, "12 € und gerettet", RED, "gerettet", Money.of(10.00, EURO), Money.of(12.00, EURO), "Der beste Freund des Menschen: spanischer Wein"));
		wineCatalog.save(new Wine(11, "Wine for dummies", WHITE, "dummies", Money.of(0.80, EURO), Money.of(1.23, EURO), "schar-do-nää!!!"));
		wineCatalog.save(new Wine(12, "Just Fucking Good Wine", RED , "fuckingGood", Money.of(20.00, EURO), Money.of(54.32, EURO), "nu genau!"));
		wineCatalog.save(new Wine(13, "Katzpiss", OTHER, "katzpiss", Money.of(0.90, EURO), Money.of(3.21, EURO), "unglaublich lecker der Wein, nu!"));
		wineCatalog.save(new Wine(14, "Sensaciones Edición Limitada", RED, "sensaciones", Money.of(19.95, EURO), Money.of(33.33, EURO), "details"));

	}
}
