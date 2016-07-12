package test.model;

/**
 * Created by Jaising on 2016/7/5.
 * <p>
 * 版本信息
 */
public class Version {

    /**
     * 版本号
     *
     * 0000:无
     * -1： 非法请求
     * -2： 更新成功
     */
    private String v;

    public void setV(String v) {
        this.v = v;
    }

    public String getV() {
        return v;
    }

}
