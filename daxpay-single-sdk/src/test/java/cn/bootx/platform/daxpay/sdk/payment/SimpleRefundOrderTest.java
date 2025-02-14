package cn.bootx.platform.daxpay.sdk.payment;

import cn.bootx.platform.daxpay.sdk.code.SignTypeEnum;
import cn.bootx.platform.daxpay.sdk.model.refund.RefundModel;
import cn.bootx.platform.daxpay.sdk.net.DaxPayConfig;
import cn.bootx.platform.daxpay.sdk.net.DaxPayKit;
import cn.bootx.platform.daxpay.sdk.param.refund.RefundParam;
import cn.bootx.platform.daxpay.sdk.response.DaxPayResult;
import cn.hutool.core.util.RandomUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * 简单退款演示
 * @author xxm
 * @since 2024/2/26
 */
public class SimpleRefundOrderTest {

    /**
     * 初始化
     */
    @Before
    public void init() {
        // 初始化支付配置
        DaxPayConfig config = DaxPayConfig.builder()
                .serviceUrl("http://127.0.0.1:9000")
                .signSecret("123456")
                .signType(SignTypeEnum.HMAC_SHA256)
                .build();
        DaxPayKit.initConfig(config);
    }

    /**
     * 退款
     */
    @Test
    public void refund(){
        RefundParam param = new RefundParam();
        param.setClientIp("127.0.0.1");

        param.setBizRefundNo("R" + RandomUtil.randomNumbers(5));
        // 设置具体的退款参数
        param.setAmount(19);

        DaxPayResult<RefundModel> execute = DaxPayKit.execute(param);
        System.out.println(execute);
        System.out.println(execute.getData());
    }
}
