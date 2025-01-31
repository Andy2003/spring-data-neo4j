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

import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.AbstractNeo4jConfig;
import org.springframework.data.neo4j.core.convert.Neo4jConversions;
import org.springframework.data.neo4j.core.mapping.Neo4jMappingContext;
import org.springframework.data.neo4j.integration.shared.common.ImmutablePersonWithExternallyGeneratedId;
import org.springframework.data.neo4j.integration.shared.common.ImmutablePersonWithExternallyGeneratedIdRelationshipProperties;
import org.springframework.data.neo4j.integration.shared.common.ImmutableSecondPersonWithExternallyGeneratedId;
import org.springframework.data.neo4j.integration.shared.common.ImmutableSecondPersonWithExternallyGeneratedIdRelationshipProperties;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.test.Neo4jExtension;
import org.springframework.data.neo4j.test.Neo4jIntegrationTest;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Gerrit Meier
 */
@Neo4jIntegrationTest
public class ImmutableExternallyGeneratedIdsIT {

	public static final String SOME_VALUE_VALUE = "testValue";
	protected static Neo4jExtension.Neo4jConnectionSupport neo4jConnectionSupport;

	@Test // GH-2141
	void saveWithExternallyGeneratedIdsReturnsObjectWithIdSet(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutablePersonWithExternallyGeneratedId fallback1 = new ImmutablePersonWithExternallyGeneratedId();
		ImmutablePersonWithExternallyGeneratedId fallback2 = ImmutablePersonWithExternallyGeneratedId.fallback(fallback1);
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.fallback(fallback2);

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.save(person);

		assertThat(savedPerson.id).isNotNull();
		assertThat(savedPerson.fallback).isNotNull();
		assertThat(savedPerson.fallback.fallback).isNotNull();
	}

	@Test // GH-2141
	void saveAllWithExternallyGeneratedIdsReturnsObjectWithIdSet(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutablePersonWithExternallyGeneratedId fallback1 = new ImmutablePersonWithExternallyGeneratedId();
		ImmutablePersonWithExternallyGeneratedId fallback2 = ImmutablePersonWithExternallyGeneratedId.fallback(fallback1);
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.fallback(fallback2);

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.id).isNotNull();
		assertThat(savedPerson.fallback).isNotNull();
		assertThat(savedPerson.fallback.fallback).isNotNull();
	}

	@Test // GH-2148
	void saveRelationshipWithExternallyGeneratedIdsContainsObjectWithIdSetForList(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutablePersonWithExternallyGeneratedId onboarder = new ImmutablePersonWithExternallyGeneratedId();
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.wasOnboardedBy(Collections.singletonList(onboarder));

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.wasOnboardedBy.get(0).id).isNotNull();
	}

	@Test // GH-2148
	void saveRelationshipWithExternallyGeneratedIdsContainsObjectWithIdSetForSet(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutablePersonWithExternallyGeneratedId knowingPerson = new ImmutablePersonWithExternallyGeneratedId();
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.knownBy(Collections.singleton(knowingPerson));

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.knownBy.iterator().next().id).isNotNull();
	}

	@Test // GH-2148
	void saveRelationshipWithExternallyGeneratedIdsContainsObjectWithIdSetForMap(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutablePersonWithExternallyGeneratedId rater = new ImmutablePersonWithExternallyGeneratedId();
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.ratedBy(Collections.singletonMap("Good", rater));

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.ratedBy.keySet().iterator().next()).isEqualTo("Good");
		assertThat(savedPerson.ratedBy.values().iterator().next().id).isNotNull();
	}

	@Test // GH-2148
	void saveRelationshipWithExternallyGeneratedIdsContainsObjectWithIdSetForMapWithMultipleKeys(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutablePersonWithExternallyGeneratedId rater1 = new ImmutablePersonWithExternallyGeneratedId();
		ImmutablePersonWithExternallyGeneratedId rater2 = new ImmutablePersonWithExternallyGeneratedId();
		Map<String, ImmutablePersonWithExternallyGeneratedId> raterMap = new HashMap<>();
		raterMap.put("Good", rater1);
		raterMap.put("Bad", rater2);
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.ratedBy(raterMap);

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.ratedBy.keySet()).containsExactlyInAnyOrder("Good", "Bad");
		assertThat(savedPerson.ratedBy.get("Good").id).isNotNull();
		assertThat(savedPerson.ratedBy.get("Bad").id).isNotNull();
	}

	@Test // GH-2148
	void saveRelationshipWithExternallyGeneratedIdsContainsObjectWithIdSetForMapCollection(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutableSecondPersonWithExternallyGeneratedId rater = new ImmutableSecondPersonWithExternallyGeneratedId();
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.ratedByCollection(Collections.singletonMap("Good", Collections.singletonList(rater)));

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.ratedByCollection.values().iterator().next().get(0).id).isNotNull();
	}

	@Test // GH-2148
	void saveRelationshipWithExternallyGeneratedIdsContainsObjectWithIdSetForRelationshipProperties(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutablePersonWithExternallyGeneratedId somebody = new ImmutablePersonWithExternallyGeneratedId();
		ImmutablePersonWithExternallyGeneratedIdRelationshipProperties properties = new ImmutablePersonWithExternallyGeneratedIdRelationshipProperties(null, "blubb", somebody);
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.relationshipProperties(properties);

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.relationshipProperties.name).isNotNull();
		assertThat(savedPerson.relationshipProperties.target.id).isNotNull();
	}

	@Test // GH-2148
	void saveRelationshipWithExternallyGeneratedIdsContainsObjectWithIdSetForRelationshipPropertiesCollection(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutablePersonWithExternallyGeneratedId somebody = new ImmutablePersonWithExternallyGeneratedId();
		ImmutablePersonWithExternallyGeneratedIdRelationshipProperties properties = new ImmutablePersonWithExternallyGeneratedIdRelationshipProperties(null, "blubb", somebody);
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.relationshipPropertiesCollection(Collections.singletonList(properties));

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.relationshipPropertiesCollection.get(0).name).isNotNull();
		assertThat(savedPerson.relationshipPropertiesCollection.get(0).target.id).isNotNull();
	}

	@Test // GH-2148
	void saveRelationshipWithExternallyGeneratedIdsContainsObjectWithIdSetForRelationshipPropertiesDynamic(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutablePersonWithExternallyGeneratedId somebody = new ImmutablePersonWithExternallyGeneratedId();
		ImmutablePersonWithExternallyGeneratedIdRelationshipProperties properties = new ImmutablePersonWithExternallyGeneratedIdRelationshipProperties(null, "blubb", somebody);
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.relationshipPropertiesDynamic(Collections.singletonMap("Good", properties));

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.relationshipPropertiesDynamic.keySet().iterator().next()).isEqualTo("Good");
		assertThat(savedPerson.relationshipPropertiesDynamic.values().iterator().next().name).isNotNull();
		assertThat(savedPerson.relationshipPropertiesDynamic.values().iterator().next().target.id).isNotNull();
	}


	@Test // GH-2148
	void saveRelationshipWithExternallyGeneratedIdsContainsObjectWithIdSetForRelationshipPropertiesDynamicCollection(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutableSecondPersonWithExternallyGeneratedId somebody = new ImmutableSecondPersonWithExternallyGeneratedId();
		ImmutableSecondPersonWithExternallyGeneratedIdRelationshipProperties properties = new ImmutableSecondPersonWithExternallyGeneratedIdRelationshipProperties(null, "blubb", somebody);
		ImmutablePersonWithExternallyGeneratedId person = ImmutablePersonWithExternallyGeneratedId.relationshipPropertiesDynamicCollection(Collections.singletonMap("Good", Collections.singletonList(properties)));

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.relationshipPropertiesDynamicCollection.keySet().iterator().next()).isEqualTo("Good");
		assertThat(savedPerson.relationshipPropertiesDynamicCollection.values().iterator().next().get(0).name).isNotNull();
		assertThat(savedPerson.relationshipPropertiesDynamicCollection.values().iterator().next().get(0).target.id).isNotNull();
	}

	@Test // GH-2148
	void saveRelationshipWithExternallyGeneratedIdsContainsAllRelationshipTypes(
			@Autowired ImmutablePersonWithExternalIdRepository repository) {

		ImmutablePersonWithExternallyGeneratedId fallback =
				new ImmutablePersonWithExternallyGeneratedId();

		List<ImmutablePersonWithExternallyGeneratedId> wasOnboardedBy =
				Collections.singletonList(new ImmutablePersonWithExternallyGeneratedId());

		Set<ImmutablePersonWithExternallyGeneratedId> knownBy =
				Collections.singleton(new ImmutablePersonWithExternallyGeneratedId());

		Map<String, ImmutablePersonWithExternallyGeneratedId> ratedBy =
				Collections.singletonMap("Good", new ImmutablePersonWithExternallyGeneratedId());

		Map<String, List<ImmutableSecondPersonWithExternallyGeneratedId>> ratedByCollection =
				Collections.singletonMap("Na", Collections.singletonList(new ImmutableSecondPersonWithExternallyGeneratedId()));

		ImmutablePersonWithExternallyGeneratedIdRelationshipProperties relationshipProperties =
				new ImmutablePersonWithExternallyGeneratedIdRelationshipProperties(null, "rel1", new ImmutablePersonWithExternallyGeneratedId());

		List<ImmutablePersonWithExternallyGeneratedIdRelationshipProperties> relationshipPropertiesCollection =
				Collections.singletonList(new ImmutablePersonWithExternallyGeneratedIdRelationshipProperties(null, "rel2", new ImmutablePersonWithExternallyGeneratedId()));

		Map<String, ImmutablePersonWithExternallyGeneratedIdRelationshipProperties> relationshipPropertiesDynamic =
				Collections.singletonMap("Ok", new ImmutablePersonWithExternallyGeneratedIdRelationshipProperties(null, "rel3", new ImmutablePersonWithExternallyGeneratedId()));

		Map<String, List<ImmutableSecondPersonWithExternallyGeneratedIdRelationshipProperties>> relationshipPropertiesDynamicCollection =
				Collections.singletonMap("Nope",
						Collections.singletonList(new ImmutableSecondPersonWithExternallyGeneratedIdRelationshipProperties(
								null, "rel4", new ImmutableSecondPersonWithExternallyGeneratedId()))
				);

		ImmutablePersonWithExternallyGeneratedId person = new ImmutablePersonWithExternallyGeneratedId(null,
				wasOnboardedBy,
				knownBy,
				ratedBy,
				ratedByCollection,
				fallback,
				relationshipProperties,
				relationshipPropertiesCollection,
				relationshipPropertiesDynamic,
				relationshipPropertiesDynamicCollection
				);

		ImmutablePersonWithExternallyGeneratedId savedPerson = repository.saveAll(Collections.singleton(person)).get(0);

		assertThat(savedPerson.wasOnboardedBy.get(0).id).isNotNull();
		assertThat(savedPerson.knownBy.iterator().next().id).isNotNull();

		assertThat(savedPerson.ratedBy.keySet().iterator().next()).isEqualTo("Good");
		assertThat(savedPerson.ratedBy.values().iterator().next().id).isNotNull();

		assertThat(savedPerson.ratedByCollection.keySet().iterator().next()).isEqualTo("Na");
		assertThat(savedPerson.ratedByCollection.values().iterator().next().get(0).id).isNotNull();

		assertThat(savedPerson.fallback.id).isNotNull();

		assertThat(savedPerson.relationshipProperties.name).isEqualTo("rel1");
		assertThat(savedPerson.relationshipProperties.target.id).isNotNull();

		assertThat(savedPerson.relationshipPropertiesCollection.get(0).name).isEqualTo("rel2");
		assertThat(savedPerson.relationshipPropertiesCollection.get(0).target.id).isNotNull();

		assertThat(savedPerson.relationshipPropertiesDynamic.keySet().iterator().next()).isEqualTo("Ok");
		assertThat(savedPerson.relationshipPropertiesDynamic.values().iterator().next().name).isEqualTo("rel3");
		assertThat(savedPerson.relationshipPropertiesDynamic.values().iterator().next().target.id).isNotNull();

		assertThat(savedPerson.relationshipPropertiesDynamicCollection.keySet().iterator().next()).isEqualTo("Nope");
		assertThat(savedPerson.relationshipPropertiesDynamicCollection.values().iterator().next().get(0).name).isEqualTo("rel4");
		assertThat(savedPerson.relationshipPropertiesDynamicCollection.values().iterator().next().get(0).target.id).isNotNull();
	}

	interface ImmutablePersonWithExternalIdRepository extends Neo4jRepository<ImmutablePersonWithExternallyGeneratedId, UUID> {}

	@Configuration
	@EnableNeo4jRepositories(considerNestedRepositories = true)
	@EnableTransactionManagement
	static class Config extends AbstractNeo4jConfig {

		@Bean
		public Driver driver() {
			return neo4jConnectionSupport.getDriver();
		}

		@Override
		protected Collection<String> getMappingBasePackages() {
			return Arrays.asList(ImmutablePersonWithExternallyGeneratedId.class.getPackage().getName());
		}

		@Bean
		public Neo4jMappingContext neo4jMappingContext(Neo4jConversions neo4JConversions) throws ClassNotFoundException {

			Neo4jMappingContext mappingContext = new Neo4jMappingContext(neo4JConversions);
			mappingContext.setInitialEntitySet(getInitialEntitySet());
			mappingContext.setStrict(true);

			return mappingContext;
		}
	}
}
