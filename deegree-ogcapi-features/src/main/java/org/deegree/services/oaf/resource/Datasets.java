package org.deegree.services.oaf.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import org.deegree.services.oaf.RequestFormat;
import org.deegree.services.oaf.domain.dataset.Dataset;
import org.deegree.services.oaf.exceptions.InvalidParameterValue;
import org.deegree.services.oaf.link.Link;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DeegreeWorkspaceInitializer;
import org.deegree.services.oaf.workspace.configuration.OafDatasetConfiguration;
import org.deegree.services.oaf.workspace.configuration.OafDatasets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.deegree.services.oaf.OgcApiFeaturesMediaType.APPLICATION_GEOJSON;
import static org.deegree.services.oaf.RequestFormat.HTML;
import static org.deegree.services.oaf.RequestFormat.JSON;
import static org.deegree.services.oaf.RequestFormat.byFormatParameter;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@Path("/datasets")
public class Datasets {

    @GET
    @Produces({ APPLICATION_JSON })
    public Response datasetsJson(
                    @Context UriInfo uriInfo,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f") String format )
                    throws InvalidParameterValue {
        return datasets( uriInfo, format, JSON );
    }

    @GET
    @Produces({ TEXT_HTML })
    @Operation(hidden = true)
    public Response datasetsHtml(
                    @Context UriInfo uriInfo,
                    @Parameter(description = "The request output format.", style = ParameterStyle.FORM)
                    @QueryParam("f") String format )
                    throws InvalidParameterValue {
        return datasets( uriInfo, format, HTML );
    }

    private Response datasets( UriInfo uriInfo, String formatParamValue,
                               RequestFormat defaultFormat )
                    throws InvalidParameterValue {
        RequestFormat requestFormat = byFormatParameter( formatParamValue, defaultFormat );
        if ( HTML.equals( requestFormat ) ) {
            return Response.ok( getClass().getResourceAsStream( "/datasets.html" ), TEXT_HTML ).build();
        }
        LinkBuilder linkBuilder = new LinkBuilder( uriInfo );
        List<Link> links = linkBuilder.createDatasetsLinks();
        List<Dataset> datasets = new ArrayList<>();

        OafDatasets oafDatasets = DeegreeWorkspaceInitializer.getOafDatasets();
        Map<String, OafDatasetConfiguration> datasetsConfigurations = oafDatasets.getDatasets();
        datasetsConfigurations.forEach( ( id, oafDatasetConfiguration ) -> {
            List<Link> datasetLinks = linkBuilder.createDatasetLinks( id );
            Dataset dataset = new Dataset( id, datasetLinks );
            datasets.add( dataset );
        } );
        org.deegree.services.oaf.domain.dataset.Datasets allDatasets = new org.deegree.services.oaf.domain.dataset.Datasets(
                        links, datasets );
        return Response.ok( allDatasets, APPLICATION_GEOJSON ).build();
    }

}