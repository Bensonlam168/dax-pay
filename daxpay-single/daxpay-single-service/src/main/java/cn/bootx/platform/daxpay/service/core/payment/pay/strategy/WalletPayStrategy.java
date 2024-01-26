package cn.bootx.platform.daxpay.service.core.payment.pay.strategy;

import cn.bootx.platform.daxpay.code.PayChannelEnum;
import cn.bootx.platform.daxpay.service.code.WalletCode;
import cn.bootx.platform.daxpay.service.core.channel.wallet.entity.Wallet;
import cn.bootx.platform.daxpay.service.core.channel.wallet.service.WalletPayService;
import cn.bootx.platform.daxpay.service.core.channel.wallet.service.WalletPayOrderService;
import cn.bootx.platform.daxpay.service.core.channel.wallet.service.WalletQueryService;
import cn.bootx.platform.daxpay.exception.pay.PayFailureException;
import cn.bootx.platform.daxpay.exception.waller.WalletBannedException;
import cn.bootx.platform.daxpay.exception.waller.WalletLackOfBalanceException;
import cn.bootx.platform.daxpay.service.func.AbsPayStrategy;
import cn.bootx.platform.daxpay.service.param.channel.wallet.WalletPayParam;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 钱包支付策略
 *
 * @author xxm
 * @since 2020/12/11
 */
@Scope(SCOPE_PROTOTYPE)
@Component
@RequiredArgsConstructor
public class WalletPayStrategy extends AbsPayStrategy {

    private final WalletPayOrderService walletPayOrderService;

    private final WalletPayService walletPayService;

    private final WalletQueryService walletQueryService;

    private Wallet wallet;

    @Override
    public PayChannelEnum getChannel() {
        return PayChannelEnum.WALLET;
    }

    /**
     * 支付前处理
     */
    @Override
    public void doBeforePayHandler() {
        WalletPayParam walletPayParam = new WalletPayParam();
        try {
            // 钱包参数验证
            String extraParamsJson = this.getPayChannelParam().getChannelExtra();
            if (StrUtil.isNotBlank(extraParamsJson)) {
                walletPayParam = JSONUtil.toBean(extraParamsJson, WalletPayParam.class);
            }
        } catch (JSONException e) {
            throw new PayFailureException("支付参数错误");
        }
        // 获取钱包
        this.wallet = walletQueryService.getWallet(walletPayParam,getPayParam());
        if (Objects.isNull(this.wallet)){
            throw new PayFailureException("钱包不存在");
        }
        // 是否被禁用
        if (Objects.equals(WalletCode.STATUS_FORBIDDEN, this.wallet.getStatus())) {
            throw new WalletBannedException();
        }
        // 判断余额
        if (this.wallet.getBalance() < getPayChannelParam().getAmount()) {
            throw new WalletLackOfBalanceException();
        }
    }

    /**
     * 支付操作
     */
    @Override
    public void doPayHandler() {
        // 异步支付方式时使用冻结方式
        if (this.getOrder().isAsyncPay()){
            walletPayService.freezeBalance(getPayChannelParam().getAmount(), this.getOrder(), this.wallet);
        } else {
            walletPayService.pay(getPayChannelParam().getAmount(), this.getOrder(), this.wallet);
        }
        walletPayOrderService.savePayment(this.getOrder(), this.getPayParam(), this.getPayChannelParam(), this.wallet);
    }

    /**
     * 成功
     */
    @Override
    public void doSuccessHandler() {
        if (this.getOrder().isAsyncPay()){
            walletPayService.paySuccess(this.getOrder().getId());
        }
        walletPayOrderService.updateSuccess(this.getOrder().getId());
    }

}
