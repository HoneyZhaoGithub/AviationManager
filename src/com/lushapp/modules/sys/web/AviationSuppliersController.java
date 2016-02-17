/**
 *  Copyright (c) 2014 http://www.lushapp.wang
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.lushapp.modules.sys.web;

import com.google.common.collect.Lists;
import com.lushapp.common.model.Combobox;
import com.lushapp.common.model.Datagrid;
import com.lushapp.common.model.Result;
import com.lushapp.common.orm.Page;
import com.lushapp.common.orm.PropertyFilter;
import com.lushapp.common.orm.entity.StatusState;
import com.lushapp.common.orm.hibernate.EntityManager;
import com.lushapp.common.utils.StringUtils;
import com.lushapp.common.utils.collections.Collections3;
import com.lushapp.common.utils.mapper.JsonMapper;
import com.lushapp.common.web.springmvc.BaseController;
import com.lushapp.modules.sys._enum.SexType;
import com.lushapp.modules.sys.entity.AviationBuyers;
import com.lushapp.modules.sys.entity.AviationOrder;
import com.lushapp.modules.sys.entity.AviationSuppliers;
import com.lushapp.modules.sys.service.AviationBuyersManager;
import com.lushapp.modules.sys.service.AviationOrderManager;
import com.lushapp.modules.sys.service.AviationSuppliersManager;
import com.lushapp.utils.SelectType;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 供应商AviatioinSuppliers管理 Controller层.
 *
 * @author honey.zhao@aliyun.com  
 * @date 2014-10-21 上午12:20:13
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping(value = "/sys/aviationSuppliers")
public class AviationSuppliersController extends BaseController<AviationSuppliers,Long> {


    @Autowired
    private AviationOrderManager aviationOrderManager;

    @Autowired
    private AviationBuyersManager aviationBuyersManager;

    @Autowired
    private AviationSuppliersManager aviationSuppliersManager;

    @Override
    public EntityManager<AviationSuppliers, Long> getEntityManager() {
        return aviationSuppliersManager;
    }


    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/aviationSuppliers";
    }

    /**
     * @param aviationSuppliers
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"_input"})
    public String input(@ModelAttribute("model") AviationSuppliers aviationSuppliers) throws Exception {
        return "modules/sys/aviationSuppliers-input";
    }

    /**
     *
     * @param aviationSuppliers
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"_view"})
    public String view(@ModelAttribute("model") AviationSuppliers aviationSuppliers) throws Exception {
        return "modules/sys/aviationSuppliers-view";
    }


    @RequestMapping(value = {"_remove"})
    @ResponseBody
    @Override
    public Result remove(@RequestParam(value = "ids", required = false) List<Long> ids) {
        Result result;
        aviationSuppliersManager.deleteByIds(ids);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
    }

    /**
     * 保存.
     */
    @RequestMapping(value = {"save"})
    @ResponseBody
    public Result save(@ModelAttribute("model") AviationSuppliers aviationSuppliers) {
        Result result = null;
        //-------校验-begin-----


        //-------校验-end-------
        aviationSuppliersManager.saveEntity(aviationSuppliers);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
    }

    /**
     * 用户combogrid所有
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"combogridAll"})
    @ResponseBody
    public String combogridAll() {
        List<PropertyFilter> filters = Lists.newArrayList();
        filters.add(new PropertyFilter("EQI_status", StatusState.normal.getValue().toString()));
        List<AviationSuppliers> aviationSupplierslist = aviationSuppliersManager.find(filters, "id", "asc");
        Datagrid<AviationSuppliers> dg = new Datagrid<AviationSuppliers>(aviationSupplierslist.size(), aviationSupplierslist);
        return JsonMapper.getInstance().toJson(dg,AviationSuppliers.class,new String[]{"id","loginName","name","sexView"});
    }


    /**
     * combogrid
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"combogrid"})
    @ResponseBody
    public Datagrid<AviationSuppliers> combogrid(@RequestParam(value = "ids", required = false)List<Long> ids, String loginNameOrName, Integer rows) throws Exception {
        Criterion statusCriterion = Restrictions.eq("status", StatusState.normal.getValue());
        Criterion[] criterions = new Criterion[0];
        criterions = (Criterion[]) ArrayUtils.add(criterions, 0, statusCriterion);
        Criterion criterion = null;
        if (Collections3.isNotEmpty(ids)) {
            //in条件
            Criterion inCriterion = Restrictions.in("id", ids);

            if (StringUtils.isNotBlank(loginNameOrName)) {
                Criterion loginNameCriterion = Restrictions.like("loginName", loginNameOrName, MatchMode.ANYWHERE);
                Criterion nameCriterion = Restrictions.like("name", loginNameOrName, MatchMode.ANYWHERE);
                Criterion criterion1 = Restrictions.or(loginNameCriterion, nameCriterion);
                criterion = Restrictions.or(inCriterion, criterion1);
            } else {
                criterion = inCriterion;
            }
            //合并查询条件
            criterions = (Criterion[]) ArrayUtils.add(criterions, 0, criterion);
        } else {
            if (StringUtils.isNotBlank(loginNameOrName)) {
                Criterion loginNameCriterion = Restrictions.like("loginName", loginNameOrName, MatchMode.ANYWHERE);
                Criterion nameCriterion = Restrictions.like("name", loginNameOrName, MatchMode.ANYWHERE);
                criterion = Restrictions.or(loginNameCriterion, nameCriterion);
                //合并查询条件
                criterions = (Criterion[]) ArrayUtils.add(criterions, 0, criterion);
            }
        }

        //分页查询
        Page<AviationSuppliers> p = new Page<AviationSuppliers>(rows);//分页对象
        p = aviationSuppliersManager.findByCriteria(p, criterions);
        Datagrid<AviationSuppliers> dg = new Datagrid<AviationSuppliers>(p.getTotalCount(), p.getResult());
        return dg;
    }



}
