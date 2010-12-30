package org.amplafi.flow.definitions;

import java.net.URI;
import java.util.Map;

/**
 * Retrieves the available flow definitions from the server. Only the flows that the current user has accessed to will be retrieved.
 * The flow definitions should be refreshed after login/logout.
 * @author patmoore
 *
 */
public class ClientSideDefinitionSource implements DefinitionSource<FlowDefinition> {

	private URI amplafiServerUri;
	ClientSideDefinitionSource(URI amplafiServerUri) {
		this.amplafiServerUri = amplafiServerUri;
	}
	public FlowDefinition getFlowDefinition(String flowTypeName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, FlowDefinition> getFlowDefinitions() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isFlowDefined(String flowTypeName) {
		// TODO Auto-generated method stub
		return false;
	}

}
