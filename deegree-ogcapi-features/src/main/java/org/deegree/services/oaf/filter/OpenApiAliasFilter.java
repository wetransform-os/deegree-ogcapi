package org.deegree.services.oaf.filter;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 * Filter that provides OpenApi endpoints under the alias "openapi" in addition to the original "api".
 */
@Provider
@PreMatching
public class OpenApiAliasFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		UriInfo orgUri = requestContext.getUriInfo();
		List<PathSegment> segments = orgUri.getPathSegments();
		
		// if the relative path is /datasets/{dataset}/openapi* replace "openapi" with "api"
		if (segments.size() == 3 && segments.get(2).getPath().startsWith("openapi") && "datasets".equals(segments.get(0).getPath())) {
			StringBuilder newPath = new StringBuilder(orgUri.getBaseUri().getPath());
			newPath.append("datasets/");
			newPath.append(segments.get(1).getPath());
			newPath.append("/");
			newPath.append(segments.get(2).getPath().replace("openapi", "api"));
			requestContext.setRequestUri(orgUri.getBaseUri(), orgUri.getRequestUriBuilder().replacePath(newPath.toString()).build());
		}
	}

}
