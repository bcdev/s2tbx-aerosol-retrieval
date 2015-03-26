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

package org.esa.beam.dataio.s2.structure;

import java.util.ArrayList;
import java.util.List;

public class S2ProductStructure {
    final private List<StructuralItem> thePattern;

    public S2ProductStructure() {
        thePattern = new ArrayList<StructuralItem>();
    }

    public S2ProductStructure(List<StructuralItem> thePattern) {
        this.thePattern = thePattern;
    }

    public void addItem(StructuralItem item) {
        thePattern.add(item);
    }

    public List<StructuralItem> getThePattern() {
        return thePattern;
    }
}
