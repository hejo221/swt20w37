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
package kickstart.repository;

import kickstart.model.Wine;
import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * An extension of {@link Catalog} to add video shop specific query methods.
 *
 * @author Oliver Gierke
 */
public interface WineRepository extends Catalog<Wine> {

	static final Sort DEFAULT_SORT = Sort.by("productIdentifier").descending();

	Iterable<Wine> findByType(Wine.WineType type, Sort sort);

	default Iterable<Wine> findByType(Wine.WineType type) {
		return findByType(type, DEFAULT_SORT);
	}

	Page<Wine> findByName(String name, Pageable pageable);
}
