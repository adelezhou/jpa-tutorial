/**
 *
 */
package org.meri.jpa.simplest.route;

import org.apache.camel.Main;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;

/**
 *
 */
public class PersonRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
//		PropertiesComponent pc = new PropertiesComponent();
//		pc.setLocation("classpath:META-INF/pset.properties");
//		getContext().addComponent("properties", pc);

		from("jetty:http://localhost:1234?matchOnUriPrefix=true").id("org.meri.jpa.tutorial").log("${body}")
				.to("cxfbean:personRestBean");

		from("direct:marshaller").id("Person marshaller").marshal("jaxb");

		from("direct:unmarshaller").id("Person unmarshaller").unmarshal("jaxb");
	}

	/**
	 * A main() so we can easily run these routing rules in our IDE
	 */
	public static void main(String... args) throws Exception {
		Main.main(args);
	}
}