/* Copyright 2014 Norconex Inc.
 * 
 * This file is part of Norconex Importer.
 * 
 * Norconex Importer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Norconex Importer is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Norconex Importer. If not, see <http://www.gnu.org/licenses/>.
 */
package com.norconex.importer.tagger.impl;

import java.io.IOException;

import org.junit.Test;

import com.norconex.commons.lang.config.ConfigurationUtil;
import com.norconex.importer.tagger.impl.CopyTagger;

public class CopyTaggerTest {

    @Test
    public void testWriteRead() throws IOException {
        CopyTagger tagger = new CopyTagger();
        tagger.addCopyDetails("from1", "to1", false);
        tagger.addCopyDetails("from2", "to2", true);
        tagger.addCopyDetails("from3", null, true);
        tagger.addCopyDetails(null, "to4", true);
        System.out.println("Writing/Reading this: " + tagger);
        ConfigurationUtil.assertWriteRead(tagger);
    }

}
