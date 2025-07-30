package tech.qiantong.qknow.module.ext.convert.extEntityPool;

import java.util.*;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolRespVO;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolSaveReqVO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extEntityPool.ExtEntityPoolDO;

/**
 * 实体池 Convert
 *
 * @author qknow
 */
@Mapper
public interface ExtEntityPoolConvert {

    ExtEntityPoolConvert INSTANCE = Mappers.getMapper(ExtEntityPoolConvert.class);

    ExtEntityPoolDO convert(ExtEntityPoolSaveReqVO bean);

    ExtEntityPoolRespVO convert(ExtEntityPoolDO bean);

    List<ExtEntityPoolRespVO> convertList(List<ExtEntityPoolDO> list);

    ExtEntityPoolDO convert(ExtEntityPoolRespVO bean);

} 