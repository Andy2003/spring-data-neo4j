package org.springframework.data.neo4j.integration.issues.gh2421;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder(toBuilder = true)
public class MeasurementMeta extends BaseNodeEntity {

	@Relationship(type = "WEIGHTS", direction = OUTGOING)
	private MeasurementMeta baseMeasurement;
}
