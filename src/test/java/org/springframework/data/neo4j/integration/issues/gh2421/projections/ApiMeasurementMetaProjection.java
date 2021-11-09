package org.springframework.data.neo4j.integration.issues.gh2421.projections;

import org.springframework.data.neo4j.integration.issues.gh2421.NodeType;

public interface ApiMeasurementMetaProjection extends BaseNodeFieldsProjection {
	BaseNodeFieldsProjection getParent();
	NodeType getNodeType();
}
