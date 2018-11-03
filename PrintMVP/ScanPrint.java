package com.mycompany.spbot;

import java.io.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

public class ScanPrint {

    public static void print(String filename) throws PrintException {
        FileInputStream psStream;
        try {
            psStream = new FileInputStream(filename);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
            return;
        }
        DocFlavor psInFormat = DocFlavor.INPUT_STREAM.PDF;
        Doc myDoc = new SimpleDoc(psStream, psInFormat, null);
        PrintRequestAttributeSet job_aset
                = new HashPrintRequestAttributeSet();
        AttributeSet lookup_aset
                = new HashAttributeSet();
        job_aset.add(MediaSizeName.ISO_A4);
        job_aset.add(new PageRanges(1, 1));
        lookup_aset.add(new PrinterName("CUPS-PDF-Printer", null));
        PrintService[] services
                = PrintServiceLookup.lookupPrintServices(psInFormat, lookup_aset);
        System.out.println("Printer Selected " + services[0]);
        if (services.length > 0) {
            DocPrintJob job = services[0].createPrintJob();
            job.print(myDoc, job_aset);
        }
    }
}
