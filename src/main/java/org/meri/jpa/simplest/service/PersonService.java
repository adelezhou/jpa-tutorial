/**
 *
 */
package org.meri.jpa.simplest.service;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 */
@Path("/myapp")
@Produces({ "application/json", "application/xml" })
public interface PersonService {

	@GET
	@Path("/person/{personId}")
	public Response getPerson(@PathParam("personId") int id);
	
	@GET
	@Path("/none")
	public Response getFoo();

}
