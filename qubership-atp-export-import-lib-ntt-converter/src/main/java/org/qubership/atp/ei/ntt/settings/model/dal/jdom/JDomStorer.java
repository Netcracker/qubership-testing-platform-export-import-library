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

package org.qubership.atp.ei.ntt.settings.model.dal.jdom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.qubership.atp.ei.ntt.settings.model.dal.AssociableFiledStorer;
import org.qubership.atp.ei.ntt.settings.model.dal.support.DaoFactory;
import org.qubership.atp.ei.ntt.settings.store.file.jdom.JDomOptionProcessor;
import org.qubership.atp.ei.ntt.settings.store.option.OptionProcessor;

/**
 * TODO Make summary for this class.
 */
public class JDomStorer implements AssociableFiledStorer {

    private static Logger log = Logger.getLogger(JDomStorer.class);

    private OptionProcessor<Element> processor = new JDomOptionProcessor();
    private Document document;
    private String filePath;
    private final DaoFactory.XmlResource resource;

    private XMLOutputter outputter = null;
    private SAXBuilder builder = null;

    public JDomStorer(DaoFactory.XmlResource resource) {
        this.resource = resource;
    }

    @Override
    public void initAssociations(DaoFactory.ElementDal obj) {
        processor.load(obj, getDocument().getRootElement());
    }

    @Override
    public void setAssociations(DaoFactory.ElementDal obj) {
        processor.save(obj, getDocument().getRootElement());
    }

    @Override
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean writeAs(String filePath) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            getOutputter().output(getDocument(), out);
            return true;
        } catch (IOException e) {
            log.error("Error writing file", e);
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("Error closing stream", e);
                }
            }
        }
    }

    @Override
    public boolean read() {
        try {
            document = getBuilder().build(new File(filePath));
            return true;
        } catch (JDOMException | IOException e) {
            log.error("Error reading file", e);
            return false;
        }
    }

    @Override
    public boolean write() {
        return writeAs(filePath);
    }

    private XMLOutputter getOutputter() {
        if (outputter == null) {
            outputter = new XMLOutputter(Format.getPrettyFormat().setIndent("    "));
        }
        return outputter;
    }

    private SAXBuilder getBuilder() {
        if (builder == null) {
            builder = new SAXBuilder();
        }
        return builder;
    }

    private Document getDocument() {
        if (document == null) {
            document = new Document(new Element(resource.getRootElementName()));
        }
        return document;
    }
}
