package org.springframework.data.neo4j.integration.shared.complex;

import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Value
@EqualsAndHashCode
public class DataPoint {

	@TargetNode
	Measurand measurand;

	Boolean manual;
}
