package test.service;

import test.helper.DatabaseHelper;
import test.model.Binding;

import java.util.List;
import java.util.Map;


public class BindingService {

    /**
     * 创建绑定信息
     */
    public boolean createBinding(Map<String, Object> fieldMap) {
        return DatabaseHelper.insertEntity(Binding.class, fieldMap);
    }

    /**
     * 获得绑定信息
     */
    public List<Binding> getBinding(String id) {
        String sql = "SELECT * FROM Binding WHERE id = ?";
        return DatabaseHelper.queryEntityList(Binding.class, sql, id);
    }

    /**
     * 删除绑定信息
     */
    public boolean deleteBinding(String id) {
        return DatabaseHelper.deleteEntity(Binding.class, "id", id);
    }
}
