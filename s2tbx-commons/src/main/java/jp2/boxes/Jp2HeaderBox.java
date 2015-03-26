/*
 *
 *  * Copyright (C) 2015 CS SI
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package jp2.boxes;

import jp2.Box;
import jp2.BoxReader;
import jp2.BoxType;

import java.io.IOException;
import java.util.Map;

/**
 * @author Norman Fomferra
 */
public class Jp2HeaderBox extends Box {

    Map<Integer, Box> subBoxes;

    public Jp2HeaderBox(BoxType type, long position, long length, int dataOffset) {
        super(type, position, length, dataOffset);
    }

    @Override
    public void readFrom(BoxReader reader) throws IOException {
    }

}
