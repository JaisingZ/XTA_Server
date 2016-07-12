package test.service;

import test.helper.DatabaseHelper;
import test.model.Version;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供版本获取更新服务
 */
public class VersionService {

    /**
     * 创建版本信息
     */
    public boolean createVersion(String string_version) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("v", string_version);
        Version class_version = new Version();
        class_version.setV(string_version);
        return DatabaseHelper.insertEntity(Version.class, map);
    }

    /**
     * 获得版本信息
     */
    public Version getVersion() {
        String sql = "SELECT v FROM Version";
        return DatabaseHelper.queryEntity(Version.class, sql);
    }

    /**
     * 删除版本信息
     */
    public boolean deleteVersion(String string_version) {
        return DatabaseHelper.deleteEntity(Version.class, "v", string_version);
    }

    /**
     * 更新版本信息
     */
    public boolean updateVersion(String oldVersion, String newVersion) {
        if (deleteVersion(oldVersion) && createVersion(newVersion)) {
            return true;
        }else{
            return false;
        }
    }
}
