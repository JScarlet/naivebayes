/**
 * Created by JScarlet on 2017/3/9.
 */
public class CodeSnippet {
    private int id;
    private int postType;
    private String codeSnippet;
    private String aboveContext;
    private String belowContext;
    private String title;
    private int type;

    public CodeSnippet(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public String getAboveContext() {
        return aboveContext;
    }

    public void setAboveContext(String aboveContext) {
        this.aboveContext = aboveContext;
    }

    public String getBelowContext() {
        return belowContext;
    }

    public void setBelowContext(String belowContext) {
        this.belowContext = belowContext;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
