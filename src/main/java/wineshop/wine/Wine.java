/*
 * Copyright 2013-2020 the original author or authors.
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

import org.salespointframework.catalog.Product;
import javax.money.MonetaryAmount;
import javax.persistence.Entity;


@Entity
public class Wine extends Product {

	public static enum WineType {
		WHITE, RED;
	}

	private String description, image;
	private WineType type;

	@SuppressWarnings({ "unused", "deprecation" })
	private Wine() {}

	public Wine(String name, String image, MonetaryAmount price, String description, WineType type) {
		super(name, price);

		this.image = image;
		this.description = description;
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public String getImage() { return image; }

	public WineType getType() {	return type; }
}
