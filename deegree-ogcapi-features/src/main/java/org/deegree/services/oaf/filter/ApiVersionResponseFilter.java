package org.deegree.services.oaf.filter;


import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.deegree.commons.utils.TunableParameter;
import org.deegree.services.oaf.openapi.OpenApiCreator;


/**
 * Class to add API version to response headers.
 * 
 * @author Kapil Agnihotri
 */
@Provider
public class ApiVersionResponseFilter implements ContainerResponseFilter {

	/**
	 * Name for parameter that allows enabling returning the API version as response header. 
	 */
	public static final String PARAMETER_ENABLE_VERSION_HEADER = "deegree.oaf.openapi.version_response_header";
	
	private boolean addApiVersionToHeader = TunableParameter.get(PARAMETER_ENABLE_VERSION_HEADER, false);

	/**
	 * Name for response header with API version.
	 */
	public static final String HEADER_API_VERSION = "API-Version";

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		if (addApiVersionToHeader) {
			responseContext.getHeaders().add(HEADER_API_VERSION, OpenApiCreator.VERSION);
		}
	}

}
