package org.finra.esched.service.star.common;

import com.sun.xml.internal.ws.client.BindingProviderProperties;
import org.finra.star.matterservice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatterServiceClient {
    private String wsdlLocation;

    private static final Logger log = LoggerFactory.getLogger(MatterServiceClient.class);

    public static JAXBElement<Boolean> matterIsPreScheduleMatter(Boolean value) {
        ObjectFactory objectFactory = new ObjectFactory();
        return objectFactory.createCreateMatterIsPreScheduleMatter(value);
    }

    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    public MatterServiceResponse processRequest(MatterServiceRequest req, String userIdFromUI) throws Exception {
        log.debug("::enter processRequest");
        MatterServiceResponse value;
        MatterServiceBindingSoap port;
        log.debug("******************************PUBLISHING {}", userIdFromUI);
        port = createServiceClient(userIdFromUI);


        try {
            log.debug("binding.processRequest");
            value = port.processRequest(req);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.debug("::exit processRequest");
        return value;

    }

    private MatterServiceBindingSoap createServiceClient(String userName) {
        MatterServiceBindingSoap port;
        try {
            MatterService matterService = new MatterService(new URL(wsdlLocation));
            port = matterService.getMatterServiceBindingSoap();
            Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
            requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT, 120000); // Timeout in millis
            requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, 10000); // Timeout in millis
            addUserInfoHandler(userName, (BindingProvider) port);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return port;
    }

    private void addUserInfoHandler(String userName, BindingProvider port) {
        Binding binding = port.getBinding();
        List<Handler> handlers = new ArrayList<>();
        ObjectFactory objectFactory = new ObjectFactory();
        UserInfoHeaderType userInfoHeader = objectFactory.createUserInfoHeaderType();
        userInfoHeader.setExternalApplicationName("EW");
        userInfoHeader.setUserName(userName);
        final JAXBElement<UserInfoHeaderType> header = objectFactory.createUserInfoHeader(userInfoHeader);
        handlers.add(new SOAPHandler<SOAPMessageContext>() {
            @Override
            public boolean handleMessage(SOAPMessageContext context) {
                try {
                    // checking whether handled message is outbound one as per Martin Strauss answer
                    final Boolean outbound = (Boolean) context.get("javax.xml.ws.handler.message.outbound");
                    if (outbound != null && outbound) {
                        // obtaining marshaller which should marshal instance to xml
                        final Marshaller marshaller = JAXBContext.newInstance(UserInfoHeaderType.class).createMarshaller();
                        // adding header because otherwise it's null
                        SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                        if (null != envelope.getHeader()) {
                            envelope.getHeader().detachNode();
                        }
                        final SOAPHeader soapHeader = envelope.addHeader();
                        // marshalling instance (appending) to SOAP header's xml node
                        marshaller.marshal(header, soapHeader);
                    }
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }

            @Override
            public boolean handleFault(SOAPMessageContext context) {
                return false;
            }

            @Override
            public void close(MessageContext context) {
            }

            @Override
            public Set<QName> getHeaders() {
                return null;
            }
        });
        binding.setHandlerChain(handlers);
    }

    public String respToXml(MatterServiceResponse response) {
        try {
            String xml;
            StringWriter outStr = new StringWriter();
            Marshaller marshaller = JAXBContext.newInstance(MatterServiceResponse.class).createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(response, outStr);
            xml = outStr.toString();
            xml = xml.replaceAll(" xmlns[:=].*?\".*?\"", "").replaceAll(" xsi:type=\".*?\"", "").replaceAll(" xsi:nil=\"true\"", "");
            log.debug("response = {}", xml);
            return xml;
        } catch (JAXBException e) {
            log.error("Failed to serialize MatterServiceRequest", e);
            return "Error: " + e.getMessage();
        }
    }

    public String reqToXml(MatterServiceRequest request) {
        try {
            String xml;
            StringWriter outStr = new StringWriter();
            Marshaller marshaller = JAXBContext.newInstance(MatterServiceRequest.class).createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(request, outStr);
            xml = outStr.toString();
            xml = xml.replaceAll(" xmlns[:=].*?\".*?\"", "").replaceAll(" xsi:type=\".*?\"", "").replaceAll(" xsi:nil=\"true\"", "");
            log.debug("request = {}", xml);
            return xml;
        } catch (JAXBException e) {
            log.error("Failed to serialize MatterServiceRequest", e);
            return "Error: " + e.getMessage();
        }
    }
}
