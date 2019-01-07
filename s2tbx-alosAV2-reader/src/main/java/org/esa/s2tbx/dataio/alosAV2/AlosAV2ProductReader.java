package org.esa.s2tbx.dataio.alosAV2;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.alosAV2.internal.AlosAV2Constants;
import org.esa.s2tbx.dataio.alosAV2.internal.AlosAV2Metadata;
import org.esa.s2tbx.dataio.readers.GeoTiffBasedReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * This product reader is intended for reading ALOS AVNIR-2 files
 * from compressed ESA archive files or from (uncompressed) file system.
 *
 * @author Denisa Stefanescu
 */
public class AlosAV2ProductReader extends GeoTiffBasedReader<AlosAV2Metadata> {
    private static final Logger logger = Logger.getLogger(AlosAV2ProductReader.class.getName());

    protected AlosAV2ProductReader(ProductReaderPlugIn readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn, colorPaletteFilePath);
    }

    @Override
    protected VirtualDirEx getInput(Object input) throws IOException {
        File inputFile = getFileInput(input);
        AlosAV2ProductReaderPlugin readerPlugin = (AlosAV2ProductReaderPlugin) getReaderPlugIn();
        try {
        productDirectory = readerPlugin.getInput(getInput());

        if (this.productDirectory.isCompressed()) {
         productDirectory = VirtualDirEx.create(productDirectory.getFile(inputFile.getName().substring(0, inputFile.getName().indexOf("."))+".ZIP"));
        } else {
            if (inputFile.getName().endsWith(AlosAV2Constants.METADATA_FILE_SUFFIX)) {
                if (productDirectory.exists(inputFile.getName().substring(0, inputFile.getName().indexOf(AlosAV2Constants.METADATA_FILE_SUFFIX)))) {
                    productDirectory = VirtualDirEx.create(productDirectory.getFile(inputFile.getName().substring(0, inputFile.getName().indexOf(AlosAV2Constants.METADATA_FILE_SUFFIX))));
                } else {
                    productDirectory = VirtualDirEx.create(productDirectory.getFile(inputFile.getName().substring(0, inputFile.getName().indexOf(AlosAV2Constants.METADATA_FILE_SUFFIX)) + ".ZIP"));
                }
            }
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

        return productDirectory;
    }

    @Override
    protected String getMetadataExtension() {
        return AlosAV2Constants.METADATA_EXTENSION;
    }

    @Override
    protected String getMetadataProfile() {
        if (metadata != null && metadata.size() > 0) {
            return metadata.get(0).getMetadataProfile();
        } else {
            return AlosAV2Constants.VALUE_NOT_AVAILABLE;
        }
    }

    @Override
    protected String getProductGenericName() {
        if (metadata != null && metadata.size() > 0) {
            return metadata.get(0).getProductName();
        } else {
            return AlosAV2Constants.VALUE_NOT_AVAILABLE;
        }
    }

    @Override
    protected String getMetadataFileSuffix() {
        return AlosAV2Constants.IMAGE_METADATA_EXTENSION;
    }

    @Override
    protected String[] getBandNames() {
        if (metadata != null && metadata.size() > 0) {
            return metadata.get(0).getBandNames();
        } else {
            return new String[]{};
        }
    }



    @Override
    protected void addMetadataMasks(Product product, AlosAV2Metadata componentMetadata) {
        logger.info("Create masks");
        int noDataValue, saturatedValue;
        if ((noDataValue = componentMetadata.getNoDataValue()) >= 0) {
            product.getMaskGroup().add(Mask.BandMathsType.create(AlosAV2Constants.NODATA,
                                                                 AlosAV2Constants.NODATA,
                                                                 product.getSceneRasterWidth(),
                                                                 product.getSceneRasterHeight(),
                                                                 String.valueOf(noDataValue),
                                                                 componentMetadata.getNoDataColor(),
                                                                 0.5));
        }
        if ((saturatedValue = componentMetadata.getSaturatedPixelValue()) >= 0) {
            product.getMaskGroup().add(Mask.BandMathsType.create(AlosAV2Constants.SATURATED,
                                                                 AlosAV2Constants.SATURATED,
                                                                 product.getSceneRasterWidth(),
                                                                 product.getSceneRasterHeight(),
                                                                 String.valueOf(saturatedValue),
                                                                 componentMetadata.getSaturatedColor(),
                                                                 0.5));
        }
    }

    @Override
    protected void addBands(Product product, AlosAV2Metadata componentMetadata, int componentIndex) {

        super.addBands(product, componentMetadata, componentIndex);
        //add the band informations from the metadata
        for (Band band:product.getBands()) {
            band.setScalingFactor(componentMetadata.getGain(band.getName()));
            band.setScalingOffset(componentMetadata.getBias(band.getName()));
            band.setUnit(componentMetadata.getBandUnits().get(band.getName()));
        }
        if (!AlosAV2Constants.PROCESSING_1B.equals(componentMetadata.getProcessingLevel())) {
            initGeoCoding(product);
        }
    }

    private void initGeoCoding(Product product) {
        AlosAV2Metadata deimosMetadata = metadata.get(0);
        AlosAV2Metadata.InsertionPoint[] geopositionPoints = deimosMetadata.getGeopositionPoints();
        if (geopositionPoints != null) {
            int numPoints = geopositionPoints.length;
            if (numPoints > 1 && (int) (numPoints / Math.sqrt((double) numPoints)) == numPoints) {
                float stepX = geopositionPoints[1].stepX - geopositionPoints[0].stepX;
                float stepY = geopositionPoints[1].stepY - geopositionPoints[0].stepY;
                float[] latitudes = new float[numPoints];
                float[] longitudes = new float[numPoints];
                for (int i = 0; i < numPoints; i++) {
                    latitudes[i] = geopositionPoints[i].y;
                    longitudes[i] = geopositionPoints[i].x;
                }
                TiePointGrid latGrid = addTiePointGrid(stepX, stepY, product, AlosAV2Constants.LAT_DS_NAME, latitudes);
                TiePointGrid lonGrid = addTiePointGrid(stepX, stepY, product, AlosAV2Constants.LON_DS_NAME, longitudes);
                GeoCoding geoCoding = new TiePointGeoCoding(latGrid, lonGrid);
                product.setSceneGeoCoding(geoCoding);
            }
        }
    }

    private TiePointGrid addTiePointGrid(float subSamplingX, float subSamplingY, Product product, String gridName, float[] tiePoints) {
        int gridDim = (int) Math.sqrt(tiePoints.length);
        final TiePointGrid tiePointGrid = createTiePointGrid(gridName, gridDim, gridDim, 0, 0, subSamplingX, subSamplingY, tiePoints);
        product.addTiePointGrid(tiePointGrid);
        return tiePointGrid;
    }
}
