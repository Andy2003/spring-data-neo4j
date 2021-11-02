package org.springframework.data.neo4j.integration.issues.gh2421;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder(toBuilder = true)
public class NodeEntity extends BaseNodeEntity {
}
