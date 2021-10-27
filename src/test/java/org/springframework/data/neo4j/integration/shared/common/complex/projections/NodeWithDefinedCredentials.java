package org.springframework.data.neo4j.integration.shared.common.complex.projections;


import java.util.Set;

import org.springframework.data.neo4j.integration.shared.common.complex.Credential;

/**
 * @author Andreas Berger
 */
public interface NodeWithDefinedCredentials {

	String getNodeId();

	String getName();

	Set<Credential> getDefinedCredentials();
}
