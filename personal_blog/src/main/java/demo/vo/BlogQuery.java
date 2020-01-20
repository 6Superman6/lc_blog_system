package demo.vo;

/**
 * 博客查询
 */
public class BlogQuery {

    private String title;          // 标题
    private Long typeId;           // 分类id
    private boolean recommend;   // 推荐开启

    public BlogQuery() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }
}
