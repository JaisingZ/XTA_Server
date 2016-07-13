package test.service;

import test.helper.DatabaseHelper;
import test.model.Location;
import java.util.Map;

/**
 * Created by Jaising on 2016/7/13.
 */
public class LocationService {

    /**
     *  创建新位置
     */
    public boolean createLocation(Map<String, Object> fieldMap) {
        return DatabaseHelper.insertEntity(Location.class, fieldMap);
    }

    /**
     * 根据id获得位置
     */
    public Location getLocationById(String id) {
        String sql = "SELECT * FROM Location WHERE id = ?";
        return DatabaseHelper.queryEntity(Location.class, sql, id);
    }

    /**
     * 根据时间戳获得位置
     */
    public Location getLocationByTime(String time) {
        String sql = "SELECT * FROM Location WHERE time = ?";
        return DatabaseHelper.queryEntity(Location.class, sql, time);
    }

}
