package cn.bootx.platform.daxpay.service.core.payment.close.strategy;

import cn.bootx.platform.daxpay.code.PayChannelEnum;
import cn.bootx.platform.daxpay.service.core.channel.voucher.service.VoucherPayOrderService;
import cn.bootx.platform.daxpay.service.core.channel.voucher.service.VoucherPayService;
import cn.bootx.platform.daxpay.service.func.AbsPayCloseStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 储值卡
 * @author xxm
 * @since 2023/12/30
 */
@Slf4j
@Scope(SCOPE_PROTOTYPE)
@Service
@RequiredArgsConstructor
public class VoucherPayCloseStrategy extends AbsPayCloseStrategy {

    private final VoucherPayService voucherPayService;
    private final VoucherPayOrderService voucherPayOrderService;

    @Override
    public PayChannelEnum getChannel() {
        return PayChannelEnum.VOUCHER;
    }

    /**
     * 关闭操作
     */
    @Override
    public void doCloseHandler() {
        voucherPayService.close(this.getOrder().getId());
        voucherPayOrderService.updateClose(this.getOrder().getId());

    }
}
