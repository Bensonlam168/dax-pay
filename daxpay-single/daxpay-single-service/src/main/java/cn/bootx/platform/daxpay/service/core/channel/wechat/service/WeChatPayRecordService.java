package cn.bootx.platform.daxpay.service.core.channel.wechat.service;

import cn.bootx.platform.common.core.exception.DataNotExistException;
import cn.bootx.platform.common.core.rest.PageResult;
import cn.bootx.platform.common.core.rest.param.PageParam;
import cn.bootx.platform.common.mybatisplus.util.MpUtil;
import cn.bootx.platform.daxpay.service.code.AliPayRecordTypeEnum;
import cn.bootx.platform.daxpay.service.core.channel.wechat.dao.WeChatPayRecordManager;
import cn.bootx.platform.daxpay.service.core.channel.wechat.entity.WeChatPayRecord;
import cn.bootx.platform.daxpay.service.core.order.pay.entity.PayChannelOrder;
import cn.bootx.platform.daxpay.service.core.order.pay.entity.PayOrder;
import cn.bootx.platform.daxpay.service.core.order.refund.entity.PayRefundChannelOrder;
import cn.bootx.platform.daxpay.service.core.order.refund.entity.PayRefundOrder;
import cn.bootx.platform.daxpay.service.dto.channel.wechat.WeChatPayRecordDto;
import cn.bootx.platform.daxpay.service.param.channel.wechat.WeChatPayRecordQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author xxm
 * @since 2024/2/19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeChatPayRecordService {
    private final WeChatPayRecordManager weChatPayRecordManager;

    /**
     * 支付
     */
    public void pay(PayOrder order, PayChannelOrder channelOrder){
        WeChatPayRecord weChatPayRecord = new WeChatPayRecord()
                .setType(AliPayRecordTypeEnum.PAY.getCode())
                .setTitle(order.getTitle())
                .setOrderId(order.getId())
                .setGatewayOrderNo(order.getGatewayOrderNo())
                .setAmount(channelOrder.getAmount());
        weChatPayRecordManager.save(weChatPayRecord);
    }

    /**
     * 退款
     */
    public void refund(PayRefundOrder order, PayRefundChannelOrder channelOrder){
        WeChatPayRecord weChatPayRecord = new WeChatPayRecord()
                .setType(AliPayRecordTypeEnum.PAY.getCode())
                .setTitle(order.getTitle())
                .setOrderId(order.getId())
                .setGatewayOrderNo(order.getGatewayOrderNo())
                .setAmount(channelOrder.getAmount());
        weChatPayRecordManager.save(weChatPayRecord);
    }

    /**
     * 分页
     */
    public PageResult<WeChatPayRecordDto> page(PageParam pageParam, WeChatPayRecordQuery param){
        return MpUtil.convert2DtoPageResult(weChatPayRecordManager.page(pageParam, param));
    }

    /**
     * 查询详情
     */
    public WeChatPayRecordDto findById(Long id){
        return weChatPayRecordManager.findById(id).map(WeChatPayRecord::toDto).orElseThrow(DataNotExistException::new);
    }


}
