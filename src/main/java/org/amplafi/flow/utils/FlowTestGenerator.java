package org.amplafi.flow.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FlowTestGenerator implements Iterable<GeneralFlowRequest> {
    private URI uri;
    // Set of all GeneralFlowRequests that fail;
    private List<GeneralFlowRequest> failedGeneralFlowRequest;

    public FlowTestGenerator(URI uri) {
        this.uri = uri;
        failedGeneralFlowRequest = new ArrayList<GeneralFlowRequest>();
    }

    // Return a Iterator over a Set of all GeneralFlowRequests that fail.
    public Iterator<GeneralFlowRequest> iterator() {
        return new Iterator<GeneralFlowRequest>() {

            public boolean hasNext() {
                // TODO Auto-generated method stub
                return false;
            }

            public GeneralFlowRequest next() {
                // TODO Auto-generated method stub
                return null;
            }

            public void remove() {
                // TODO Auto-generated method stub

            }

        };
    }

    public List<GeneralFlowRequest> getFailedGeneralFlowRequests() {
        return failedGeneralFlowRequest;
    }

    public void addToFailedGeneralFLowRequests(
            GeneralFlowRequest generalFlowRequest) {
        failedGeneralFlowRequest.add(generalFlowRequest);
    }

}
