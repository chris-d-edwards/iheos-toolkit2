package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.services.client.PifType
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import groovy.transform.TypeChecked

/**
 * Send a Patient Identify Feed.  This relies heavily on OrchestrationProperties. The feed is only sent if
 * orchProps.updated() is true.
 */
@TypeChecked
class PifSender {
    private OrchestrationProperties orchProps
//    AbstractOrchestrationRequest request
    private SiteSpec regSite
    private ToolkitApi api
    private Util util
    private TestSession testSession

    public PifSender(ToolkitApi api, TestSession testSession, SiteSpec regSite, OrchestrationProperties orchProps) {
        this.api = api
        this.testSession = testSession
        this.regSite = regSite
        this.orchProps = orchProps
        util = new Util(api)
    }

    def send(PifType pifType, Map<String, TestInstanceManager> pidNameMap) {
        if (orchProps.updated()) {
            if (pifType == PifType.V2) {
                // register patient ids with registry
                pidNameMap.each { String key, TestInstanceManager value ->
                    String pidId = key
                    TestInstanceManager testInstanceManager = value
                    try {
                        Pid pid = PidBuilder.createPid(orchProps.getProperty(pidId))
                        util.submit(testSession, regSite, testInstanceManager.testInstance, 'pif', pid, null)
                    }
                    catch (Exception e) {
//                        testInstanceManager.messageItem.setMessage("V2 Patient Identity Feed to " + regSite.name + " failed\n" + ExceptionUtil.exception_details(e));
                        testInstanceManager.messageItem.setSuccess(false);
                    }
                }
            }
        } else {  // pass back PIF status
            pidNameMap.each { String key, TestInstanceManager value ->
                TestInstanceManager testInstanceManager = value
                testInstanceManager.messageItem.setSuccess(api.getTestLogs(testInstanceManager.testInstance).isSuccess())
            }
        }
    }
}
