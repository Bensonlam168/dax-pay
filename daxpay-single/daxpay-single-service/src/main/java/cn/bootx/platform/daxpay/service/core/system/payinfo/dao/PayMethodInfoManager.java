package cn.bootx.platform.daxpay.service.core.system.payinfo.dao;

import cn.bootx.platform.common.mybatisplus.impl.BaseManager;
import cn.bootx.platform.daxpay.service.core.system.payinfo.entity.PayMethodInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 支付方式
 * @author xxm
 * @since 2024/1/8
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PayMethodInfoManager extends BaseManager<PayMethodInfoMapper, PayMethodInfo> {
}
