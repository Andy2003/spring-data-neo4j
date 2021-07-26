package org.springframework.data.neo4j.integration.shared.complex;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.*;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node
@Value
@NonFinal
@Getter
@AllArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@SuperBuilder(toBuilder = true)
public abstract class BaseNodeEntity {
	@DynamicLabels
	Set<String> labels;

	@Id
	@GeneratedValue(GeneratedValue.UUIDGenerator.class)
	String nodeId;

	@Relationship(type = "CHILD_OF", direction = OUTGOING)
	BaseNodeEntity childOf;

	@Relationship(type = "HAS_TYPE", direction = OUTGOING)
	NodeType nodeType;
}
