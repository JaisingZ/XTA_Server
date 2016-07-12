package test.model;

/**
 * 用户类
 */
public class User {

    /**
     * 用户手机作为PK
     */
    private String id;

    /**
     * 用户昵称
     */
    private String name;

    /**
     * 用户头像
     */
    private String head_img;

    /**
     * 设备标识，安卓44位
     */
    private String device_token;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getDevice_token() {
        return device_token;
    }
}

