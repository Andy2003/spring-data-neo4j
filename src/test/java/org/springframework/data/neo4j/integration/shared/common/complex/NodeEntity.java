package org.springframework.data.neo4j.integration.shared.common.complex;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.integration.shared.common.complex.projections.NodeWithDefinedCredentials;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

/**
 * @author Andreas Berger
 */
@Node
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder(toBuilder = true)
public class NodeEntity extends BaseNodeEntity implements NodeWithDefinedCredentials {

	@JsonIgnore
	@Relationship(type = "CHILD_OF", direction = INCOMING)
	private Set<BaseNodeEntity> children;

	@Relationship(type = "HAS_CREDENTIAL")
	private Set<Credential> definedCredentials;

	@Override
	public String toString() {
		return super.toString();
	}
}
