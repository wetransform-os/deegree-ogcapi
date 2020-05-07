package org.deegree.services.oaf.resource;

import org.deegree.services.oaf.domain.collections.Collection;
import org.deegree.services.oaf.domain.collections.Collections;
import org.deegree.services.oaf.exceptions.UnknownCollectionId;
import org.deegree.services.oaf.exceptions.UnknownDatasetId;
import org.deegree.services.oaf.link.LinkBuilder;
import org.deegree.services.oaf.workspace.DataAccess;
import org.deegree.services.oaf.workspace.DataAccessFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.deegree.services.oaf.TestData.createCollection;
import static org.deegree.services.oaf.TestData.createCollections;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DataAccessFactory.class)
public class FeatureCollectionsTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable( TestProperties.LOG_TRAFFIC );
        return new ResourceConfig( FeatureCollections.class );
    }

    @Before
    public void mockCollectionFactory()
                    throws UnknownCollectionId, UnknownDatasetId {
        PowerMockito.mockStatic( DataAccessFactory.class );
        DataAccess testFactory = mock( DataAccess.class );
        Collection collection = createCollection();
        Collections testCollection = createCollections( collection );
        when( testFactory.createCollections( eq( "oaf" ), any( LinkBuilder.class ) ) ).thenReturn( testCollection );
        when( testFactory.createCollection( eq( "oaf" ), eq( "test" ), any( LinkBuilder.class ) ) ).thenReturn(
                        collection );
        when( DataAccessFactory.getInstance() ).thenReturn( testFactory );
    }

    @Test
    public void test_CollectionsDeclaration_Json_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections" ).request( APPLICATION_JSON_TYPE ).get();

        assertThat( response.getStatus(), is( 200 ) );
    }

    @Test
    public void test_CollectionsDeclaration_Xml_ShouldBeAvailable() {
        Response response = target( "/datasets/oaf/collections" ).request( APPLICATION_XML ).get();

        assertThat( response.getStatus(), is( 200 ) );
    }

}
