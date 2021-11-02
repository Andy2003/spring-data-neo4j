package org.springframework.data.neo4j.integration.issues.gh2421.projections;


public interface BaseNodeWithParent {

	BaseNodeFieldsProjection getParent();
}
