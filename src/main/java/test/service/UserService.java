package test.service;

import test.helper.DatabaseHelper;
import test.model.User;

import java.util.List;
import java.util.Map;

/**
 * 提供用户数据服务
 */
public class UserService {

    /**
     * 获取用户列表
     */
    public List<User> getUserList() {
        String sql = "SELECT * FROM User";
        return DatabaseHelper.queryEntityList(User.class, sql);
    }

    /**
     * 获取用户
     */
    public User getUser(String id) {
        String sql = "SELECT * FROM User WHERE id = ?";
        return DatabaseHelper.queryEntity(User.class, sql, id);
    }

    /**
     * 创建用户
     */
    public boolean createUser(Map<String, Object> fieldMap) {
        return DatabaseHelper.insertEntity(User.class, fieldMap);
    }

    /**
     * 更新用户
     */
    public boolean updateUser(String key, String value, Map<String, Object> fieldMap) {
        return DatabaseHelper.updateEntity(User.class, key, value, fieldMap);
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(String id) {
        return DatabaseHelper.deleteEntity(User.class, "id", id);
    }
}
