/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.aop;

import com.skyeye.common.util.SpringUtils;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Stack;

/**
 *
 * @ClassName: ActivitiTransactionAop
 * @Description: 工作流与基础数据库的切面
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/14 17:45
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Aspect
@Component
public class ActivitiTransactionAop {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivitiTransactionAop.class);

    @Pointcut("@annotation(com.skyeye.annotation.transaction.ActivitiAndBaseTransaction)")
    public void ActivitiAndBaseTransaction() {
    }

    @Pointcut("execution(* com.skyeye.activiti.service.impl.*.*(..))")
    public void excudeController() {
    }

    @Around(value = "ActivitiAndBaseTransaction()&&excudeController()&&@annotation(annotation)")
    public Object twiceAsOld(ProceedingJoinPoint thisJoinPoint, ActivitiAndBaseTransaction annotation) throws Throwable {
        Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack = new Stack<>();
        Stack<TransactionStatus> transactionStatuStack = new Stack<>();

        try {
            if (!openTransaction(dataSourceTransactionManagerStack, transactionStatuStack, annotation)) {
                return null;
            }
            Object ret = thisJoinPoint.proceed();
            commit(dataSourceTransactionManagerStack, transactionStatuStack);
            return ret;
        } catch (Throwable e) {
            rollback(dataSourceTransactionManagerStack, transactionStatuStack);
            LOGGER.warn(String.format("MultiTransactionalAspect, method: {}-{} occors error:{}",
                    thisJoinPoint.getTarget().getClass().getSimpleName(), thisJoinPoint.getSignature().getName()), e);
            throw e;
        }
    }

    /**
     * 开启事务处理方法
     *
     * @param dataSourceTransactionManagerStack
     * @param transactionStatuStack
     * @param multiTransactional
     * @return
     */
    private boolean openTransaction(Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack,
                                    Stack<TransactionStatus> transactionStatuStack, ActivitiAndBaseTransaction multiTransactional) {
        String[] transactionMangerNames = multiTransactional.value();
        if (ArrayUtils.isEmpty(multiTransactional.value())) {
            return false;
        }
        for (String beanName : transactionMangerNames) {
            DataSourceTransactionManager dataSourceTransactionManager =(DataSourceTransactionManager) SpringUtils.getBean(beanName);
            TransactionStatus transactionStatus = dataSourceTransactionManager
                    .getTransaction(new DefaultTransactionDefinition());
            transactionStatuStack.push(transactionStatus);
            dataSourceTransactionManagerStack.push(dataSourceTransactionManager);
        }
        return true;
    }

    /**
     * 提交处理方法
     *
     * @param dataSourceTransactionManagerStack
     * @param transactionStatuStack
     */
    private void commit(Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack,
                        Stack<TransactionStatus> transactionStatuStack) {
        while (!dataSourceTransactionManagerStack.isEmpty()) {
            dataSourceTransactionManagerStack.pop().commit(transactionStatuStack.pop());
        }
    }

    /**
     * 回滚处理方法
     * @param dataSourceTransactionManagerStack
     * @param transactionStatuStack
     */
    private void rollback(Stack<DataSourceTransactionManager> dataSourceTransactionManagerStack,
                          Stack<TransactionStatus> transactionStatuStack) {
        while (!dataSourceTransactionManagerStack.isEmpty()) {
            dataSourceTransactionManagerStack.pop().rollback(transactionStatuStack.pop());
        }
    }

}
