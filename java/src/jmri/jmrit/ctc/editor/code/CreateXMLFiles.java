/*
    The sole purpose of this class is to create the text file that can be
    copied into a JMRI .xml file to ease the creation of a CTC panel from
    scratch.
 */
package jmri.jmrit.ctc.editor.code;

import java.io.PrintWriter;

/**
 * @author Gregory J. Bedlek Copyright (C) 2018, 2019
 */
public class CreateXMLFiles {

    public static void generateProlog(PrintWriter printWriter) {
        printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");  // NOI18N
        printWriter.println("<?xml-stylesheet href=\"/xml/XSLT/panelfile-2-9-6.xsl\" type=\"text/xsl\"?>"); // NOI18N
        printWriter.println("<layout-config xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://jmri.org/xml/schema/layout-2-9-6.xsd\">");   // NOI18N
    }

    public static void generateEpilogue(PrintWriter printWriter) {
        printWriter.println("</layout-config>");    // NOI18N
    }
}
