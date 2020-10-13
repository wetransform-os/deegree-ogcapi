/*-
 * #%L
 * deegree-ogcapi-features - OGC API Features (OAF) implementation - Querying and modifying of geospatial data objects
 * %%
 * Copyright (C) 2019 - 2020 lat/lon GmbH, info@lat-lon.de, www.lat-lon.de
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.deegree.services.oaf.resource;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import org.deegree.services.oaf.openapi.OpenApiCreator;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_OPENAPI;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_OPENAPI_TYPE;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets/{datasetId}/api")
public class OpenApi {

    @Context
    private ServletContext servletContext;

    @Inject
    private OpenApiCreator openApiCreator;

    @GET
    @Produces({ APPLICATION_OPENAPI, APPLICATION_JSON })
    @Operation(operationId = "openApi", summary = "api documentation", description = "api documentation")
    @Tag(name = "Capabilities")
    public Response getOpenApiOpenApiJson(
                    @Context HttpHeaders headers,
                    @Context UriInfo uriInfo,
                    @PathParam("datasetId")
                                    String datasetId )
                    throws Exception {
        OpenAPI openApi = this.openApiCreator.createOpenApi( headers, datasetId );

        if ( openApi == null )
            return Response.status( 404 ).build();
        return Response.status( Response.Status.OK ).entity( Json.mapper().writeValueAsString( openApi ) ).build();
    }

    @GET
    @Produces({ TEXT_HTML })
    @Operation(hidden = true)
    public InputStream getOpenApiHtml() {
        return getFile( "index.html" );
    }

    @Operation(hidden = true)
    @Path("/{path: .+}")
    @GET
    public InputStream getFile(
                    @PathParam("path")
                                    String path ) {
        try {
            if ( path.startsWith( "api/" ) )
                path = path.substring( 4 );
            String base = servletContext.getRealPath( "/swagger-ui/" );
            File f = new File( String.format( "%s/%s", base, path ) );
            return new FileInputStream( f );
        } catch ( FileNotFoundException e ) {
            System.out.println( e.getLocalizedMessage() );
            return null;
        }
    }

}
