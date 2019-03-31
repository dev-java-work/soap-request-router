/**
 * Author Rajarshi Ray
 */


package com.ssstsar.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class HomeController {

	
	
	/*
	 * Rest api method to capture the POST request coming for SOAP call
	 */
	@RequestMapping(value = "/**", method = { RequestMethod.POST }, produces = "application/xml")
	@ResponseBody
	public String mirrorSoapCall(@RequestBody String body, HttpMethod method, HttpServletRequest request)
			throws URISyntaxException, SOAPException, IOException {
		int port = 0;

		try {

			if (body != null) {
				if (body.contains("GetCourseDetailsRequest")) {

					port = 8082;
				}
				if (body.contains("DeleteCourseDetailsRequest")) {
					port = 8083;

				}
			}

			HttpHeaders header = new HttpHeaders();
			header.set(HttpHeaders.CONTENT_TYPE, "text/xml");
			header.set("Accept", "text/xml");
			HttpEntity entity = new HttpEntity<>(body, header);
			
			//Http Header is important as header contains the Accept header 

			RestTemplate restTemplate = new RestTemplate();

			URI uri = new URI("http", null, "localhost", port, "/soapws", request.getQueryString(), null);
			ResponseEntity<String> responseEntity = restTemplate.exchange(uri, method, entity, String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
	
	
/*
 * This method is to route the GET call to hit the WSDL
 */
	@RequestMapping(value = "/**", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public String mirrorWSDLt(HttpMethod method, HttpServletRequest request) throws URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();

		URI uri = new URI("http", null, "localhost", 8082, "/soapws/courses.wsdl", request.getQueryString(), null);

		ResponseEntity<String> responseEntity = restTemplate.exchange(uri, method, null, String.class);

		return responseEntity.getBody();
	}

}
