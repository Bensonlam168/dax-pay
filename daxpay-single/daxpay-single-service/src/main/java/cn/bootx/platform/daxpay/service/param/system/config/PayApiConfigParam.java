package cn.bootx.platform.daxpay.service.param.system.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 支付开放接口管理
 * @author xxm
 * @since 2023/12/22
 */
@Data
@Accessors(chain = true)
@Schema(title = "支付开放接口管理")
public class PayApiConfigParam {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "是否启用")
    private boolean enable;

    @Schema(description = "是否开启回调通知")
    private boolean notice;

    @Schema(description = "默认回调地址")
    private String noticeUrl;

    @Schema(description = "请求参数是否签名")
    private boolean reqSign;

    @Schema(description = "备注")
    private String remark;
}
