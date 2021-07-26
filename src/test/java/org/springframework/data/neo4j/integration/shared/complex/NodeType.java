package org.springframework.data.neo4j.integration.shared.complex;

import lombok.Value;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Value
@Node
public class NodeType {

	@Id
	String nodeTypeId;
}
