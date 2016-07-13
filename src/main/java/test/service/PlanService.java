package test.service;

import test.helper.DatabaseHelper;
import test.model.Plan;

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
     * 根据id获得计划（列表）
     */
    public List<Plan> getPlanById(String id) {
        String sql = "SELECT * FROM Plan WHERE id = ?";
        return DatabaseHelper.queryEntityList(Plan.class, sql, id);
    }

}
