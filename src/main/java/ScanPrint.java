import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.*;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrinterName;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

class ScanPrint {
    static void print(String filename) throws Exception {

        PDDocument document = PDDocument.load(new File(filename));
        PrintService myPrintService = findPrintService("CUPS-PDF-Printer");
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        job.setPrintService(myPrintService);
        job.print();
    }

    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }
}

//        FileInputStream psStream;
//        try {
//            psStream = new FileInputStream(filename);
//        } catch (FileNotFoundException ex) {
//            System.out.println("File not found");
//            return;
//        }
//        DocFlavor psInFormat = DocFlavor.INPUT_STREAM.PDF;
//        Doc myDoc = new SimpleDoc(psStream, psInFormat, null);
//        PrintRequestAttributeSet job_aset = new HashPrintRequestAttributeSet();
//        AttributeSet lookup_aset = new HashAttributeSet();
//        job_aset.add(MediaSizeName.ISO_A4);
//        job_aset.add(new PageRanges(1, 1));
//        lookup_aset.add(new PrinterName("CUPS-PDF-Printer", null));
//        PrintService[] services = PrintServiceLookup.lookupPrintServices(psInFormat, lookup_aset);
//        System.out.println("Printer Selected " + services[0]);
//        if (services.length > 0) {
//            DocPrintJob job = services[0].createPrintJob();
//            job.print(myDoc, job_aset);
//        }
