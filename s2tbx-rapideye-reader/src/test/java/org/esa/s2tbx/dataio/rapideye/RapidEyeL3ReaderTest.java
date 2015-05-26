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

package org.esa.s2tbx.dataio.rapideye;

import com.bc.ceres.core.NullProgressMonitor;
import org.esa.snap.framework.datamodel.Product;
import org.esa.snap.framework.datamodel.ProductData;
import org.esa.snap.util.TreeNode;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Ramona Manda
 */
public class RapidEyeL3ReaderTest {

    private RapidEyeL3Reader reader;

    @Before
    public void setup() {
        RapidEyeL3ReaderPlugin plugin = new RapidEyeL3ReaderPlugin();
        reader = new RapidEyeL3Reader(plugin);
    }

    @Test
    public void testGetReaderPlugin() {
        assertEquals(RapidEyeL3ReaderPlugin.class, reader.getReaderPlugIn().getClass());
    }

    @Test
    public void testReadProductNodes() {
        Date startDate = Calendar.getInstance().getTime();
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("Eritrea/1234567890_metadata.xml");
        System.setProperty("snap.reader.tileWidth", "100");
        System.setProperty("snap.reader.tileHeight", "100");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertEquals(3, finalProduct.getBands().length);
            assertEquals("EPSG:World Geodetic System 1984", finalProduct.getGeoCoding().getGeoCRS().getName().toString());
            assertEquals("L3M", finalProduct.getProductType());
            assertEquals(0, finalProduct.getMaskGroup().getNodeCount());
            assertEquals(10985, finalProduct.getSceneRasterWidth());
            assertEquals(11232, finalProduct.getSceneRasterHeight());
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadBandRasterData() {
        Date startDate = Calendar.getInstance().getTime();
        Product product = new Product("name", "desc", 100, 200);
        File file = TestUtil.getTestFile("Eritrea/1234567890_metadata.xml");
        File rasterFile = TestUtil.getTestFile("mediumImage.tif");
        System.setProperty("snap.reader.tileWidth", "100");
        System.setProperty("snap.reader.tileHeight", "200");
        try {

            Product finalProduct = reader.readProductNodes(file, null);
            ProductData data = ProductData.createInstance(ProductData.TYPE_UINT16, 20000);
            data.setElemFloatAt(3, 5);
            reader.readBandRasterData(finalProduct.getBandAt(0), 2000, 2000, 100, 200, data, new NullProgressMonitor());
            assertNotEquals(0, data.getElemFloatAt(0));
            assertNotEquals(-1000, data.getElemFloatAt(0));
            assertNotEquals(0, data.getElemFloatAt(1999));
            assertNotEquals(-1000, data.getElemFloatAt(1999));
            assertNotEquals(5, data.getElemFloatAt(3));
            Date endDate = Calendar.getInstance().getTime();
            assertTrue("The load time for the product is too big!", (endDate.getTime() - startDate.getTime()) / (60 * 1000) < 30);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnFileInput() {
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("Eritrea/1234567890_metadata.xml");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(2, components.getChildren().length);
            assertEquals("1234567890_metadata.xml", components.getChildren()[0].getId());
            assertEquals("13N041E-R1C2_2012_RE-3M_1234567890.tif", components.getChildren()[1].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetProductComponentsOnArchiveInput() {
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("Demo26_3A.zip");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            TreeNode<File> components = reader.getProductComponents();
            assertEquals(1, components.getChildren().length);
            assertEquals("Demo26_3A.zip", components.getChildren()[0].getId());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testConfigurePreferredTileSize() {
        Product product = new Product("name", "desc", 100, 100);
        File file = TestUtil.getTestFile("Eritrea/1234567890_metadata.xml");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            System.setProperty("snap.reader.tileWidth", "300");
            System.setProperty("snap.reader.tileHeight", "100");
            reader.configurePreferredTileSize(finalProduct);
            Dimension size = finalProduct.getPreferredTileSize();
            assertEquals(100, size.height);
            assertEquals(300, size.width);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }
}