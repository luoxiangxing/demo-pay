package com.github.xuchengen.web.alipay;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.github.xuchengen.web.BaseCtl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 支付宝-手机网站支付
 * 作者：徐承恩
 * 邮箱：xuchengen@gmail.com
 * 日期：2019/9/18
 */
@Controller
@RequestMapping(value = "/alipay/wapPay")
public class AliPayWapPayCtl extends BaseCtl {

    private static final Log log = LogFactory.get(AliPayWapPayCtl.class);

    @GetMapping(value = {"", "/"})
    public String index(ModelMap modelMap) {
        modelMap.put("returnUrl", getHostUrl() + "/alipay/wapPay");
        modelMap.put("notifyUrl", getHostUrl() + "/alipay/doNotify");
        modelMap.put("mobileUrl", getHostUrl() + "/alipay/wapPay/mobile");

        return "/alipay/wapPay.html";
    }

    @GetMapping(value = "/mobile")
    public String mobile(ModelMap modelMap) {
        modelMap.put("returnUrl", getHostUrl() + "/alipay/wapPay/mobile");
        modelMap.put("notifyUrl", getHostUrl() + "/alipay/doNotify");
        return "/alipay/wapPayMobile.html";
    }

    @PostMapping(value = "/doPay")
    @ResponseBody
    public String doPay(String orderNo,
                        String subject,
                        String totalAmount,
                        String notifyUrl,
                        String returnUrl) {
        try {
            AlipayClient client = getAlipayClient();

            AlipayTradeWapPayRequest alipayTradeWapPayRequest = new AlipayTradeWapPayRequest();
            alipayTradeWapPayRequest.setNotifyUrl(notifyUrl);
            alipayTradeWapPayRequest.setReturnUrl(returnUrl);

            AlipayTradeWapPayModel alipayTradeWapPayModel = new AlipayTradeWapPayModel();
            alipayTradeWapPayModel.setOutTradeNo(orderNo);
            alipayTradeWapPayModel.setSubject(subject);
            alipayTradeWapPayModel.setTotalAmount(totalAmount);
            alipayTradeWapPayModel.setProductCode("QUICK_WAP_WAY");

            alipayTradeWapPayRequest.setBizModel(alipayTradeWapPayModel);

            AlipayTradeWapPayResponse alipayTradeWapPayResponse = client.pageExecute(alipayTradeWapPayRequest);
            log.info("支付宝手机网站支付响应参数：{}", JSONUtil.toJsonStr(alipayTradeWapPayResponse));
            if (alipayTradeWapPayResponse.isSuccess()) {
                return alipayTradeWapPayResponse.getBody();
            } else {
                throw new RuntimeException("调用支付宝手机网站支付失败");
            }
        } catch (Exception e) {
            log.error("调用支付宝手机网站支付接口异常：{}", JSONUtil.toJsonStr(e));
            throw new RuntimeException("支付宝手机网站支付异常", e);
        }
    }

}
