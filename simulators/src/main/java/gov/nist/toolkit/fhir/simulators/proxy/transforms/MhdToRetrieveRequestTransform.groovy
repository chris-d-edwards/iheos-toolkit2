package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.simulators.mhd.RetrieveRequestTranslator
import gov.nist.toolkit.fhir.simulators.proxy.util.BinaryPartSpec
import gov.nist.toolkit.fhir.simulators.proxy.util.ContentRequestTransform
import gov.nist.toolkit.fhir.simulators.proxy.util.MtomContentTypeGenerator
import gov.nist.toolkit.simcommon.server.SimEndpoint
import gov.nist.toolkit.simcoresupport.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.simcoresupport.proxy.util.ReturnableErrorException
import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase
import gov.nist.toolkit.utilities.io.Io
import org.apache.http.Header
import org.apache.http.HttpRequest
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.log4j.Logger

class MhdToRetrieveRequestTransform implements ContentRequestTransform {
    static private final Logger logger = Logger.getLogger(MhdToRetrieveRequestTransform)

    @Override
    HttpRequest run(SimProxyBase base, BasicHttpEntityEnclosingRequest request) {
        logger.info('Running MhdToRetrieveRequestTransform')
        String repositoryUniqueId = base.getTargetRepositoryUniqueId()
        assert repositoryUniqueId
        SimEndpoint endpoint = base.endpoint
        assert endpoint

        Map model = [:]
        model[endpoint.id] = [repositoryUniqueId]
        String retrieveRequestXml = RetrieveRequestTranslator.toXml(model)

        List<BinaryPartSpec> parts = []
        // TODO need real To address  here
        // TODO repositoryUniqueIds generated by REP sim are not valid oids
        def partContent = buildPartBody(base.targetEndpoint, retrieveRequestXml)
        def contentId = 'CIDxxx'
        parts << new BinaryPartSpec('application/xop+xml; charset=UTF-8; type="application/soap+xml"', partContent.bytes, contentId)
        BasicHttpEntity entity = new BasicHttpEntity()
        byte[] body = MtomContentTypeGenerator.buildBody(parts)
        Header targetContentTypeHeader = MtomContentTypeGenerator.buildHeader(TransactionType.RETRIEVE.requestAction, contentId)
        request.setHeader(targetContentTypeHeader)
        entity.setContent(Io.bytesToInputStream(body))
        request.setEntity(entity)
        return request
    }

    @Override
    HttpRequest run(SimProxyBase base, HttpRequest request) throws ReturnableErrorException {
        throw new SimProxyTransformException("MhdToRetrieveRequestTransform cannot handle requests of type ${request.getClass().getName() } ")
    }

    String buildPartBody(String toAddress, String body) {
        def header1 = header.replace('TO_ADDRESS', toAddress)
        header1 + body + trailer
    }

    def header = '''
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
   <soapenv:Header xmlns:wsa="http://www.w3.org/2005/08/addressing">
      <wsa:To soapenv:mustUnderstand="true">TO_ADDRESS</wsa:To>
      <wsa:MessageID soapenv:mustUnderstand="true">urn:uuid:B163C7B266257EAA091504010552642</wsa:MessageID>
      <wsa:Action soapenv:mustUnderstand="true">urn:ihe:iti:2007:RetrieveDocumentSet</wsa:Action>
   </soapenv:Header>
   <soapenv:Body>
'''
    def trailer = '''
   </soapenv:Body>
</soapenv:Envelope>
'''

}
