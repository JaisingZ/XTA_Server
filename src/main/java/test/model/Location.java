package test.model;

/**
 * Created by Jaising on 2016/7/13.
 *
 * 位置类
 * 注意：共用一张表时，id主键要忽略冲突
 */
public class Location {

    /**
     * 用户
     */
    private String id;

    /**
     * 位置地名
     */
    private String space;

    /**
     * 经度
     */
    private String lat;

    /**
     * 纬度
     */
    private String lot;

    /**
     * 时间戳
     */
    private String timestamp;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getSpace() {
        return space;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {
        return lat;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getLot() {
        return lot;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
