package tech.qiantong.qknow.module.ext.service.neo4j;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.qiantong.qknow.module.ext.dal.dataobject.extDatasource.ExtDatasourceDO;
import tech.qiantong.qknow.module.ext.service.extDatasource.IExtDatasourceService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多数据源Neo4j管理器
 * 支持根据数据源ID动态创建和管理Neo4j连接
 * 位于EXT模块，直接管理Neo4j连接
 *
 * @author qknow
 * @date 2025-08-19
 */
@Slf4j
@Component
public class MultiDataSourceNeo4jManager {

    @Autowired
    private IExtDatasourceService extDatasourceService;

    // 缓存已创建的连接
    private final Map<Long, Driver> driverCache = new ConcurrentHashMap<>();

    /**
     * 根据数据源ID获取Neo4j Driver
     *
     * @param datasourceId 数据源ID
     * @return Neo4j Driver
     */
    public Driver getDriver(Long datasourceId) {
        if (datasourceId == null) {
            log.warn("数据源ID为空，返回null");
            return null;
        }

        // 检查缓存中是否已有连接
        Driver cachedDriver = driverCache.get(datasourceId);
        if (cachedDriver != null && isConnectionValid(cachedDriver)) {
            log.debug("使用缓存的Neo4j连接，数据源ID: {}", datasourceId);
            return cachedDriver;
        }

        // 创建新连接
        try {
            ExtDatasourceDO datasource = extDatasourceService.getExtDatasourceById(datasourceId);
            if (datasource == null) {
                log.error("数据源不存在，ID: {}", datasourceId);
                return null;
            }

            if (datasource.getType() != 2) { // 2表示Neo4j
                log.error("数据源类型不是Neo4j，ID: {}, 类型: {}", datasourceId, datasource.getType());
                return null;
            }

            // 构建连接URI
            String uri = String.format("neo4j://%s:%d", datasource.getHost(), datasource.getPort());
            
            // 创建Driver
            Driver driver = GraphDatabase.driver(
                uri, 
                AuthTokens.basic(datasource.getUsername(), datasource.getPassword())
            );

            // 测试连接
            try (org.neo4j.driver.Session session = driver.session()) {
                session.run("RETURN 1");
                log.info("Neo4j连接测试成功，数据源ID: {}, URI: {}", datasourceId, uri);
            }

            // 缓存连接
            driverCache.put(datasourceId, driver);
            
            return driver;

        } catch (Exception e) {
            log.error("创建Neo4j连接失败，数据源ID: {}", datasourceId, e);
            return null;
        }
    }

    /**
     * 检查连接是否有效
     */
    private boolean isConnectionValid(Driver driver) {
        try {
            try (org.neo4j.driver.Session session = driver.session()) {
                session.run("RETURN 1");
                return true;
            }
        } catch (Exception e) {
            log.debug("连接测试失败，需要重新创建: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 关闭指定数据源的连接
     */
    public void closeDriver(Long datasourceId) {
        Driver driver = driverCache.remove(datasourceId);
        if (driver != null) {
            try {
                driver.close();
                log.info("已关闭Neo4j连接，数据源ID: {}", datasourceId);
            } catch (Exception e) {
                log.warn("关闭Neo4j连接时出错，数据源ID: {}", datasourceId, e);
            }
        }
    }

    /**
     * 关闭所有连接
     */
    public void closeAllDrivers() {
        log.info("开始关闭所有Neo4j连接，当前连接数: {}", driverCache.size());
        
        for (Map.Entry<Long, Driver> entry : driverCache.entrySet()) {
            try {
                entry.getValue().close();
                log.debug("已关闭Neo4j连接，数据源ID: {}", entry.getKey());
            } catch (Exception e) {
                log.warn("关闭Neo4j连接时出错，数据源ID: {}", entry.getKey(), e);
            }
        }
        
        driverCache.clear();
        log.info("所有Neo4j连接已关闭");
    }

    /**
     * 获取当前连接统计信息
     */
    public Map<String, Object> getConnectionStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("totalConnections", driverCache.size());
        stats.put("cachedDatasourceIds", driverCache.keySet());
        return stats;
    }
}
