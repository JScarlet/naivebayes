
import java.util.Collections;
import java.util.List;

/**
 * Created by JScarlet on 2017/3/9.
 */
public class Utils {
    public static List<CodeSnippet> shuffle(List<CodeSnippet> codeSnippets){
        Collections.shuffle(codeSnippets);
        return codeSnippets;
    }

    public static String removeTag(String str){
        if(str.contains("&#xA;")){
            str = str.replace("&#xA;", " ");
        }
        if(str.contains("&#xD;")){
            str = str.replace("&#xD;", "");
        }
        if(str.contains("<pre>")){
            str = str.replace("<pre>", "");
        }
        if(str.contains("</pre>")){
            str = str.replace("</pre>", "");
        }
        if(str.contains("<p>")){
            str = str.replace("<p>", "");
        }
        if(str.contains("</p>")){
            str = str.replace("</p>", "");
        }
        if(str.contains("<code>")){
            str = str.replace("<code>", "");
        }
        if(str.contains("</code>")){
            str = str.replace("</code>", "");
        }
        if(str.contains("<br>")){
            str = str.replace("<br>", "");
        }
        if(str.contains("<strong>")){
            str = str.replace("<strong>", "");
        }
        if(str.contains("</strong>")){
            str = str.replace("</strong>", "");
        }
        if(str.contains("<em>")){
            str = str.replace("<em>", "");
        }
        if(str.contains("</em>")){
            str = str.replace("</em>", "");
        }
        if(str.contains("<blockquote>")){
            str = str.replace("<blockquote>", "");
        }
        if(str.contains("</blockquote>")){
            str = str.replace("</blockquote>", "");
        }
        if(str.contains("<ol>")){
            str = str.replace("<ol>", "");
        }
        if(str.contains("<li>")){
            str = str.replace("<li>", "");
        }
        if(str.contains("</li>")){
            str = str.replace("</li>", "");
        }
        if(str.contains("<hr>")){
            str = str.replace("<hr>", "");
        }
        return str;
    }

    public static String removeHrefTag(String str){
        String sub = str;
        while(sub.contains("<a href")){
            int index = str.indexOf("<a href");
            sub = str.substring(index);
            int sufIndex = sub.indexOf(">");
            String href = str.substring(index, index + sufIndex + 1);
//            System.out.println(href);
            str = str.replace(href, "").replace("</a>", "");
            sub = str;
        }
        return str;
    }
}
