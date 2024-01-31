package cn.bootx.platform.daxpay.service.core.payment.callback.service;

import cn.bootx.platform.daxpay.code.PayRefundStatusEnum;
import cn.bootx.platform.daxpay.service.code.PayCallbackStatusEnum;
import cn.bootx.platform.daxpay.service.code.RefundRepairWayEnum;
import cn.bootx.platform.daxpay.service.common.context.CallbackLocal;
import cn.bootx.platform.daxpay.service.common.local.PaymentContextLocal;
import cn.bootx.platform.daxpay.service.core.order.refund.dao.PayRefundOrderManager;
import cn.bootx.platform.daxpay.service.core.order.refund.entity.PayRefundOrder;
import cn.bootx.platform.daxpay.service.core.payment.repair.result.RefundRepairResult;
import cn.bootx.platform.daxpay.service.core.payment.repair.service.RefundRepairService;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 支付退款回调
 * @author xxm
 * @since 2024/1/24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayRefundCallbackService {
    private final PayRefundOrderManager refundOrderManager;

    private final RefundRepairService reflectionService;

    private final LockTemplate lockTemplate;

    /**
     * 退款回调统一处理
     */
    public void refundCallback() {

        CallbackLocal callbackInfo = PaymentContextLocal.get().getCallbackInfo();
        // 加锁
        LockInfo lock = lockTemplate.lock("callback:refund:" + callbackInfo.getOrderId());
        if (Objects.isNull(lock)){
            callbackInfo.setCallbackStatus(PayCallbackStatusEnum.IGNORE).setMsg("回调正在处理中，忽略本次回调请求");
            log.warn("订单号: {} 回调正在处理中，忽略本次回调请求", callbackInfo.getOrderId());
            return;
        }
        try {
            // 获取退款单
            PayRefundOrder refundOrder = refundOrderManager.findById(callbackInfo.getOrderId()).orElse(null);
            // 退款单不存在,记录回调记录
            if (Objects.isNull(refundOrder)) {
                callbackInfo.setCallbackStatus(PayCallbackStatusEnum.NOT_FOUND).setMsg("退款单不存在,记录回调记录");
                return;
            }
            // 退款单已经被处理, 记录回调记录
            if (!Objects.equals(PayRefundStatusEnum.PROGRESS.getCode(), refundOrder.getStatus())) {
                callbackInfo.setCallbackStatus(PayCallbackStatusEnum.IGNORE).setMsg("退款单状态已处理,记录回调记录");
            }

            // 退款成功还是失败
            if (Objects.equals(PayRefundStatusEnum.SUCCESS.getCode(), callbackInfo.getGatewayStatus())) {
                RefundRepairResult repair = reflectionService.repair(refundOrder, RefundRepairWayEnum.SUCCESS);
                callbackInfo.setPayRepairNo(repair.getRepairNo());
            }  else {
                RefundRepairResult repair = reflectionService.repair(refundOrder, RefundRepairWayEnum.FAIL);
                callbackInfo.setPayRepairNo(repair.getRepairNo());
            }

        } finally {
            lockTemplate.releaseLock(lock);
        }
    }

}
