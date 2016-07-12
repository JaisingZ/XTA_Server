package test.model;

/**
 * Created by Jaising on 2016/7/10.
 */
public class Binding {

    /**
     * 绑定用户id
     */
    private String id;

    /**
     * 被绑定用户id
     */
    private String bindid;


    public void setBindid(String bindid) {
        this.bindid = bindid;
    }

    public String getBindid() {
        return bindid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
