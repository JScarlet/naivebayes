import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;

import java.io.IOException;

/**
 * Created by JScarlet on 2017/4/28.
 */
public class TT4jTest {
    public static void main(String[] args){
        String str = "it is best";
        String[] temp = str.split(" ");
        System.setProperty("treetagger.home", "/TreeTagger");
        TreeTaggerWrapper tt = new TreeTaggerWrapper();
        try {
            tt.setModel("/TreeTagger/lib/english-utf8.par:iso8859-1");

            tt.setHandler((o, s, s1) -> System.out.println(o + "\t" + s + "\t" + s1));
        //    tt.process(asList(new String[]{"your", "cannot", "simpler", "exceptions"}));
            tt.process(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TreeTaggerException e) {
            e.printStackTrace();
        }finally {
            tt.destroy();
        }
    }
}
