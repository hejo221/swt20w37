/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wineshop.wine;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static org.salespointframework.core.Currencies.EURO;


@Component
@Order(20)
class WineDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(WineDataInitializer.class);

	private final WineRepository wineRepository;

	WineDataInitializer(WineRepository wineRepository) {
		Assert.notNull(wineRepository, "VideoCatalog must not be null!");

		this.wineRepository = wineRepository;
	}

	@Override
	public void initialize() {

		if (wineRepository.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default catalog entries.");

		wineRepository.save(new Wine("Mas Martinet Clos Martinet", "mas", Money.of(99, EURO), "red", Wine.WineType.RED));
		wineRepository.save(new Wine("Acentaudo Tempranillo", "acentuado", Money.of(14.95, EURO), "red", Wine.WineType.RED));
		wineRepository.save(new Wine("Sensaciones Edición Limitada", "sensaciones", Money.of(19.95, EURO), "redwine", Wine.WineType.RED));
		wineRepository.save(new Wine("Hauswein Nr. 1", "hauswein", Money.of(3.50, EURO), "red", Wine.WineType.RED));
		wineRepository.save(new Wine("Baron de Ley Reserva 2015", "baron", Money.of(9.95, EURO),"red", Wine.WineType.RED));

		wineRepository.save(new Wine("MESA/4.9 Blanco", "mesa", Money.of(6.99, EURO), "white", Wine.WineType.WHITE));
		wineRepository.save(new Wine("Quietus Verdejo 2019", "quietus", Money.of(19.99, EURO), "white", Wine.WineType.WHITE));
		wineRepository.save(new Wine("Nembus Blanco 2019", "nembus", Money.of(19.99, EURO), "white", Wine.WineType.WHITE));
		wineRepository.save(new Wine("Intuicion Sauvignon Blanc 2019", "intuicion", Money.of(24.99, EURO), "white", Wine.WineType.WHITE));
	}
}
