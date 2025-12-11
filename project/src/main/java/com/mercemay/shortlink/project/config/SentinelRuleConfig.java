package com.mercemay.shortlink.project.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SentinelRuleConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        List<FlowRule> flowRules = new ArrayList<>();
        FlowRule createOrderRule = new FlowRule();
        createOrderRule.setResource("create_short_link");
        createOrderRule.setGrade(RuleConstant.FLOW_GRADE_QPS); // QPS限流
        createOrderRule.setCount(1); // 每秒允许的请求数
        flowRules.add(createOrderRule);
        FlowRuleManager.loadRules(flowRules);
    }
}
