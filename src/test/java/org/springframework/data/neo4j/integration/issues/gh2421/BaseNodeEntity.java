package org.springframework.data.neo4j.integration.issues.gh2421;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuperBuilder(toBuilder = true)
abstract public class BaseNodeEntity {

	@Id
	@GeneratedValue(UUIDStringGenerator.class)
	@EqualsAndHashCode.Include
	private String nodeId;

	@Relationship(type = "CHILD_OF", direction = OUTGOING)
	private NodeEntity parent;

	@Relationship(type = "HAS_TYPE", direction = OUTGOING)
	private NodeType nodeType;
}
