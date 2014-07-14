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
package com.norconex.importer;

import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import com.norconex.commons.lang.config.ConfigurationException;
import com.norconex.commons.lang.config.ConfigurationLoader;
import com.norconex.commons.lang.config.ConfigurationUtil;
import com.norconex.importer.handler.IImporterHandler;

/**
 * Importer configuration loader.  Configuration options are defined
 * as part of general product documentation.
 * @author Pascal Essiembre
 */
@SuppressWarnings("nls")
public final class ImporterConfigLoader {

    private ImporterConfigLoader() {
        super();
    }

    /**
     * Loads importer configuration.
     * @param configFile configuration file
     * @param configVariables configuration variables 
     *        (.variables or .properties)
     * @return importer configuration
     * @throws ConfigurationException problem loading configuration
     */
    public static ImporterConfig loadImporterConfig(
            File configFile, File configVariables) {
        try {
            ConfigurationLoader configLoader = new ConfigurationLoader();
            XMLConfiguration xml = configLoader.loadXML(
                    configFile, configVariables);
            return loadImporterConfig(xml);
        } catch (Exception e) {
            throw new ConfigurationException(
                    "Could not load configuration file: " + configFile, e);
        }
    }    
    

    /**
     * Loads importer configuration.
     * @param config reader for the configuration file
     * @return importer configuration
     * @throws ConfigurationException problem loading configuration
     */    
    public static ImporterConfig loadImporterConfig(Reader config)  {
        try {
            XMLConfiguration xml = ConfigurationLoader.loadXML(config);
            return loadImporterConfig(xml);
        } catch (Exception e) {
            throw new ConfigurationException(
                    "Could not load configuration file from Reader.", e);
        }
    }
    
    
    /**
     * Loads importer configuration.
     * @param xml XMLConfiguration instance
     * @return importer configuration
     * @throws ConfigurationException problem loading configuration
     */
    public static ImporterConfig loadImporterConfig(
            XMLConfiguration xml) {
        if (xml == null) {
            return null;
        }
        ImporterConfig config = new ImporterConfig();
        try {
            //--- Temp directory -----------------------------------------------
            config.setTempDir(new File(xml.getString(
                    "tempDir", ImporterConfig.DEFAULT_TEMP_DIR_PATH)));

            //--- File Mem Cache Size ------------------------------------------
            config.setFileMemCacheSize(xml.getInt("fileMemCacheSize", 
                    ImporterConfig.DEFAULT_FILE_MEM_CACHE_SIZE));
            
            //--- Pre-Import Handlers ------------------------------------------
            config.setPreParseHandlers(
                    loadImportHandlers(xml, "preParseHandlers"));

            //--- Document Parser Factory --------------------------------------
            config.setParserFactory(ConfigurationUtil.newInstance(
                    xml, "documentParserFactory", config.getParserFactory()));

            //--- Post-Import Handlers -----------------------------------------
            config.setPostParseHandlers(
                    loadImportHandlers(xml, "postParseHandlers"));
        } catch (Exception e) {
            throw new ConfigurationException("Could not load configuration "
                    + "from XMLConfiguration instance.", e);
        }
        return config;
    }
    
    private static IImporterHandler[] loadImportHandlers(
            XMLConfiguration xml, String xmlPath) {
        List<IImporterHandler> handlers = new ArrayList<IImporterHandler>();

        ExpressionEngine originalEngine = xml.getExpressionEngine();
        xml.setExpressionEngine(new XPathExpressionEngine());
        List<HierarchicalConfiguration> xmlHandlers = 
                xml.configurationsAt(xmlPath + "/*");
        xml.setExpressionEngine(originalEngine);
        for (HierarchicalConfiguration xmlHandler : xmlHandlers) {
            xmlHandler.setExpressionEngine(originalEngine);
            handlers.add(
                    (IImporterHandler) ConfigurationUtil.newInstance(xmlHandler));
        }
        return handlers.toArray(new IImporterHandler[]{});
    }
}
