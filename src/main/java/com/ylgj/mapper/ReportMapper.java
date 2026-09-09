package com.ylgj.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 报表聚合查询 Mapper（自定义 SQL 聚合，避免全表拉取后在内存统计）。
 *
 * @author ylgj
 */
public interface ReportMapper {

    /**
     * 统计每个套餐的预约订单数（按数量降序）。
     *
     * @return 每行包含 setmealId 与 cnt 两个字段
     */
    @Select("SELECT setmeal_id AS setmealId, COUNT(*) AS cnt FROM t_order GROUP BY setmeal_id ORDER BY cnt DESC")
    List<Map<String, Object>> countOrderGroupBySetmeal();
}
