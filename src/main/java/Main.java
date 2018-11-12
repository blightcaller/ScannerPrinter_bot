import javax.print.PrintException;

public class Main {
    public static void main(String[] args) throws Exception {
        String filename;
        if (args.length < 1) {
            filename = "file.pdf";
        } else {
            filename = args[0];
        }
        ScanPrint.print(filename);
    }
}
