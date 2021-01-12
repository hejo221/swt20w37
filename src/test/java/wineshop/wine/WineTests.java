package wineshop.wine;

import static org.assertj.core.api.Assertions.*;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

public class WineTests {

	@Test
	void throwsEmptyItemNr() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Wine(null, "name", Wine.WineType.FRUITWINE, "pic", Money.of(10, "EUR"), Money.of(10, "EUR"), "details"));
	}

	@Test
	void throwsEmptyName() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Wine(1, null, Wine.WineType.FRUITWINE, "pic", Money.of(10, "EUR"), Money.of(10, "EUR"), "details"));
	}

	@Test
	void throwsEmptyWineType() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Wine(1, "name", null, "pic", Money.of(10, "EUR"), Money.of(10, "EUR"), "details"));
	}

	@Test
	void throwsEmptyPic() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Wine(null, "name", Wine.WineType.FRUITWINE, null, Money.of(10, "EUR"), Money.of(10, "EUR"), "details"));
	}

	@Test
	void throwsEmptyBuyPrice() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Wine(1, "name", Wine.WineType.FRUITWINE, "pic", null, Money.of(10, "EUR"), "details"));
	}

	@Test
	void throwsEmptySellPrice() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Wine(1, "name", Wine.WineType.FRUITWINE, "pic", Money.of(10, "EUR"), null, "details"));
	}

	@Test
	void throwsEmptyDetails() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Wine(1, "name", Wine.WineType.FRUITWINE, "pic", Money.of(10, "EUR"), Money.of(10, "EUR"), null));
	}


}
