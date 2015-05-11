/*
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 *  with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.spot.dimap;

import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.snap.framework.datamodel.ProductData;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ramona Manda
 */
public class SpotTake5MetadataTest {

    private SpotTake5Metadata metadata;

    @Before
    public void setUp() throws Exception {
        XmlMetadataParserFactory.registerParser(SpotTake5Metadata.class, new XmlMetadataParser<SpotTake5Metadata>(SpotTake5Metadata.class));
        metadata = XmlMetadata.create(SpotTake5Metadata.class, TestUtil.getTestFile("SPOT4_HRVIR1_XS_20130616_N2A_JTanzanieD0000B0000.xml"));
    }

    @Test
    public void testGetDatePdv() throws Exception {
        ProductData.UTC expected = ProductData.UTC.parse("2013-06-16 06:40:34", "yyyy-MM-dd HH:mm:ss");
        assertEquals(expected.getAsDate().getTime(), metadata.getDatePdv().getAsDate().getTime());
    }

    @Test
    public void testGetDimensions() throws Exception {
        assertEquals(3750, metadata.getRasterHeight());
        assertEquals(4000, metadata.getRasterWidth());
        assertEquals(150000.0, metadata.getRasterGeoRefX(), 0.001);
        assertEquals(9240000.0, metadata.getRasterGeoRefY(), 0.001);
        assertEquals(20.0, metadata.getRasterGeoRefSizeX(), 0.001);
        assertEquals(-20.0, metadata.getRasterGeoRefSizeY(), 0.001);
    }

    @Test
    public void testGetBands() throws Exception {
        assertEquals(4, metadata.getNumBands());
        assertEquals(4, metadata.getBandNames().length);
        assertEquals("XS1", metadata.getBandNames()[0]);
        assertEquals("XS2", metadata.getBandNames()[1]);
        assertEquals("XS3", metadata.getBandNames()[2]);
        assertEquals("SWIR", metadata.getBandNames()[3]);
    }

    @Test
    public void testGetTiffAndMaskFiles() throws Exception {
        assertEquals(3, metadata.getTiffFiles().size());
        assertEquals("SPOT4_HRVIR1_XS_20130616_N2A_AOT_JTanzanieD0000B0000.TIF", metadata.getTiffFiles().get("ORTHO_SURF_AOT"));
        assertEquals("SPOT4_HRVIR1_XS_20130616_N2A_ORTHO_SURF_CORR_ENV_JTanzanieD0000B0000.TIF", metadata.getTiffFiles().get("ORTHO_SURF_CORR_ENV"));
        assertEquals("SPOT4_HRVIR1_XS_20130616_N2A_ORTHO_SURF_CORR_PENTE_JTanzanieD0000B0000.TIF", metadata.getTiffFiles().get("ORTHO_SURF_CORR_PENTE"));

        assertEquals(3, metadata.getMaskFiles().size());
        assertEquals("MASK/SPOT4_HRVIR1_XS_20130616_N2A_JTanzanieD0000B0000_SAT.TIF", metadata.getMaskFiles().get("MASK_SATURATION"));
        assertEquals("MASK/SPOT4_HRVIR1_XS_20130616_N2A_JTanzanieD0000B0000_NUA.TIF", metadata.getMaskFiles().get("MASK_CLOUDS"));
        assertEquals("MASK/SPOT4_HRVIR1_XS_20130616_N2A_JTanzanieD0000B0000_DIV.TIF", metadata.getMaskFiles().get("MASK_DIV"));
    }

    @Test
    public void testGetProductName() throws Exception {
        assertEquals("SPOT4_HRVIR1_XS_20130616_N2A_JTanzanieD0000B0000", metadata.getProductName());
    }

    @Test
    public void testGetProjectionCode() throws Exception {
        assertEquals("UTM37S", metadata.getProjectionCode());
    }

    @Test
    public void testGetMetadataProfile() throws Exception {
        assertEquals("N2A", metadata.getMetadataProfile());
    }

}
