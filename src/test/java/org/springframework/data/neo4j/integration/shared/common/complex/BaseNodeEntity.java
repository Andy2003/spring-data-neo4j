package org.springframework.data.neo4j.integration.shared.common.complex;

import lombok.*;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

/**
 * @author Andreas Berger
 */
@Node
@NonFinal
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuperBuilder(toBuilder = true)
public class BaseNodeEntity {

	@Id
	@GeneratedValue(UUIDStringGenerator.class)
	@EqualsAndHashCode.Include
	private String nodeId;

	private String name;


	@Override
	public String toString() {
		return getClass().getSimpleName() + " - " + getName() + " (" + getNodeId() + ")";
	}
}
