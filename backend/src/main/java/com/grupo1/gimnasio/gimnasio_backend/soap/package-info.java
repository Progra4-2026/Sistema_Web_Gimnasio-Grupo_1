/**
 * Servicio SOAP de cursos (comparación con los servicios REST de /api).
 * Las clases de este paquete son el mapeo JAXB de soap/cursos.xsd.
 */
@XmlSchema(namespace = CursoSoapEndpoint.NAMESPACE, elementFormDefault = XmlNsForm.QUALIFIED)
package com.grupo1.gimnasio.gimnasio_backend.soap;

import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
