package org.amplafi.flow.auth;

/**
 * Some common flow request parameters.
 *
 * To KOSTYA :
 *
 * A quick search shows these strings are defined in FlowLauncher. (violating the DRY principle).
 * 1) What happens if someone changes FlowLauncher constants?
 * 2) If they notice these constants should these change? or not -- the connection between the two has been broken.
 *
 * Please replace the constants in FlowLauncher with these enums.

 * @author Konstantin Burov
 *
 */
public enum StandardFlowRequestParameters {

	flowClientUserId,

	flow,

	fsAdvanceTo,

	fsCompleteFlow,

	flowState;
}
