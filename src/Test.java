import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by JScarlet on 2017/3/9.
 */
public class Test {
    public static void main(String[] args){
        List<CodeSnippet> codeSnippets;
        List<CodeSnippet> trainSet = new ArrayList<>();
        List<CodeSnippet> testSet = new ArrayList<>();
        int[] index = new int[8];
        int size = 800;
        Test test = new Test();
        for(int mm = 0; mm < 1; mm++){
            codeSnippets = Utils.shuffle(test.readData());
//        System.out.println("this is train set id:");
            for(int i = 0; i < size; i++){
                trainSet.add(codeSnippets.get(i));
//            System.out.println(trainSet.get(i).getId());
            }
            for(CodeSnippet code: trainSet){
                for(int i = 1; i < index.length; i++){
                    if(code.getType() == i){
                        index[i]++;
                    }
                }
            }

            for(int i = 1; i < index.length; i++){
                System.out.println("type " + i + " number: " + index[i]);
            }

//        System.out.println("this is test set id:");
            for(int i = size; i < codeSnippets.size(); i++){
                testSet.add(codeSnippets.get(i));
//            System.out.println(testSet.get(i - size).getId());
            }

            BayesModel model = new BayesModel();
            model.extractMajorWords(trainSet);
            Set<String> featureWords = model.loadFeatureWords("feature words.txt");
            model.trainModel(featureWords);
            HashMap<String, double[]> featureScores = model.loadModel("trainModel.txt");
            model.predict(featureScores,testSet, featureWords);
        }
    }

    private List<CodeSnippet> readData(){
        List<CodeSnippet> codeSnippetList = new ArrayList<>();
        CodeSnippet codeSnippet;
        ResultSet ret;
        String sql = "select * from code_snippets where Type <= 8 and Type >= 1";
        DBHelper dbHelper = new DBHelper(sql);

        try {
            ret = dbHelper.pst.executeQuery();
            int id = 0;
            while(ret.next()){
                id++;
                int postType = ret.getInt(2);
                String code = Utils.removeTag(ret.getString(3)).trim();
                String aboveContext = Utils.removeHrefTag(Utils.removeTag(ret.getString(4)).trim());
                String belowContext = Utils.removeHrefTag(Utils.removeTag(ret.getString(5)).trim());
                String title = ret.getString(6);
                int type = ret.getInt(8);

           /*     System.out.println("the NO." + id);
                System.out.println("postType: " + postType);
                System.out.println("code: " + code);
                System.out.println("aboveContext: " + aboveContext);
                System.out.println("belowContext: " + belowContext);
                System.out.println("title: " + title);
                System.out.println("type: " + type);
                System.out.println();*/

                codeSnippet = new CodeSnippet();
                codeSnippet.setId(id);
                codeSnippet.setPostType(postType);
                codeSnippet.setCodeSnippet(code);
                codeSnippet.setAboveContext(aboveContext);
                codeSnippet.setBelowContext(belowContext);
                codeSnippet.setTitle(title);
                codeSnippet.setType(type);

                codeSnippetList.add(codeSnippet);
            }
            ret.close();
            dbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codeSnippetList;
    }
}
