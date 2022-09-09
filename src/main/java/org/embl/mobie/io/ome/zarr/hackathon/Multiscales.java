package org.embl.mobie.io.ome.zarr.hackathon;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import mpicbg.spim.data.sequence.VoxelDimensions;

import java.util.Arrays;
import java.util.List;

/**
 * Copy from {@code org.embl.mobie.io.ome.zarr.util.OmeZarrMultiscales}
 *
 */
public class Multiscales
{
    // key in json for multiscales
    public static final String MULTI_SCALE_KEY = "multiscales";

    // Serialisation
    private String version;
    private String name;
    private String type;
    private Axis[] axes; // from v0.4+ within JSON
    private Dataset[] datasets;
    private CoordinateTransformations[] coordinateTransformations;

    // Runtime

    // Simply contains the {@codeAxes[] axes}
    // but in reversed order to accommodate
    // the Java array ordering of the image data.
    private List< Axis > axisList;
    private int numDimensions;

    public Multiscales() {
    }

    public static class Dataset {
        public String path;
        public CoordinateTransformations[] coordinateTransformations;
    }

    public static class CoordinateTransformations {
        public String type;
        public double[] scale;
        public double[] translation;
        public String path;
    }

    public static class Axis
    {
        public static final String CHANNEL_TYPE = "channel";
        public static final String TIME_TYPE = "time";
        public static final String SPATIAL_TYPE = "space";

        public static final String X_AXIS_NAME = "x";
        public static final String Y_AXIS_NAME = "y";
        public static final String Z_AXIS_NAME = "z";

        public String name;
        public String type;
        public String unit;
    }

    /**
     * "Manually" set some fields from the JSON.
     * This is necessary to support the
     * different versions of OME-Zarr.
     *
     * TODO: Do this via a JSONAdapter?
     *
     * @param multiscales The multiscales JSON metadata.
     */
    public void applyVersionFixesAndInit( JsonElement multiscales ) {
        applyVersionFixes( multiscales );
        init();
    }

    private void init()
    {
        axisList = Lists.reverse( Arrays.asList( axes ) );
        numDimensions = axisList.size();
    }

    private void applyVersionFixes( JsonElement multiscales )
    {
        final JsonElement jsonElement = multiscales.getAsJsonArray().get( 0 );
        String version = jsonElement.getAsJsonObject().get("version").getAsString();
        if ( version.equals("0.3") ) {
            JsonElement axes = jsonElement.getAsJsonObject().get("axes");
            // FIXME
            //   - populate Axes[]
            //   - populate coordinateTransformations[]
            throw new RuntimeException("Parsing version 0.3 not yet implemented.");
        } else if ( version.equals("0.4") ) {
            // This should just work automatically
        } else {
            JsonElement axes = jsonElement.getAsJsonObject().get("axes");
            // FIXME
            //   - populate Axes[]
            //   - populate coordinateTransformations[]
            throw new RuntimeException("Parsing version "+ version + " is not yet implemented.");
        }
    }

    public int getChannelAxisIndex()
    {
        for ( int d = 0; d < numDimensions; d++ )
            if ( axisList.get( d ).type.equals( Axis.CHANNEL_TYPE ) )
                return d;
        return -1;
    }

    public int getTimePointAxisIndex()
    {
        for ( int d = 0; d < numDimensions; d++ )
            if ( axisList.get( d ).type.equals( Axis.TIME_TYPE ) )
                return d;
        return -1;
    }

    public int getSpatialAxisIndex( String axisName )
    {
        for ( int d = 0; d < numDimensions; d++ )
            if ( axisList.get( d ).type.equals( Axis.SPATIAL_TYPE )
                 && axisList.get( d ).name.equals( axisName ) )
                return d;
        return -1;
    }

    public List< Axis > getAxes()
    {
        return axisList;
    }

    public CoordinateTransformations[] getCoordinateTransformations()
    {
        return coordinateTransformations;
    }

    public Dataset[] getDatasets()
    {
        return datasets;
    }

    public int numDimensions()
    {
        return numDimensions;
    }
}
