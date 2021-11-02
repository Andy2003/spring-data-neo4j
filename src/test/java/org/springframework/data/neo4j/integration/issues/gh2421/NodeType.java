package org.springframework.data.neo4j.integration.issues.gh2421;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Value
@AllArgsConstructor
@EqualsAndHashCode
@Immutable
public class NodeType {
	@Id
	String nodeTypeId;
}
