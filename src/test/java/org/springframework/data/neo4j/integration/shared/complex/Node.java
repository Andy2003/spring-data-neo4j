package org.springframework.data.neo4j.integration.shared.complex;

import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@org.springframework.data.neo4j.core.schema.Node
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Node extends BaseNodeEntity {
	@Relationship(type = "CHILD_OF", direction = INCOMING)
	protected Set<BaseNodeEntity> childNodes;
}
