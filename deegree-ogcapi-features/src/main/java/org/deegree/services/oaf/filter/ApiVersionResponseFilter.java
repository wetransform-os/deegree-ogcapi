package org.deegree.services.oaf.filter;


import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.deegree.services.oaf.openapi.OpenApiCreator;


/**
 * Class to add API version to response headers.
 * 
 * @author Kapil Agnihotri
 */
@Provider
public class ApiVersionResponseFilter implements ContainerResponseFilter {

	// Whether to add API version to the header or not
	private boolean addApiVersionToHeader = true;

	// Constant for API-Version
	private static final String API_VERSION_CONSTANT = "API-Version";

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		if (addApiVersionToHeader) {
			responseContext.getHeaders().add(API_VERSION_CONSTANT, OpenApiCreator.VERSION);
		}
	}

}
