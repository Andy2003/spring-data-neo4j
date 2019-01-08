/*
 * Copyright (c)  [2011-2019] "Pivotal Software, Inc." / "Neo Technology" / "Graph Aware Ltd."
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with
 * separate copyright notices and license terms. Your use of the source
 * code for these subcomponents is subject to the terms and
 * conditions of the subcomponent's license, as noted in the LICENSE file.
 *
 */

package org.springframework.data.neo4j.examples.movies.domain.queryresult;

import org.neo4j.ogm.annotation.Property;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.examples.movies.repo.UserRepository;

/**
 * Example POJO {@link QueryResult} to test mapping onto arbitrary objects, even for properties that are on the nodes
 * but not in the entity classes.
 *
 * @see UserRepository
 * @author Adam George
 * @author Luanne Misquitta
 */
@QueryResult
public class UserQueryResult {

	private Long id;
	Long userId;
	private String userName;

	@Property(name = "user.age") private int age;

	UserQueryResult() {
		// default constructor for OGM
	}

	public UserQueryResult(String name, int age) {
		this.userName = name;
		this.age = age;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 23;
		int result = 1;
		result = prime * result + age;
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserQueryResult other = (UserQueryResult) obj;
		if (age != other.age)
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserQueryResult{" +
				"id=" + id +
				", userId=" + userId +
				", userName='" + userName + '\'' +
				", age=" + age +
				'}';
	}
}
