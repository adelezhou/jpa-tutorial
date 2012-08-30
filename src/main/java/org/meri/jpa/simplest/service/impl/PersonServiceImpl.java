/**
 *
 */
package org.meri.jpa.simplest.service.impl;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.meri.jpa.simplest.entities.Person;
import org.meri.jpa.simplest.service.PersonService;

/**
 *
 */
public class PersonServiceImpl implements PersonService {

	@EndpointInject
	ProducerTemplate producer;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.nature.pset.service.PsetService#getProduct(int)
	 */
	public Response getPerson(int id) {
//		Injector injector = Guice.createInjector(new PsetModule());
//		ProductDao productDao = injector.getInstance(ProductDao.class);
//
//		Product p = productDao.getProduct(id);
		
		Person p = new Person();
		p.setId(Long.valueOf(id));
		p.setFirstName("homer");
		p.setLastName("simpson");
//
		Object xml = producer.requestBody("direct:marshaller", p);

//		return Response.ok(xml, MediaType.APPLICATION_XML_TYPE).build();
		return Response.ok(xml, MediaType.APPLICATION_XML_TYPE).build();
	}

	@Override
	public Response getFoo() {
		return Response.status(200).build();
	}

}
