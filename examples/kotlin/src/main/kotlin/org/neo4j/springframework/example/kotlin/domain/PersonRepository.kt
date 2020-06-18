/*
 * Copyright (c) 2019-2020 "Neo4j,"
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
package org.neo4j.springframework.example.kotlin.domain

import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository
import org.neo4j.springframework.data.repository.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author Gerrit Meier
 */
interface PersonRepository : ReactiveNeo4jRepository<PersonEntity, String> {
    fun findByName(name: String): Mono<PersonEntity>

    @Query("MATCH (am:Movie)<-[ai:ACTED_IN]-(p:Person)-[d:DIRECTED]->(dm:Movie) return p, collect(ai), collect(d), collect(am), collect(dm)")
    fun getPersonsWhoActAndDirect(): Flux<PersonEntity>
}
