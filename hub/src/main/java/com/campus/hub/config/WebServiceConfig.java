package com.campus.hub.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurer;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;
import java.util.Properties;

@EnableWs
@Configuration
public class WebServiceConfig implements WsConfigurer {  // Fixed: implements instead of extends

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet>
    messageDispatcherServlet(ApplicationContext applicationContext) {

        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        servlet.setTransformSchemaLocations(true);

        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "campusService")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema campusSchema) {
        DefaultWsdl11Definition wsdlDefinition = new DefaultWsdl11Definition();
        wsdlDefinition.setPortTypeName("CampusServicePort");
        wsdlDefinition.setLocationUri("/ws");
        wsdlDefinition.setTargetNamespace("http://campus.hub.com/soap");
        wsdlDefinition.setSchema(campusSchema);
        wsdlDefinition.setCreateSoap11Binding(true);
        wsdlDefinition.setCreateSoap12Binding(true);
        wsdlDefinition.setServiceName("CampusService");

        return wsdlDefinition;
    }

    @Bean
    public XsdSchema campusSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/ServiceRequest.xsd"));
    }

    @Bean
    public PayloadValidatingInterceptor payloadValidatingInterceptor() {
        PayloadValidatingInterceptor interceptor = new PayloadValidatingInterceptor();
        interceptor.setSchema(new ClassPathResource("xsd/ServiceRequest.xsd"));
        interceptor.setValidateRequest(true);
        interceptor.setValidateResponse(true);
        interceptor.setXsdSchema(campusSchema());
        return interceptor;
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(payloadValidatingInterceptor());
    }

    @Bean
    public SoapFaultMappingExceptionResolver exceptionResolver() {
        SoapFaultMappingExceptionResolver exceptionResolver =
                new SoapFaultMappingExceptionResolver();

        SoapFaultDefinition faultDefinition = new SoapFaultDefinition();
        faultDefinition.setFaultCode(SoapFaultDefinition.SERVER);
        exceptionResolver.setDefaultFault(faultDefinition);

        Properties errorMappings = new Properties();
        errorMappings.setProperty(Exception.class.getName(),
                SoapFaultDefinition.SERVER.toString());

        exceptionResolver.setExceptionMappings(errorMappings);
        exceptionResolver.setOrder(1);

        return exceptionResolver;
    }
}