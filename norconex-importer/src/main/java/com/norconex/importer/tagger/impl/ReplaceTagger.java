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
package com.norconex.importer.tagger.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.commons.lang.config.ConfigurationException;
import com.norconex.commons.lang.config.ConfigurationLoader;
import com.norconex.commons.lang.config.IXMLConfigurable;
import com.norconex.commons.lang.map.Properties;
import com.norconex.importer.tagger.IDocumentTagger;

//TODO Implement more efficiently. 
//TODO A regex version?
//TODO A multi-value version?
/**
 * Replaces an existing metadata value with another one.  The "toName" argument
 * is optional (the same field will be used for the replacement if no
 * "toName" is specified").
 * <p>Can be used both as a pre-parse or post-parse handler.</p>
 * <p>
 * XML configuration usage:
 * </p>
 * <pre>
 *  &lt;tagger class="com.norconex.importer.tagger.impl.ReplaceTagger"&gt;
 *      &lt;replace fromName="sourceFieldName" toName="targetFieldName"&gt
 *          &lt;fromValue&gtSource Value&lt;/fromValue&gt
 *          &lt;toValue&gtTarget Value&lt;/toValue&gt
 *      &lt;/replace&gt
 *      &lt;!-- multiple replace tags allowed --&gt;
 *  &lt;/tagger&gt;
 * </pre>
 * @author Pascal Essiembre
 *
 */
@SuppressWarnings("nls")
public class ReplaceTagger implements IDocumentTagger, IXMLConfigurable {

    private static final Logger LOG = LogManager.getLogger(ReplaceTagger.class);
    private static final long serialVersionUID = -6062036871216739761L;
    
    private final Map<String, Replacement> replacements = 
            new HashMap<String, Replacement>();
    
    @Override
    public void tagDocument(
            String reference, InputStream document,
            Properties metadata, boolean parsed)
            throws IOException {
        for (String name : replacements.keySet()) {
            Replacement repl = replacements.get(name);
            // Skip if no replacement
            if (repl == null) {
                continue;
            }

            // Do the potential replacement
            String value = metadata.getString(name);

            if(repl.details.containsKey(value))
            {
                ReplacementDetails detail = repl.details.get(value);
                    if (detail != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(name + " value replaced: "
                                    + detail.fromValue + "->" + detail.toValue);
                        }
                        value = detail.toValue;
                    }
                    if ((detail.toName != null) && (detail.toName.length() > 0)) {
                        metadata.addString(detail.toName, value);
                    } else {
                        metadata.setString(repl.fromName, value);
                    }
            }
            else{
                LOG.error("THE VALUE DOES NOT EXIST IN MIME TYPE----"+value);
            }
        }
    }

    
    public Map<String, Replacement> getReplacements() {
        return Collections.unmodifiableMap(replacements);
    }

    public void removeReplacement(String fromName) {
        replacements.remove(fromName);
    }
    
    public void addReplacement(
            String fromValue, String toValue, String fromName) {
        addReplacement(fromValue, toValue, fromName, null);
    }

    public void addReplacement(
            String fromValue, String toValue, String fromName, String toName) {
        Replacement repl = replacements.get(fromName);
        if (repl == null) {
            repl = new Replacement();
            repl.fromName = fromName;
            replacements.put(fromName, repl);
        }
        ReplacementDetails details = new ReplacementDetails();
        details.fromValue = fromValue;
        details.toName = toName;
        details.toValue = toValue;
        repl.details.put(fromValue, details);
    }

    
    public class Replacement {
        private String fromName;
        private final Map<String, ReplacementDetails> details = 
            new HashMap<String, ReplacementDetails>();
        public String getFromName() {
            return fromName;
        }
        public List<ReplacementDetails> getDetails() {
            return Collections.unmodifiableList(
                    new ArrayList<ReplacementDetails>(details.values()));
        }
        @Override
        public String toString() {
            return "Replacement [fromName=" + fromName + ", details=" + details
                    + "]";
        }
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                .append(details)
                .append(fromName)
            .toHashCode();
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ReplaceTagger.Replacement)) {
                return false;
            }
            ReplaceTagger.Replacement other = (ReplaceTagger.Replacement) obj;
            return new EqualsBuilder()
                .append(details, other.details)
                .append(fromName, other.fromName)
                .isEquals();
        }
    }
    public class ReplacementDetails {
        private String fromValue;
        private String toName;
        private String toValue;
        public String getFromValue() {
            return fromValue;
        }
        public String getToName() {
            return toName;
        }
        public String getToValue() {
            return toValue;
        }
        @Override
        public String toString() {
            return "ReplacementDetails [fromValue=" + fromValue + ", toName="
                    + toName + ", toValue=" + toValue + "]";
        }
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                .append(fromValue)
                .append(toName)
                .append(toValue)
                .toHashCode();
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ReplaceTagger.ReplacementDetails)) {
                return false;
            }
            ReplaceTagger.ReplacementDetails other = (ReplaceTagger.ReplacementDetails) obj;
            return new EqualsBuilder()
                .append(fromValue, other.fromValue)
                .append(toName, other.toName)
                .append(toValue, other.toValue)
                .isEquals();
        }
    }
    
    @Override
    public void loadFromXML(Reader in) throws IOException {
        try {
            XMLConfiguration xml = ConfigurationLoader.loadXML(in);
            List<HierarchicalConfiguration> nodes = 
                    xml.configurationsAt("replace");
            for (HierarchicalConfiguration node : nodes) {
                addReplacement(
                        node.getString("fromValue"),
                        node.getString("toValue"),
                        node.getString("[@fromName]"),
                        node.getString("[@toName]"));
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
            for (String key : replacements.keySet()) {
                Replacement replacement = replacements.get(key);
                for (String detailKey : replacement.details.keySet()) {
                    ReplacementDetails details = 
                            replacement.details.get(detailKey);
                    writer.writeStartElement("replace");
                    writer.writeAttribute("fromName", key);
                    if (details.toName != null) {
                        writer.writeAttribute("toName", details.toName);
                    }
                    writer.writeStartElement("fromValue");
                    writer.writeCharacters(details.fromValue);
                    writer.writeEndElement();
                    writer.writeStartElement("toValue");
                    writer.writeCharacters(details.toValue);
                    writer.writeEndElement();
                    writer.writeEndElement();
                }
            }
            writer.writeEndElement();
            writer.flush();
            writer.close();
        } catch (XMLStreamException e) {
            throw new IOException("Cannot save as XML.", e);
        }
    }


    @Override
    public String toString() {
        return "ReplaceTagger [replacements=" + replacements + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((replacements == null) ? 0 : replacements.hashCode());
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
        ReplaceTagger other = (ReplaceTagger) obj;
        if (replacements == null) {
            if (other.replacements != null) {
                return false;
            }
        } else if (!replacements.equals(other.replacements)) {
            return false;
        }
        return true;
    }
}
