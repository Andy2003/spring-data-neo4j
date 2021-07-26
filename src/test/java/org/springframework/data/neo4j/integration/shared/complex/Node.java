package org.springframework.data.neo4j.integration.shared.complex;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@org.springframework.data.neo4j.core.schema.Node
@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class Node extends BaseNodeEntity {
	@Relationship(type = "CHILD_OF", direction = INCOMING)
	protected Set<BaseNodeEntity> childNodes;

	@PersistenceConstructor
	public Node(
			Set<String> labels,
			String nodeId,
			BaseNodeEntity childOf,
			NodeType nodeType,
			Set<BaseNodeEntity> childNodes

	)
	{
		super(labels, nodeId, childOf, nodeType);
		this.childNodes = childNodes;
	}
}
