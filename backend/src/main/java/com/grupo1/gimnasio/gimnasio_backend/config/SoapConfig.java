package com.grupo1.gimnasio.gimnasio_backend.config;

import com.grupo1.gimnasio.gimnasio_backend.soap.CursoSoapEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * Publica el servicio SOAP de cursos. Spring Boot registra el servlet de Spring Web Services
 * en /ws (spring.webservices.path) y este bean genera el WSDL a partir del XSD:
 *   WSDL: http://localhost:8080/ws/cursos.wsdl
 */
@Configuration
public class SoapConfig {

    @Bean
    public XsdSchema cursosSchema() {
        return new SimpleXsdSchema(new ClassPathResource("soap/cursos.xsd"));
    }

    /** El nombre del bean ("cursos") define la URL del WSDL: /ws/cursos.wsdl. */
    @Bean(name = "cursos")
    public DefaultWsdl11Definition cursosWsdl(XsdSchema cursosSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("CursosPort");
        wsdl.setServiceName("CursosService");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace(CursoSoapEndpoint.NAMESPACE);
        wsdl.setSchema(cursosSchema);
        return wsdl;
    }
}
