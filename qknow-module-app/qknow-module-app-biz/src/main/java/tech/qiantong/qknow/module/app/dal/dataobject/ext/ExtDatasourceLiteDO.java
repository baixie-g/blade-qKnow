package tech.qiantong.qknow.module.app.dal.dataobject.ext;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ext_datasource")
public class ExtDatasourceLiteDO {
    @TableId
    private Long id;
    private String name;
    private Integer type;
    private String host;
    private Integer port;
    private String databaseName;
    private String username;
    private String password;
    private Integer status;
}


