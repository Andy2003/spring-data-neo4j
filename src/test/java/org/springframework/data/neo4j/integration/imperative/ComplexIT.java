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

import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.AbstractNeo4jConfig;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jBookmarkManager;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.integration.shared.complex.BaseNodeEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.test.BookmarkCapture;
import org.springframework.data.neo4j.test.Neo4jExtension;
import org.springframework.data.neo4j.test.Neo4jIntegrationTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.ITERABLE;
import static org.assertj.core.api.InstanceOfAssertFactories.iterable;

@Neo4jIntegrationTest
public class ComplexIT {

	protected static Neo4jExtension.Neo4jConnectionSupport neo4jConnectionSupport;

	private final Driver driver;

	private final BookmarkCapture bookmarkCapture;

	private final TransactionTemplate transactionTemplate;

	@Autowired
	public ComplexIT(Driver driver, BookmarkCapture bookmarkCapture, TransactionTemplate transactionTemplate) {
		this.driver = driver;
		this.bookmarkCapture = bookmarkCapture;
		this.transactionTemplate = transactionTemplate;
	}


	@BeforeAll
	protected static void setupData() {
		try (Transaction transaction = neo4jConnectionSupport.getDriver().session().beginTransaction()) {
			transaction.run("MATCH (n) DETACH DELETE n");
			transaction.run("CREATE (ntRoot:NodeType{nodeTypeId: 'root'})\n" +
					"CREATE (ntCompany:NodeType{nodeTypeId: 'company'})\n" +
					"CREATE (ntLocation:NodeType{nodeTypeId: 'location'})\n" +
					"CREATE (ntMeter:NodeType{nodeTypeId: 'meter'})\n" +
					"\n" +
					"CREATE (root:Node:BaseNodeEntity{nodeId: 'root'})\n" +
					"CREATE (company:Node:BaseNodeEntity{nodeId: 'comp'})\n" +
					"CREATE (location1:Node:BaseNodeEntity{nodeId: 'location1'})\n" +
					"CREATE (location2:Node:BaseNodeEntity{nodeId: 'location2'})\n" +
					"\n" +
					"CREATE (meter1:MeasurementMeta:BaseNodeEntity{nodeId: 'meter1'})\n" +
					"CREATE (meter2:MeasurementMeta:BaseNodeEntity{nodeId: 'meter2'})\n" +
					"\n" +
					"CREATE (measurand1:Measurand{measurandId: 'm1'})\n" +
					"CREATE (measurand2:Measurand{measurandId: 'm2'})\n" +
					"CREATE (measurand3:Measurand{measurandId: 'm3'})\n" +
					"\n" +
					"CREATE (root)-[:HAS_TYPE]->(ntRoot)\n" +
					"CREATE (company)-[:HAS_TYPE]->(ntCompany)\n" +
					"CREATE (location1)-[:HAS_TYPE]->(ntLocation)\n" +
					"CREATE (location2)-[:HAS_TYPE]->(ntLocation)\n" +
					"CREATE (meter1)-[:HAS_TYPE]->(ntMeter)\n" +
					"CREATE (meter2)-[:HAS_TYPE]->(ntMeter)\n" +
					"\n" +
					"CREATE (meter1)-[:CHILD_OF]->(location1)\n" +
					"CREATE (meter2)-[:CHILD_OF]->(location2)\n" +
					"CREATE (location1)-[:CHILD_OF]->(company)\n" +
					"CREATE (location2)-[:CHILD_OF]->(company)\n" +
					"CREATE (company)-[:CHILD_OF]->(root)\n" +
					"\n" +
					"CREATE (meter1)-[:BELONGS_TO]->(company)\n" +
					"CREATE (meter2)-[:BELONGS_TO]->(company)\n" +
					"\n" +
					"CREATE (meter1)<-[:IS_MEASURED_BY]-(measurand1)\n" +
					"CREATE (meter1)<-[:IS_MEASURED_BY]-(measurand2)\n" +
					"CREATE (meter2)<-[:IS_MEASURED_BY]-(measurand1)\n" +
					"CREATE (meter2)<-[:IS_MEASURED_BY]-(measurand3)\n");
			transaction.commit();
		}
	}

	@Test
	void shouldLoadOnlyIdsOfRelation(@Autowired BaseNodeRepository<BaseNodeEntity> repository) {
		Optional<NodeWithChild> rootNode = repository.findNodeByNodeId("root", NodeWithChild.class);
		assertThat(rootNode).isPresent()
				.get()
				.satisfies(root -> {
					assertThat(root).extracting(NodeWithChild::getNodeId).isEqualTo("root");
					assertThat(root).extracting(NodeWithChild::getChildNodes, (as(iterable(NodeWithChild.class))))
							.hasSize(1)
							.first()
							.satisfies(child -> {
								assertThat(child).extracting(NodeWithoutChild::getNodeId).isEqualTo("root");
								// TODO we need to know the Labels, so we can query the right repository to load a child node
								assertThat(child).extracting(NodeWithoutChild::getLabels, as(ITERABLE)).containsExactly("BaseNodeEntity", "Node");
							});
				});
	}

	public interface BaseNodeRepository<T extends BaseNodeEntity> extends Neo4jRepository<T, String> {

		<R> Optional<R> findNodeByNodeId(String nodeId, Class<R> clazz);
	}

	public interface NodeWithoutChild {
		List<String> getLabels();

		String getNodeId();
	}

	public interface NodeWithChild extends NodeWithoutChild {
		Set<NodeWithoutChild> getChildNodes();
	}

	@Configuration
	@EnableNeo4jRepositories(considerNestedRepositories = true)
	@EnableTransactionManagement
	static class Config extends AbstractNeo4jConfig {

		@Override
		protected Collection<String> getMappingBasePackages() {
			return Collections.singleton(BaseNodeEntity.class.getPackage().getName());
		}

		@Bean
		public Driver driver() {
			return neo4jConnectionSupport.getDriver();
		}

		@Bean
		public BookmarkCapture bookmarkCapture() {
			return new BookmarkCapture();
		}

		@Bean
		@Override
		public PlatformTransactionManager transactionManager(Driver driver, DatabaseSelectionProvider databaseNameProvider) {

			BookmarkCapture bookmarkCapture = bookmarkCapture();
			return new Neo4jTransactionManager(driver, databaseNameProvider, Neo4jBookmarkManager.create(bookmarkCapture));
		}

		@Bean
		public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
			return new TransactionTemplate(transactionManager);
		}
	}
}
