/*
 * Copyright 2017-2019 the original author or authors.
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
import org.salespointframework.useraccount.Role;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.salespointframework.core.Currencies.EURO;


@Service
@Transactional
public class WineManager {

	public static final Role CUSTOMER_ROLE = Role.of("EMPLOYEE");

	private final WineRepository wineRepository;

	WineManager(WineRepository wineRepository) {
		Assert.notNull(wineRepository, "AccountRepository must not be null!");

		this.wineRepository = wineRepository;
	}


	public Wine addWine(WineRegistrationForm form) {
		Assert.notNull(form, "Registration form must not be null!");

		Wine.WineType type;
		if(form.getType() == "RED") {
			type = Wine.WineType.RED;
 		} else {
			type = Wine.WineType.WHITE;
		}

		Wine wine = new Wine(form.getName(), form.getImage(), Money.of(form.getPrice(), EURO), form.getDescription(), type);

		return wineRepository.save(wine);
	}

	public Streamable<Wine> findAll() {
		return wineRepository.findAll();
	}
}
