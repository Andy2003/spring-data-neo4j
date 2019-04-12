/*
 * Copyright (c) 2019 "Neo4j,"
 * Neo4j Sweden AB [https://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.neo4j.core.transaction;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.data.neo4j.core.transaction.Neo4jTransactionUtils.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * @author Michael J. Simons
 */
class Neo4jConnectionHolderTest {

	@Nested
	class DatabaseNameComparision {

		@ParameterizedTest
		@CsvSource({
			",",
			"," + DEFAULT_DATABASE_NAME,
			DEFAULT_DATABASE_NAME + ",",
			DEFAULT_DATABASE_NAME + "," + DEFAULT_DATABASE_NAME,
			"a,a"
		})
		void nameComparisionShouldWorkForNamesTargetingTheSame(String name1, String name2) {

			assertThat(Neo4jConnectionHolder.namesMapToTheSameDatabase(name1, name2)).isTrue();
		}

		@ParameterizedTest
		@CsvSource({
			"a,",
			",b",
			"a,b"
		})
		void nameComparisionShouldWorkForNamesTargetingOther(String name1, String name2) {

			assertThat(Neo4jConnectionHolder.namesMapToTheSameDatabase(name1, name2)).isFalse();
		}
	}
}