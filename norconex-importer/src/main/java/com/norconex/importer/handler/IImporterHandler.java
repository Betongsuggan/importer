/* Copyright 2010-2014 Norconex Inc.
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
package com.norconex.importer.handler;

import java.io.Serializable;

import com.norconex.importer.handler.filter.IDocumentFilter;
import com.norconex.importer.handler.splitter.IDocumentSplitter;
import com.norconex.importer.handler.tagger.IDocumentTagger;
import com.norconex.importer.handler.transformer.IDocumentTransformer;

/**
 * <p>Identifies a class as being an import handler.  Handlers performs specific
 * tasks on the importing content (other than parsing to extract raw content).
 * They can be invoked before or after a document is parsed.  There are 
 * three types of handlers currently supported:</p> 
 * <ul>
 *   <li>{@link IDocumentFilter}: accepts or reject an incoming document.</li>
 *   <li>{@link IDocumentTagger}: modifies a document metadata.</li>
 *   <li>{@link IDocumentTransformer}: modifies a document content.</li>
 *   <li>{@link IDocumentSplitter}: slipts a document into multiple ones.</li>
 * </ul>
 * @author Pascal Essiembre
 */
public interface IImporterHandler extends Serializable {
    // Act as a marker only for now.
}
