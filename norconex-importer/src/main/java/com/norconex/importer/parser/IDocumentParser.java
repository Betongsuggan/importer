/* Copyright 2010-2013 Norconex Inc.
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
package com.norconex.importer.parser;

import java.io.InputStream;
import java.io.Serializable;
import java.io.Writer;

import com.norconex.commons.lang.file.ContentType;
import com.norconex.importer.ImporterMetadata;

/**
 * Implementations are responsible for parsing a document (InputStream) to 
 * extract its text and metadata.
 * @author Pascal Essiembre
 * @see IDocumentParserFactory
 */
@SuppressWarnings("nls")
public interface IDocumentParser extends Serializable {

    String RDF_BASE_URI = "http://norconex.com/Document";
    String RDF_SUBJECT_CONTENT = "Content";

    /**
     * Parses a document.
     * @param inputStream the document to parse
     * @param contentType the content type of the document
     * @param writer where to save the extracted text
     * @param metadata where to store the metadata
     * @throws DocumentParserException
     */
    void parseDocument(
            InputStream inputStream, ContentType contentType,
            Writer writer, ImporterMetadata metadata)
        throws DocumentParserException;
}
