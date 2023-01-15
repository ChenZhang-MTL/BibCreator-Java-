import java.io.*;
import java.util.Scanner;

/**
 * @author chen
 * @description:
 * @date: created in 16:21 2022/12/15
 * @V1.6
 * @ count valid/invalid files. output file name. ask user to enter the name of output file(2 times)
 */
public class BibFileProcessTest {

    static Scanner kb = null;

    public static void main(String[] args) {
        System.out.println("------------Welcome to BibFileProcessTest------------");
        //foreach file in files folder's
        File dir = new File("./Files");
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Files is not exists or not any .bib file,please put some file ");
            return;
        }
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".bib");
            }
        };
        File[] files = dir.listFiles(fileFilter);
        BibFileProcess bibFileProcess = new BibFileProcess();
        bibFileProcess.preProcessFiles(files);
        bibFileProcess.outPutFiles();
        
        BufferedReader br = null;
        String line = null;

        System.out.println("A total of "+bibFileProcess.getInvalid()+" files were invalid. There are only "+(bibFileProcess.getTotal() -bibFileProcess.getInvalid())+" valid files processed");

        //        Here we call and handle the function that scans and prints the files to review
        try {
            kb = new Scanner(System.in);
            bibFileProcess.reviewFile(br, line, kb);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open input file. File does not exist!");
            try {
                bibFileProcess.reviewFile(br, line, kb);
            } catch (FileNotFoundException ex) {
                System.out.println("Could not open input file. File does not exist!");
                System.out.println("Will now terminate program!");
                System.exit(0);
            } catch (IOException ex) {
                System.out.println("IO Exception occurred! Will now terminate program!");
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("IO Exception occurred! Will now terminate program!");
            System.exit(0);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ;
            }
        }
    }
}
