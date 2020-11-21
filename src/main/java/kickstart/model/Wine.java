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
package kickstart.model;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wine extends Product {

	public static enum WineType {
		WHITE, RED;
	}

	private String genre, image;
	private WineType type;

	@OneToMany(cascade = CascadeType.ALL) //
	private List<Comment> comments = new ArrayList<>();

	@SuppressWarnings({ "unused", "deprecation" })
	private Wine() {}

	public Wine(String name, String image, Money price, String genre, WineType type) {

		super(name, price);

		this.image = image;
		this.genre = genre;
		this.type = type;
	}

	public String getGenre() {
		return genre;
	}

	public void addComment(Comment comment) {
		comments.add(comment);
	}

	public Iterable<Comment> getComments() {
		return comments;
	}

	public String getImage() {
		return image;
	}

	public WineType getType() {
		return type;
	}
}
