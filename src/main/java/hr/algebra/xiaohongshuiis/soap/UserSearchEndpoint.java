package hr.algebra.xiaohongshuiis.soap;

import hr.algebra.xiaohongshuiis.service.XPathSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;

@Endpoint
public class UserSearchEndpoint {

    private static final String NAMESPACE_URI = "http://xiaohongshuiis.algebra.hr/soap";

    @Autowired
    private XPathSearchService xPathSearchService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchUsersRequest")
    @ResponsePayload
    public Element searchUsers(@RequestPayload Element request) throws Exception {

        String searchTerm = extractSearchTerm(request);
        List<String> results = xPathSearchService.searchUsersByTerm(searchTerm);

        return createResponse(results);
    }

    private String extractSearchTerm(Element request) {
        if (request.getElementsByTagNameNS(NAMESPACE_URI, "searchTerm").getLength() > 0) {
            return request.getElementsByTagNameNS(NAMESPACE_URI, "searchTerm").item(0).getTextContent();
        }
        if (request.getElementsByTagName("searchTerm").getLength() > 0) {
            return request.getElementsByTagName("searchTerm").item(0).getTextContent();
        }
        return "momo";
    }

    private Element createResponse(List<String> results) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document responseDoc = builder.newDocument();

        Element response = responseDoc.createElementNS(NAMESPACE_URI, "searchUsersResponse");
        responseDoc.appendChild(response);

        Element resultsElement = responseDoc.createElementNS(NAMESPACE_URI, "results");
        response.appendChild(resultsElement);

        for (String userXml : results) {
            Element userElement = responseDoc.createElementNS(NAMESPACE_URI, "user");
            userElement.setTextContent(userXml);
            resultsElement.appendChild(userElement);
        }

        return response;
    }
}