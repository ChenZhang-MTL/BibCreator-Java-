

import java.io.*;
import java.util.*;

/**
 * @author chen
 * @description:
 * @date: created in 16:40 2022/12/15
 * @V1.6
 * @ count valid/invalid files. output file name. ask user to enter the name of output file(2 times)
 */
public class BibFileProcess {

    private static List<BibFile> bibFiles = new ArrayList<>();

    private int invalid = 0, total = 0;

    private final static Set<String> BIB_FILE_KEY_WORD = new HashSet<String>() {{
        add("author");
        add("journal");
        add("title");
        add("year");
        add("volume");
        add("number");
        add("pages");
        add("keywords");
        add("doi");
        add("ISSN");
        add("month");
    }};

    public BibFileProcess() {
    }

    public void preProcessFiles(File[] files) {
        System.out.println("start pre process file");
        Long startTime = System.currentTimeMillis();
        for (File file : files) {
            BufferedReader br = null;
            // a map for save file key word

            StringJoiner promJoin = new StringJoiner("\n");
            try {
                br = new BufferedReader(new FileReader(file));
                String line = null;
                BibFile bibFile = new BibFile(file);
                Integer countACM = 0;

                Map<String, String> fileInfos = new HashMap<>();
                while ((line = br.readLine()) != null) {
                    if (line == null || "".equals(line)) {         
                        continue;
                    }
                    // content's start index
                    int cStartIndex = line.indexOf("{");
                    // content's end index
                    int cEndIndex = line.indexOf("}");
                    int indexOfDot = line.indexOf("=");
                    if (indexOfDot == -1) { 								 // If no such value of k exists, then -1 is returned.
                        continue;
                    }
                    //get key word
                    String key = line.substring(0, line.indexOf("="));      
                    if (!BIB_FILE_KEY_WORD.contains(key)) {
                        continue;
                    }
                    // the line is invalid
                    if (cStartIndex == -1 || cEndIndex == -1) {                      // missing "{" or "}"
                        promJoin.add("\"" + key + "\"" + " is invalid!");
                        bibFile.clean();
                        invalid++;
                        break;
                    }
                    if (cEndIndex - cStartIndex == 1) {                              // "{" follows by "}". {}
                        bibFile.clean();
                        promJoin.add("\"" + key + "\"" +" is Empty!");
                        invalid++;
                        break;
                    }
                    String keyContent = line.substring(cStartIndex + 1, cEndIndex);

                    if (key.equals("author")) {
                        String authorIEEE = keyContent.replaceAll(" and", ",");
                        String authorNJ = keyContent.replaceAll("and", "&");
                        String authorACM = null;
                        if (line.contains("and")) {
                            authorACM = keyContent.substring(0, keyContent.indexOf("and") - 1) + " et al";
                        } else {
                            authorACM = keyContent + "et al";
                            // authorACM = keyContent;
                        }
                        
                        fileInfos.put("#authorIEEE", authorIEEE);
                        fileInfos.put("#authorNJ", authorNJ);
                        fileInfos.put("#authorACM", authorACM);

                        countACM++;
                    } else {
                        //parse value and put it into map
                        fileInfos.put("#" + key, keyContent);
                    }
                    fileInfos.put("#countACM", countACM.toString());
                    if (key.equals("month")) {
                        bibFile.addPwIEEE(formatKeyWord("#authorIEEE. \"#title\", #journal , vol.#volume, no.#number, p.#pages, #month #year.\n", fileInfos));
                        bibFile.addPwACM(formatKeyWord("[#countACM] #authorACM. #title. #year. #journal. #volume,#number(#year),#pages. DOI:https://doi.org/#doi.\n", fileInfos));
                        bibFile.addPwNJ(formatKeyWord("#authorNJ. #title. #journal. #volume ,#pages(#year).\n", fileInfos));
 
                        fileInfos.clear();
                    }
                }
                total++;
                bibFiles.add(bibFile);
                printProblem(file, promJoin);
            } catch (FileNotFoundException e) {
                System.out.println("File " + file.getName() + " not found! Program shall terminate now.");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("pre process file finished, use " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void outPutFiles() {
        System.out.println("start out put file");
        Long currentTime = System.currentTimeMillis();
        for (BibFile bibFile : bibFiles) {
            if ("".equals(bibFile.getPwIEEE().toString()) || "".equals(bibFile.getPwACM().toString()) || "".equals(bibFile.getPwNJ().toString())) {
                deleteInvalidFile("./Files/NJ" + bibFile.getSimpleName() + ".json");
                deleteInvalidFile("./Files/IEEE" + bibFile.getSimpleName() + ".json");
                deleteInvalidFile("./Files/ACM" + bibFile.getSimpleName() + ".json");
                continue;
            }
            PrintWriter pwIEEE = null, pwACM = null, pwNJ = null;
            try {

                pwNJ = new PrintWriter(new FileOutputStream("./Files/NJ" + bibFile.getSimpleName() + ".json", true));
                pwNJ.println(bibFile.getPwNJ());

                pwIEEE = new PrintWriter(new FileOutputStream("./Files/IEEE" + bibFile.getSimpleName() + ".json", true));
                pwIEEE.println(bibFile.getPwIEEE());

                pwACM = new PrintWriter(new FileOutputStream("./Files/ACM" + bibFile.getSimpleName() + ".json", true));
                pwACM.println(bibFile.getPwACM());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (pwIEEE != null) {
                    pwIEEE.close();
                }
                if (pwACM != null) {
                    pwACM.close();
                }
                if (pwNJ != null) {
                    pwNJ.close();
                }
            }
        }
        System.out.println("outPutFiles finished, use " + (System.currentTimeMillis() - currentTime) + "ms");
    }


    public void reviewFile(BufferedReader br, String line ,Scanner kb) throws IOException{
        System.out.println("\nPlease enter the name of a file you would like to review");
        String FileReview= kb.next();
        br=new BufferedReader(new FileReader("./Files/" + FileReview));
        while((line=br.readLine())!=null) {
            System.out.println(line);
        }
        System.out.println("Thank you for using BibCreator!");
        System.exit(0);
    }

    public int getInvalid() {
        return invalid;
    }
    public int getTotal() {
        return total;
    }

    private void deleteInvalidFile(String fileName) {
        File ACMFile = new File(fileName);
        if (ACMFile.exists()) {
            ACMFile.delete();
        }
    }

    private static void printProblem(File file, StringJoiner promJoin) {
        if (promJoin.toString() != null && !"".equals(promJoin.toString())) {
            System.out.println("Error: Detected Empty Filed!");
            System.out.println("============================");
            System.out.println("\nProblem detected with file " + file.getName());
            System.out.println(promJoin);
            System.out.println("File is Invalid: Field " + promJoin + " Processing has stopped at this point. Other empty fields may be present as well!\n");
        }
    }

    private static String formatKeyWord(String content, Map<String, String> map) {

        // replace value with key
    	// traversal, map.entrySet 
        for (Map.Entry<String, String> entry : map.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }
        if (content.indexOf("#") == -1) {        // -1, no "#"
            return content;
        }
        for (String value : BIB_FILE_KEY_WORD) {
            content = content.replace("#" + value, "");
        }
        return content;
    }


}
