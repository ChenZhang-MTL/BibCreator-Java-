import java.io.File;
import java.util.StringJoiner;

/**
 * @author chen
 * @description:
 * @date: created in 16:39 2022/12/15
 * @V1.6
 * @ count valid/invalid files. output file name. ask user to enter the name of output file(2 times)
 */
public class BibFile {

    /**
     * origin file
     */
    private File originFile;
    private StringJoiner pwIEEE = new StringJoiner("\n");
    private StringJoiner pwACM = new StringJoiner("\n");
    private StringJoiner pwNJ = new StringJoiner("\n");

    public BibFile(File originFile) {
        this.originFile = originFile;
    }

    public String getFileName(){
        String fileName = originFile.getName();
        return fileName.substring(0,fileName.lastIndexOf("."));
    }

    public String getSimpleName(){
        return getFileName().replace("Latex","");
    }

    public void clean(){
        pwIEEE = new StringJoiner("\n");
        pwACM = new StringJoiner("\n");
        pwNJ = new StringJoiner("\n");
    }

    public StringJoiner getPwIEEE() {
        return pwIEEE;
    }

    public StringJoiner getPwACM() {
        return pwACM;
    }

    public StringJoiner getPwNJ() {
        return pwNJ;
    }

    public void addPwACM(String pwACM) {
       this.pwACM.add(pwACM);
    }

    public void addPwIEEE(String pwIEEE) {
        this.pwIEEE.add(pwIEEE);
    }

    public void addPwNJ(String pwNJ) {
        this.pwNJ.add(pwNJ);
    }
}
