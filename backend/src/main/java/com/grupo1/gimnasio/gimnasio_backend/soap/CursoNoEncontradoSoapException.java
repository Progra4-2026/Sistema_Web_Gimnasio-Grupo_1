package com.grupo1.gimnasio.gimnasio_backend.soap;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/** Se responde como SOAP Fault de tipo Client (equivale al 404 de REST), sin trazas del servidor. */
@SoapFault(faultCode = FaultCode.CLIENT)
public class CursoNoEncontradoSoapException extends RuntimeException {

    public CursoNoEncontradoSoapException(String mensaje) {
        super(mensaje);
    }
}
