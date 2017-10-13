package eu.profinit.maven.plugin.dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.stubs.StubArtifactCollector;
import org.apache.maven.plugin.testing.stubs.StubArtifactResolver;

import eu.profinit.maven.plugin.dependency.fromConfiguration.ArtifactItem;
import eu.profinit.maven.plugin.dependency.fromConfiguration.UnpackMojo;
import eu.profinit.maven.plugin.dependency.testUtils.DependencyTestUtils;
import eu.profinit.maven.plugin.dependency.utils.markers.UnpackFileMarkerHandler;

/**
 * TODO: does not work ... does not testing desired functionality (i.e., the test always succeeds) 
 * 
 * @author Jakub Gemrot
 */
public class TestCheckMarkerUnpackMojo 
	extends AbstractDependencyMojoTestCase
{
	private final String PACKED_FILE = "test.zip";
	private final String SHOULD_NOT_BE_UNPACKED_ZIP = "test-no-unpack.zip";
	
	private final String SHOULD_NOT_BE_UNPACKED_FILE = "should-not-be-unpacked.txt";
	
	private final String UNPACKED_FILE_PREFIX = "test";
	private final String UNPACKED_FILE_SUFFIX = ".txt";
	
	private final String PACKED_FILE_PATH = "target/test-classes/unit/unpack-checkmarker-test/" + PACKED_FILE;
	private final String SHOULD_NOT_BE_UNPACKED_FILE_PATH = "target/test-classes/unit/unpack-checkmarker-test/" + SHOULD_NOT_BE_UNPACKED_ZIP;
	
	UnpackMojo mojo;

    protected void setUp() throws Exception {
        super.setUp( "unpack", true );
        
        File testPom = new File( getBasedir(), "target/test-classes/unit/unpack-checkmarker-test/plugin-config.xml" );
        mojo = (UnpackMojo) lookupMojo( "unpack", testPom );
        mojo.setOutputDirectory( new File( this.testDir, "outputDirectory" ) );
        // mojo.silent = true;
    }
    
    protected void afterClass()
    {
        super.tearDown();
        
        mojo = null;
        System.gc();
    }
    
    public void assertMarkerFiles( Collection items, boolean exist )
    {
        Iterator iter = items.iterator();
        while ( iter.hasNext() )
        {
            assertMarkerFile( exist, (ArtifactItem) iter.next() );
        }
    }

    public void assertMarkerFile( boolean val, ArtifactItem item )
    {
        UnpackFileMarkerHandler handle = new UnpackFileMarkerHandler( item, mojo.getMarkersDirectory() );
        try
        {
            assertEquals( val, handle.isMarkerSet() );
        }
        catch ( MojoExecutionException e )
        {
            fail( e.getLongMessage() );
        }
    }
    
    private void assertUnpacked(boolean unpacked, String fileName)
    {
    	File destFile = new File( mojo.getOutputDirectory().getAbsolutePath() , fileName );    	
    	assertEquals(unpacked, destFile.exists());
    }
    
    /**
     * This test will validate that only the 1 and 11 files get unpacked
     * @throws Exception
     */
    public void testUnpackCheckMarker() throws Exception
	{
    	// FIRST - UNPACK test-test-1.0.0-SNAPSHOT ARTIFACT
    	
        // it needs to get the archivermanager
        //stubFactory.setUnpackableFile( mojo.getArchiverManager() );
        // i'm using one file repeatedly to archive so I can test the name
        // programmatically.
        
        stubFactory.setSrcFile( new File( getBasedir() + File.separatorChar + PACKED_FILE_PATH ) );
        Artifact artifact = stubFactory.createArtifact( "test", "test", "1.0.0-SNAPSHOT", Artifact.SCOPE_COMPILE, "jar", null );
        ArtifactItem item = stubFactory.getArtifactItem( artifact );
        
        ArrayList list = new ArrayList( 1 );
        list.add( item );
        
        assertNotNull( mojo );
        assertNotNull( mojo.getProject() );
        
        mojo.setFactory( DependencyTestUtils.getArtifactFactory() );
        mojo.setResolver( new StubArtifactResolver( stubFactory, false, false ) );
        mojo.setMarkersDirectory( new File( this.testDir, "markers" ) );
        mojo.setArtifactCollector( new StubArtifactCollector() );
        mojo.setArtifactItems( list );
    	
    	mojo.execute();
    	
    	// CHECK IT WAS UNPACKED
    	
    	assertUnpacked(true, UNPACKED_FILE_PREFIX + 1 + UNPACKED_FILE_SUFFIX);
    	assertUnpacked(true, UNPACKED_FILE_PREFIX + 11 + UNPACKED_FILE_SUFFIX);
    	assertUnpacked(true, UNPACKED_FILE_PREFIX + 2 + UNPACKED_FILE_SUFFIX);
    	assertUnpacked(true, UNPACKED_FILE_PREFIX + 3 + UNPACKED_FILE_SUFFIX);
    	
    	// SECOOND - UNPACK test-test-1.0.0-SNAPSHOT ARTIFACT AGAIN BUT POINTING TO DIFFERENT FILE
    	// IT SHOULD NOT UNPACKED! AS THE ARTIFACT HAS BEEN ALREADY UNPACKED BEFORE
    	
        stubFactory.setSrcFile( new File( getBasedir() + File.separatorChar + SHOULD_NOT_BE_UNPACKED_FILE_PATH ) );
        artifact = stubFactory.createArtifact( "test", "test", "1.0.0-SNAPSHOT", Artifact.SCOPE_COMPILE, "jar", null );
        item = stubFactory.getArtifactItem( artifact );
        item.setNeedsProcessing(true);
        
        list = new ArrayList( 1 );
        list.add( item );
        
        assertNotNull( mojo );
        assertNotNull( mojo.getProject() );
        
        mojo.setFactory( DependencyTestUtils.getArtifactFactory() );
        mojo.setResolver( new StubArtifactResolver( stubFactory, false, false ) );
        mojo.setMarkersDirectory( new File( this.testDir, "markers" ) );
        mojo.setArtifactCollector( new StubArtifactCollector() );
        mojo.setArtifactItems( list );
        
    	mojo.execute();
    	
    	// CHECK IT WAS NOT UNPACKED
    	
    	//assertUnpacked(false, SHOULD_NOT_BE_UNPACKED_FILE);
	}
    
    /**
     * This test will validate that only the 1 and 11 files get unpacked
     * @throws Exception
     */
    public void testUnpackCheckMarkerNewerVersion() throws Exception
	{
    	// FIRST - UNPACK test-test-1.0.0-SNAPSHOT ARTIFACT
    	
        // it needs to get the archivermanager
        //stubFactory.setUnpackableFile( mojo.getArchiverManager() );
        // i'm using one file repeatedly to archive so I can test the name
        // programmatically.
        
        stubFactory.setSrcFile( new File( getBasedir() + File.separatorChar + PACKED_FILE_PATH ) );
        Artifact artifact = stubFactory.createArtifact( "test", "test", "1.0.0-SNAPSHOT", Artifact.SCOPE_COMPILE, "jar", null );
        ArtifactItem item = stubFactory.getArtifactItem( artifact );
        
        ArrayList list = new ArrayList( 1 );
        list.add( item );
        
        assertNotNull( mojo );
        assertNotNull( mojo.getProject() );
        
        mojo.setFactory( DependencyTestUtils.getArtifactFactory() );
        mojo.setResolver( new StubArtifactResolver( stubFactory, false, false ) );
        mojo.setMarkersDirectory( new File( this.testDir, "markers" ) );
        mojo.setArtifactCollector( new StubArtifactCollector() );
        mojo.setArtifactItems( list );
    	
    	mojo.execute();
    	
    	// CHECK IT WAS UNPACKED
    	
    	assertUnpacked(true, UNPACKED_FILE_PREFIX + 1 + UNPACKED_FILE_SUFFIX);
    	assertUnpacked(true, UNPACKED_FILE_PREFIX + 11 + UNPACKED_FILE_SUFFIX);
    	assertUnpacked(true, UNPACKED_FILE_PREFIX + 2 + UNPACKED_FILE_SUFFIX);
    	assertUnpacked(true, UNPACKED_FILE_PREFIX + 3 + UNPACKED_FILE_SUFFIX);
    	
    	// SECOOND - UNPACK test-test-1.1.0-SNAPSHOT ARTIFACT AGAIN BUT POINTING TO DIFFERENT FILE
    	// IT SHOULD BE UNPACKED! AS WE HAVE INCREMENTED VERSION OF THE TEST
    	
        stubFactory.setSrcFile( new File( getBasedir() + File.separatorChar + SHOULD_NOT_BE_UNPACKED_FILE_PATH ) );
        artifact = stubFactory.createArtifact( "test", "test", "1.1.0-SNAPSHOT", Artifact.SCOPE_COMPILE, "jar", null );
        item = stubFactory.getArtifactItem( artifact );
        item.setNeedsProcessing(true);
        
        list = new ArrayList( 1 );
        list.add( item );
        
        assertNotNull( mojo );
        assertNotNull( mojo.getProject() );
        
        mojo.setFactory( DependencyTestUtils.getArtifactFactory() );
        mojo.setResolver( new StubArtifactResolver( stubFactory, false, false ) );
        mojo.setMarkersDirectory( new File( this.testDir, "markers" ) );
        mojo.setArtifactCollector( new StubArtifactCollector() );
        mojo.setArtifactItems( list );
        
    	mojo.execute();
    	
    	// CHECK IT WAS UNPACKED
    	
    	assertUnpacked(true, SHOULD_NOT_BE_UNPACKED_FILE);
	}
    
}
