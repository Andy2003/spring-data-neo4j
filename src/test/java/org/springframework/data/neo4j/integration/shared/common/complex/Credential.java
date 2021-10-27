package org.springframework.data.neo4j.integration.shared.common.complex;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

/**
 * @author Andreas Berger
 */
@Node
@Value
@With
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Immutable
@Builder(toBuilder = true)
public class Credential {

	@JsonIgnore
	@Id
	@GeneratedValue(UUIDStringGenerator.class)
	@EqualsAndHashCode.Include
	String id;

	String name;
}
