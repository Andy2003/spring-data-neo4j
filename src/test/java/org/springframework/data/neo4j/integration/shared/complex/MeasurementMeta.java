package org.springframework.data.neo4j.integration.shared.complex;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@org.springframework.data.neo4j.core.schema.Node
@Value
@NonFinal
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public abstract class MeasurementMeta extends BaseNodeEntity {

	@Relationship(type = "BELONGS_TO")
	Node company;

	@Relationship(type = "IS_MEASURED_BY", direction = INCOMING)
	Set<DataPoint> dataPoints;

	public String getMeasurementId() {
		return getNodeId();
	}

	@PersistenceConstructor
	public MeasurementMeta(
			Set<String> labels,
			String nodeId,
			Node childOf,
			NodeType nodeType,
			Node company,
			Set<DataPoint> dataPoints
	)
	{
		super(labels, nodeId, childOf, nodeType);
		this.company = company;
		this.dataPoints = dataPoints;
	}
}
