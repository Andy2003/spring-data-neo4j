package org.springframework.data.neo4j.integration.issues.gh2421;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.integration.issues.gh2421.projections.BaseNodeFieldsProjection;

@Node
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder(toBuilder = true)
public class MeasurementMeta extends BaseNodeEntity implements BaseNodeFieldsProjection {
}
