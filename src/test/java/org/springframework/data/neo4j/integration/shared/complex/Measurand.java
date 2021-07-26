package org.springframework.data.neo4j.integration.shared.complex;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;


@Node
@Value
@With
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Measurand {
	@Id
	String measurandId;
}
