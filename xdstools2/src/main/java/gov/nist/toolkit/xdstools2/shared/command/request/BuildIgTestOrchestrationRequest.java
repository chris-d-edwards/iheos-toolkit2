package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.IgOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class BuildIgTestOrchestrationRequest extends CommandContext{
    private IgOrchestrationRequest request;

    public BuildIgTestOrchestrationRequest(){}
    public BuildIgTestOrchestrationRequest(CommandContext context, IgOrchestrationRequest request){
        copyFrom(context);
        this.request=request;
    }

    public IgOrchestrationRequest getIgOrchestrationRequest() {
        return request;
    }

    public void setIgOrchestrationRequest(IgOrchestrationRequest request) {
        this.request = request;
    }
}
