package com.jaagro.report.biz.mapper.report;

import com.jaagro.report.api.entity.DriverOrderMonthly;

public interface DriverOrderMonthlyMapper {
    /**
     *
     * @mbggenerated 2018-11-26
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *
     * @mbggenerated 2018-11-26
     */
    int insert(DriverOrderMonthly record);

    /**
     *
     * @mbggenerated 2018-11-26
     */
    int insertSelective(DriverOrderMonthly record);

    /**
     *
     * @mbggenerated 2018-11-26
     */
    DriverOrderMonthly selectByPrimaryKey(Integer id);

    /**
     *
     * @mbggenerated 2018-11-26
     */
    int updateByPrimaryKeySelective(DriverOrderMonthly record);

    /**
     *
     * @mbggenerated 2018-11-26
     */
    int updateByPrimaryKey(DriverOrderMonthly record);
}