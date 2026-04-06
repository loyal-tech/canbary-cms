package com.adopt.apigw.soap;//package com.adopt.apigw.soap;
//
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.ws.config.annotation.EnableWs;
//import org.springframework.ws.config.annotation.WsConfigurerAdapter;
//import org.springframework.ws.server.EndpointInterceptor;
//import org.springframework.ws.transport.http.MessageDispatcherServlet;
//import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
//import org.springframework.xml.xsd.SimpleXsdSchema;
//import org.springframework.xml.xsd.XsdSchema;
//
//import java.util.List;
//
//@EnableWs
//@Configuration
//public class Config extends WsConfigurerAdapter
//{
//    @Bean
//    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext)
//    {
//        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
//        servlet.setApplicationContext(applicationContext);
//        servlet.setTransformWsdlLocations(true);
//        return new ServletRegistrationBean(servlet, "/debitcompleted");
//    }
//
//    @Bean(name = "debitCompletedDetailsWsdl")
//    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema)
//    {
//        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
//        wsdl11Definition.setPortTypeName("DebitCompletedDetailsPort");
//        wsdl11Definition.setLocationUri("/debitcompleted22222");
//        wsdl11Definition.setTargetNamespace("http://www.ericsson.com/em/emm/financial/v1_2");
//        wsdl11Definition.setSchema(countriesSchema);
//        return wsdl11Definition;
//    }
//
//    @Bean
//    public XsdSchema countriesSchema()
//    {
//        return new SimpleXsdSchema(new ClassPathResource("debitcompleted.xsd"));
//    }
//
//
//}
