package org.deegree.services.oaf.resource;


import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;


/**
 * Class to add API version to all the response headers in the below format
 * API-Version: 1.0.2
 * 
 * @author Kapil Agnihotri
 *
 */
@Provider
public class ApiVersionResponseFilter implements ContainerResponseFilter {
	
	@Inject
    private DeegreeWorkspaceInitializer deegreeWorkspaceInitializer;
	
	
	// Constant for API-Version
	private static final String API_VERSION_CONSTANT = "API-Version";
	
	// API version information
	private static final String API_VERSION = "1.0.2";
	
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
        throws IOException {
    	
    	if(deegreeWorkspaceInitializer.isAddApiVersionToHeader()) {
            responseContext.getHeaders().add(API_VERSION_CONSTANT, API_VERSION);
    	}
    }
}
