package org.springframework.data.neo4j.integration.issues.gh2421;

import lombok.*;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;
import org.springframework.data.neo4j.integration.issues.gh2421.projections.ApiBaseNodeProjection;
import org.springframework.data.neo4j.integration.issues.gh2421.projections.BaseNodeFieldsProjection;
import org.springframework.data.neo4j.integration.issues.gh2421.projections.BaseNodeWithNodeType;
import org.springframework.data.neo4j.integration.issues.gh2421.projections.BaseNodeWithParent;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node
@NonFinal
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuperBuilder(toBuilder = true)
public class BaseNodeEntity implements
		BaseNodeFieldsProjection,
		BaseNodeWithParent,
		BaseNodeWithNodeType,

		ApiBaseNodeProjection {

	@Id
	@GeneratedValue(UUIDStringGenerator.class)
	@EqualsAndHashCode.Include
	private String nodeId;

	@Relationship(type = "CHILD_OF", direction = OUTGOING)
	private NodeEntity parent;

	@Relationship(type = "HAS_TYPE", direction = OUTGOING)
	private NodeType nodeType;
}
