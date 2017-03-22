import java.io.*;
import java.util.*;

/**
 * Created by JScarlet on 2017/3/15.
 */
public class BayesModel {
    public BayesModel(){}

    public void extractMajorWords(List<CodeSnippet> codeSnippets){
        HashMap<String, int[]> wordCount = new HashMap<>();
        HashMap<String, Integer> codeSet = new HashMap<>();
        int[] indexWordCount;
        int[] indexCount = new int[8];
        String str = "";
        System.out.println("正在提取特征词...");
        for (CodeSnippet codeSnippet : codeSnippets) {
            int type = codeSnippet.getType();
            str = codeSnippet.getAboveContext() + " " + codeSnippet.getBelowContext() + " " + codeSnippet.getTitle();
//            System.out.println(str);
            if(!codeSet.keySet().contains(str)){
                codeSet.put(str, 1);

                String[] words = str.split("\\s+");
                for(String word: words){
                    word = word.toLowerCase();
                    indexCount[type]++;
                    if(wordCount.containsKey(word)){
                        int[] temp = wordCount.get(word);
                        temp[type]++;
                        wordCount.put(word, temp);
                    }else {
                        indexWordCount = new int[8];
                        indexWordCount[type] = 1;
                        wordCount.put(word, indexWordCount);
                    }
                }
       /*     for(String key: wordCount.keySet()){
                int length = wordCount.get(key).length;
                for(int i = 1; i < length; i++){
                    System.out.println("word: " + key + " in type " + i + " count is " + wordCount.get(key)[i]);
                }
            }*/
            }
        }
        /*for(int i = 1; i < indexCount.length; i++){
            System.out.print(indexCount[i] + " ");
        }
        System.out.println();*/
        System.out.println("计算互信息...");
        calculateMutualInfo(wordCount, indexCount);
        System.out.println("extracting finished");
    }

    private double mutualInfo(int n, int nij, int ni, int nj){
//        System.out.println("n: " + n + " nij: " + nij + " ni: " + ni + " nj: " + nj);
        return ((nij * 1.0/ n) * (Math.log(n * (nij + 1) * 1.0 / (ni * nj)) / Math.log(2)));
    }

    private void calculateMutualInfo(HashMap<String, int[]> wordCount, int[] indexCount){
        HashMap<String, double[]> muInfoMap = new HashMap<>();

        int totalCount = 0;
        for (int i = 1; i < indexCount.length; i++) {
            totalCount += indexCount[i];
        }
        for(String key : wordCount.keySet()){
            double[] indexMuInfo = new double[8];
            int[] temp = wordCount.get(key);
            int singleWordCount = 0;
            for (int i = 1; i < temp.length; i++) {
                singleWordCount += temp[i];
            }

            for(int i = 1; i < temp.length; i++){
                int n11 = temp[i];
                int n10 = singleWordCount - n11;
                int n01 = indexCount[i] - n11;
                int n00 = totalCount - n11 - n10 - n01;
//                System.out.println(key);
//                System.out.println("n11: " + n11 + " n10: " + n10 + " n01: " + n01 + " n00: " + n00);
//                double mutualInfo = mutualInfo(totalCount, n11, n10 + n11, n01 + n11) + mutualInfo(totalCount, n10, n10 + n11, n00 + n10) + mutualInfo(totalCount, n01, n01 + n11, n01 + n00) + mutualInfo(totalCount, n00, n00 + n10, n00 + n01);
                double mutualInfo = mutualInfo(totalCount, n11, n10 + n11, n01 + n11);
                indexMuInfo[i] = mutualInfo;
                muInfoMap.put(key, indexMuInfo);
            }
        }
        List<List<String>> sortedWordsList = new ArrayList<>();
        for(int i = 1; i < indexCount.length; i++){
//            System.out.println("calculate the type " + i + " code now");
            String[][] sortedWord = wordSort(muInfoMap, i);

    /*        for(int j = 0; j < sortedWord.length; j++){
                System.out.println("frequence: " + sortedWord[j][0] + " word: " + sortedWord[j][1]);
            }*/

            List<String> indexSortedWord = new ArrayList<>();

            if(sortedWord.length > 50){
                for(int j = 0; j < 50; j++){
                    indexSortedWord.add(sortedWord[j][1]);
                }
            }else {
                for(int j = 0; j < sortedWord.length; j++){
                    indexSortedWord.add(sortedWord[j][1]);
                }
            }
            sortedWordsList.add(indexSortedWord);
            System.out.println("将特征词写入文件...");
            writeFeatureInFile(sortedWordsList);
        }
    }

    private String[][] wordSort(HashMap<String, double[]> muInfoMap, int index){
        int count = 0;
        String[] temp;
        String[][] sortedMuInfo = new String[muInfoMap.keySet().size()][2];
        for(String key: muInfoMap.keySet()){
            sortedMuInfo[count][0] = muInfoMap.get(key)[index] + "";
            sortedMuInfo[count][1] = key;
            count++;
        }

        for(int i = 0; i < sortedMuInfo.length - 1; i++){
            for(int j = i + 1; j < sortedMuInfo.length; j++){
                if(Double.parseDouble(sortedMuInfo[j][0]) > Double.parseDouble(sortedMuInfo[i][0])){
                    temp = sortedMuInfo[j];
                    sortedMuInfo[j] = sortedMuInfo[i];
                    sortedMuInfo[i] = temp;
                }
            }
        }
        return sortedMuInfo;
    }

    private void writeFeatureInFile(List<List<String>> featureWords){
        File file = new File("feature words.txt");
        FileOutputStream fos;
        PrintWriter pw;
        try {
            if(!file.exists()){
                file.createNewFile();
                System.out.println("创建文件成功");
            }

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            for(int i = 0; i < featureWords.size(); i++){
                for(String str : featureWords.get(i)){
                    pw.print(str + " ");
                }
                pw.println();
            }
            pw.flush();
            pw.close();
            fos.close();

            System.out.println("特征词写入完毕");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Set<String> loadFeatureWords(String filename){
        System.out.println("开始导入特征词...");
        try {
            File file = new File(filename);
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String line;
                Set<String> set = new HashSet<>();
                while((line = bufferedReader.readLine()) != null){
//                    System.out.println(line);
                    String[] temp = line.split("\\s+");
                    Collections.addAll(set, temp);
                }
                read.close();

            /*    for(String str: set){
                    System.out.print(str + " ");
                }*/

                System.out.println("导入特征词完毕");
                return set;
            }else {
                System.out.println("找不到文件");
            }

        }catch (Exception e){
            System.out.println("读取文件出错");
            e.printStackTrace();
        }
        return null;
    }

    public void trainModel(Set<String> featureWords, List<CodeSnippet> codeSnippets){
        HashMap<String, int[]> wordCount = new HashMap<>();
        HashMap<String, Integer> codeSet = new HashMap<>();
        int[] indexWordCount;
        int[] indexCount = new int[8];
        String str;
        System.out.println("开始训练模型...");
        for(CodeSnippet codeSnippet : codeSnippets){
            int type = codeSnippet.getType();
            str = codeSnippet.getAboveContext() + " " + codeSnippet.getBelowContext() + " " + codeSnippet.getTitle();
            if(!codeSet.keySet().contains(str)){
                codeSet.put(str, 1);

                String[] words = str.split("\\s+");
                for(String word: words){
                    word = word.toLowerCase();
                    if(featureWords.contains(word)){
                        indexCount[type]++;
                        if(wordCount.containsKey(word)){
                            int[] temp = wordCount.get(word);
                            temp[type]++;
                            wordCount.put(word, temp);
                        }else {
                            indexWordCount = new int[8];
                            indexWordCount[type] = 1;
                            wordCount.put(word, indexWordCount);
                        }
                    }
                }
            }

        }
        System.out.println("训练完毕，开始写入文件中...");
        writeModelInFile(wordCount, indexCount);
    }

    private void writeModelInFile(HashMap<String, int[]> wordCount, int[] indexCount){
        File file = new File("trainModel.txt");
        FileOutputStream fos;
        PrintWriter pw;
        double score;

        try {
            if(!file.exists()){
                file.createNewFile();
                System.out.println("创建文件成功");
            }

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);

            for(String word : wordCount.keySet()){
                pw.print(word + " ");
                for(int i = 1; i < wordCount.get(word).length; i++){
                    score = (wordCount.get(word)[i] + 1) * 1.0 / (indexCount[i] + wordCount.size());
                    pw.print(score + " ");
                }
                pw.println();
            }

            pw.flush();
            pw.close();
            fos.close();
            System.out.println("写入完毕");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, double[]> loadModel(String filename){
        HashMap<String, double[]> model = new HashMap<>();
        System.out.println("导入bayes模型...");
        try{
            File file = new File(filename);
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String line;
                while((line = bufferedReader.readLine()) != null){
                    String[] wordScores = line.split("\\s+");
                    double[] scores = new double[8];
                    String word = wordScores[0];
                    for(int i = 1; i < wordScores.length; i++){
                        scores[i] = Double.parseDouble(wordScores[i]);
                    }
                    model.put(word, scores);
                }
                System.out.println("导入成功");
                return model;
            }else {
                System.out.println("找不到文件");
            }
        }catch (Exception e){
            System.out.println("读取文件出错");
            e.printStackTrace();
        }

        return null;
    }

    public void predict(HashMap<String, double[]> model, List<CodeSnippet> testSet, Set<String> featureWords){
        int rightCount = 0;
        int totalCount = 0;

        System.out.println("开始预测...");

        for(CodeSnippet codeSnippet: testSet){
            double[] preValues = new double[8];
            int type = codeSnippet.getType();
            String str = codeSnippet.getAboveContext() + " " + codeSnippet.getBelowContext() + " " + codeSnippet.getTitle();
            String[] words = str.split("\\s+");
            for(String word: words){
                if(featureWords.contains(word)){
                    double[] tempScore = model.get(word);
                    for(int i = 1; i < tempScore.length; i++){
                        preValues[i] += Math.log(tempScore[i]);
                    }
                }
            }

            int preIndex = max(preValues);
            if(preIndex == type){
                rightCount++;
            }
            totalCount++;
        }
        System.out.println("预测完毕");
        double result = rightCount *1.0 / totalCount;
        System.out.println("在" + totalCount + "个样本中，共有" + rightCount + "个预测正确，正确率为" + result);
    }

    private int max(double[] values){
        int maxIndex = 0;
        double max = -Double.MAX_VALUE;
        for(int i = 1; i < values.length; i++){
            if(values[i] > max){
                max = values[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}
