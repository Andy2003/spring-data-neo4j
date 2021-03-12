/*
 * Copyright 2011-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.neo4j.integration.imperative;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Property;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.config.AbstractNeo4jConfig;
import org.springframework.data.neo4j.core.mapping.Constants;
import org.springframework.data.neo4j.integration.shared.common.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.repository.support.CypherdslConditionExecutor;
import org.springframework.data.neo4j.test.Neo4jExtension;
import org.springframework.data.neo4j.test.Neo4jIntegrationTest;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Michael J. Simons
 */
@Neo4jIntegrationTest
class CypherdslIT {

	protected static Neo4jExtension.Neo4jConnectionSupport neo4jConnectionSupport;

	private final Driver driver;
	private final Node person;
	private final Property firstName;
	private final Property lastName;

	CypherdslIT(@Autowired Driver driver) {

		this.driver = driver;

		this.person = Cypher.node("Person").named(Constants.NAME_OF_ROOT_NODE);
		this.firstName = this.person.property("firstName");
		this.lastName = this.person.property("lastName");
	}

	@BeforeEach
	protected void setupData() {
		try (Transaction transaction = driver.session().beginTransaction()) {
			transaction.run("MATCH (n) detach delete n");
			transaction.run("CREATE (p:Person{firstName: 'A', lastName: 'LA'})");
			transaction.run("CREATE (p:Person{firstName: 'B', lastName: 'LB'})");
			transaction.run("CREATE (p:Person{firstName: 'Helge', lastName: 'Schneider'})");
			transaction.run("CREATE (p:Person{firstName: 'Bela', lastName: 'B.'})");
			transaction.commit();
		}
	}

	@Test
	void findOneShouldWork(@Autowired CypherDSLPersonRepository repository) {

		assertThat(repository.findOne(firstName.eq(Cypher.literalOf("Helge"))))
				.hasValueSatisfying(p -> assertThat(p).extracting(Person::getLastName).isEqualTo("Schneider"));
	}

	@Test
	void findAllShouldWork(@Autowired CypherDSLPersonRepository repository) {

		assertThat(repository.findAll(firstName.eq(Cypher.literalOf("Helge")).or(lastName.eq(Cypher.literalOf("B.")))))
				.extracting(Person::getFirstName)
				.containsExactlyInAnyOrder("Bela", "Helge");
	}

	@Test
	void sortedFindAllShouldWork(@Autowired CypherDSLPersonRepository repository) {

		assertThat(
				repository.findAll(firstName.eq(Cypher.literalOf("Helge")).or(lastName.eq(Cypher.literalOf("B."))),
						Sort.by("lastName").descending()
				))
				.extracting(Person::getFirstName)
				.containsExactly("Helge", "Bela");
	}

	@Test
	void sortedFindAllShouldWorkWithParameter(@Autowired CypherDSLPersonRepository repository) {

		assertThat(
				repository.findAll(firstName.eq(Cypher.anonParameter("Helge")).or(lastName.eq(Cypher.parameter("someName", "B."))),
						Sort.by("lastName").descending()
				))
				.extracting(Person::getFirstName)
				.containsExactly("Helge", "Bela");
	}

	@Test
	void orderedFindAllShouldWork(@Autowired CypherDSLPersonRepository repository) {

		assertThat(
				repository.findAll(firstName.eq(Cypher.literalOf("Helge")).or(lastName.eq(Cypher.literalOf("B."))),
						lastName.descending()
				))
				.extracting(Person::getFirstName)
				.containsExactly("Helge", "Bela");
	}

	@Test
	void orderedFindAllWithoutPredicateShouldWork(@Autowired CypherDSLPersonRepository repository) {

		assertThat(repository.findAll(lastName.descending()))
				.extracting(Person::getFirstName)
				.containsExactly("Helge", "B", "A", "Bela");
	}

	@Test
	void pagedFindAllShouldWork(@Autowired CypherDSLPersonRepository repository) {

		assertThat(
				repository.findAll(firstName.eq(Cypher.literalOf("Helge")).or(lastName.eq(Cypher.literalOf("B."))),
						PageRequest.of(1, 1, Sort.by("lastName").descending())
				))
				.extracting(Person::getFirstName)
				.containsExactly("B");
	}

	@Test
	void countShouldWork(@Autowired CypherDSLPersonRepository repository) {

		assertThat(repository.count(firstName.eq(Cypher.literalOf("Helge")).or(lastName.eq(Cypher.literalOf("B."))))).isEqualTo(2L);
	}

	@Test
	void existsShouldWork(@Autowired CypherDSLPersonRepository repository) {

		assertThat(repository.exists(firstName.eq(Cypher.literalOf("A")))).isTrue();
	}

	interface CypherDSLPersonRepository extends Neo4jRepository<Person, Long>, CypherdslConditionExecutor<Person> {
	}

	@Configuration
	@EnableTransactionManagement
	@EnableNeo4jRepositories(considerNestedRepositories = true)
	static class Config extends AbstractNeo4jConfig {

		@Bean
		public Driver driver() {

			return neo4jConnectionSupport.getDriver();
		}
	}
}
