package test.model;

/**
 * Created by Jaising on 2016/7/13.
 */
public class Plan {


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBindid() {
        return bindid;
    }

    public void setBindid(String bindid) {
        this.bindid = bindid;
    }

    public String getSpace_arrival() {
        return space_arrival;
    }

    public void setSpace_arrival(String space_arrival) {
        this.space_arrival = space_arrival;
    }

    public String getSpace_start() {
        return space_start;
    }

    public void setSpace_start(String space_start) {
        this.space_start = space_start;
    }

    public String getLat_start() {
        return lat_start;
    }

    public void setLat_start(String lat_start) {
        this.lat_start = lat_start;
    }

    public String getLot_start() {
        return lot_start;
    }

    public void setLot_start(String lot_start) {
        this.lot_start = lot_start;
    }

    public String getLat_arrival() {
        return lat_arrival;
    }

    public void setLat_arrival(String lat_arrival) {
        this.lat_arrival = lat_arrival;
    }

    public String getLot_arrival() {
        return lot_arrival;
    }

    public void setLot_arrival(String lot_arrival) {
        this.lot_arrival = lot_arrival;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_arrival() {
        return time_arrival;
    }

    public void setTime_arrival(String time_arrival) {
        this.time_arrival = time_arrival;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * 指定计划人
     */
    private String id;

    /**
     * 被计划人
     */
    private String bindid;

    /**
     * 出发地名
     */
    private String space_start;

    /**
     * 到达地点
     */
    private String space_arrival;

    /**
     * 出发经度
     */
    private String lat_start;

    /**
     * 出发纬度
     */
    private String lot_start;

    /**
     * 到达经度
     */
    private String lat_arrival;

    /**
     * 到达纬度
     */
    private String lot_arrival;

    /**
     * 出发时间戳
     */
    private String time_start;

    /**
     * 到达时间戳
     */
    private String time_arrival;

    /**
     * 备注
     */
    private String remark;

    /**
     * 级别
     */
    private String grade;


}
