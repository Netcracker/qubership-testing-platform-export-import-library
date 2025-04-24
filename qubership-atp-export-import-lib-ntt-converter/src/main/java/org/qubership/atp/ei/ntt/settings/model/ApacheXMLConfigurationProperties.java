/*
 * # Copyright 2024-2025 NetCracker Technology Corporation
 * #
 * # Licensed under the Apache License, Version 2.0 (the "License");
 * # you may not use this file except in compliance with the License.
 * # You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * # Unless required by applicable law or agreed to in writing, software
 * # distributed under the License is distributed on an "AS IS" BASIS,
 * # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * # See the License for the specific language governing permissions and
 * # limitations under the License.
 */

package org.qubership.atp.ei.ntt.settings.model;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * TODO Make summary for this class.
 */
@SuppressWarnings("serial")
public class ApacheXMLConfigurationProperties extends XMLConfiguration {
    private static Logger log = Logger.getLogger(ApacheXMLConfigurationProperties.class);
    
    @Override
    protected Transformer createTransformer() throws TransformerException {
        Transformer transformer = super.createTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "value");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        return transformer;
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    public ApacheXMLConfigurationProperties() {
        try {
            createTransformer();
            this.setDelimiterParsingDisabled(true);
            this.setAttributeSplittingDisabled(true);
            /*
            */
        } catch (TransformerException e) {
            log.error(e.getMessage(), e);
        }
    }
    

}
