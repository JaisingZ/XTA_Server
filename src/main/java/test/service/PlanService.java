package test.service;

import test.helper.DatabaseHelper;
import test.model.Plan;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Map;

/**
 * Created by Jaising on 2016/7/13.
 */
public class PlanService {

    /**
     * 创建新计划
     */
    public boolean createPlan(Map<String, Object> fieldMap) {
        return DatabaseHelper.insertEntity(Plan.class, fieldMap);
    }

    /**
     * 根据id获得计划
     */
    public Plan getPlanById(String id) {
        String sql = "SELECT * FROM Plan WHERE id = ?";
        return DatabaseHelper.queryEntity(Plan.class, sql, id);
    }

    /**
     * 根据id删除计划
     */
    public boolean deletePlanById(String id) {
        return DatabaseHelper.deleteEntity(Plan.class, "id", id);
    }

}
