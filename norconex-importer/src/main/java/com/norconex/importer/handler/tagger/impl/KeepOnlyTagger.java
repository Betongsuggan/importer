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
package com.norconex.importer.handler.tagger.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.commons.lang.config.ConfigurationException;
import com.norconex.commons.lang.config.ConfigurationLoader;
import com.norconex.commons.lang.config.IXMLConfigurable;
import com.norconex.importer.doc.ImporterMetadata;
import com.norconex.importer.handler.ImporterHandlerException;
import com.norconex.importer.handler.tagger.IDocumentTagger;

/**
 * <p>
 * Keep only the metadata fields provided, delete all other ones.
 * </p>
 * <p>Can be used both as a pre-parse or post-parse handler.</p>
 * <p>
 * XML configuration usage:
 * </p>
 * <pre>
 *  &lt;tagger class="com.norconex.importer.handler.tagger.impl.KeepOnlyTagger"
 *      fields="[coma-separated list of fields to keep]"/&gt
 * </pre>
 * @author Pascal Essiembre
 */
@SuppressWarnings("nls")
public class KeepOnlyTagger 
        implements IDocumentTagger, IXMLConfigurable {

    private static final Logger LOG = 
            LogManager.getLogger(KeepOnlyTagger.class);
    private static final long serialVersionUID = -4075527874358712815L;

    private final List<String> fields = new ArrayList<String>();
    
    @Override
    public void tagDocument(
            String reference, InputStream document,
            ImporterMetadata metadata, boolean parsed)
            throws ImporterHandlerException {
        
        // If fields is empty, it means we should keep nothing
        if (fields.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Clear all metadata from " + reference);
            }
            metadata.clear();
        } else {
            // Remove metadata not in fields
            Iterator<String> iter = metadata.keySet().iterator();
            List<String> removed = null;
            while (iter.hasNext()) {
                String name = iter.next();
                if (!exists(name)) {
                    if (LOG.isDebugEnabled()) {
                        if (removed == null) {
                            removed = new ArrayList<String>();
                        }
                        removed.add(name);
                    }
                    iter.remove();
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Removed metadata fields \""
                        + StringUtils.join(removed, ",")
                        + "\" from " + reference);
            }
        }
    }

    private boolean exists(String fieldToMatch) {
        for (String field : fields) {
            if (field.equalsIgnoreCase(fieldToMatch)) {
                return true;
            }
        }
        return false;
    }
    
    
    public List<String> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public void addField(String field) {
        fields.add(field);
    }
    public void removeField(String field) {
        fields.remove(field);
    }

    @Override
    public void loadFromXML(Reader in) throws IOException {
        try {
            XMLConfiguration xml = ConfigurationLoader.loadXML(in);
            String fieldsStr = xml.getString("[@fields]");
            String[] configFields = StringUtils.split(fieldsStr, ",");
            for (String field : configFields) {
                addField(field.trim());
            }
        } catch (ConfigurationException e) {
            throw new IOException("Cannot load XML.", e);
        }
    }

    @Override
    public void saveToXML(Writer out) throws IOException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(out);
            writer.writeStartElement("tagger");
            writer.writeAttribute("class", getClass().getCanonicalName());
            writer.writeAttribute("fields", StringUtils.join(fields, ","));
            writer.writeEndElement();
            writer.flush();
            writer.close();
        } catch (XMLStreamException e) {
            throw new IOException("Cannot save as XML.", e);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KeepOnlyTagger [{");
        builder.append(StringUtils.join(fields, ","));
        builder.append("}]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        KeepOnlyTagger other = (KeepOnlyTagger) obj;
        if (fields == null) {
            if (other.fields != null) {
                return false;
            }
        } else if (!fields.equals(other.fields)) {
            return false;
        }
        return true;
    }
}
