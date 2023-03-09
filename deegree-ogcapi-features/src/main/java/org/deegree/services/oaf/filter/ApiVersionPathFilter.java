package org.deegree.services.oaf.filter;

import java.io.IOException;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.deegree.services.oaf.openapi.OpenApiCreator;

/**
 * Filter an optional version path segment (e.g. v1) based on the major version
 * of the API.
 * 
 * Use highest priority so other filters don't need to deal with the optional
 * segment.
 * 
 * @author Kapil Agnihotri
 */
@Priority(1)
@Provider
@PreMatching
public class ApiVersionPathFilter implements ContainerRequestFilter {

	public static String determineVersionSegment() {
		// extract major version from API version
		String v = OpenApiCreator.VERSION;
		int dotIndex = v.indexOf('.');
		String major;
		if (dotIndex == 0) {
			major = "0";
		} else if (dotIndex > 0) {
			major = v.substring(0, dotIndex);
		} else {
			major = v;
		}
		return "v" + major;
	}

	public static final String VERSION_SEGMENT = determineVersionSegment();

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		UriInfo orgUri = requestContext.getUriInfo();
		List<PathSegment> segments = orgUri.getPathSegments();

		// if the relative path is /datasets/{dataset}/{versionSegment}/* skip the
		// version segment
		if (segments.size() >= 3 && VERSION_SEGMENT.equals(segments.get(2).getPath())
				&& "datasets".equals(segments.get(0).getPath())) {
			StringBuilder newPath = new StringBuilder(orgUri.getBaseUri().getPath());
			newPath.append("datasets/");
			newPath.append(segments.get(1).getPath());
			for (int i = 3; i < segments.size(); i++) {
				newPath.append("/");
				newPath.append(segments.get(i).getPath());
			}
			requestContext.setRequestUri(orgUri.getBaseUri(),
					orgUri.getRequestUriBuilder().replacePath(newPath.toString()).build());
		}
	}

}