package com.swpproject.koi_care_system.service.vnpay;

import com.swpproject.koi_care_system.config.VnPayConfig;
import com.swpproject.koi_care_system.dto.VnPayDto;
import com.swpproject.koi_care_system.models.Order;
import com.swpproject.koi_care_system.repository.OrderRepository;
import com.swpproject.koi_care_system.service.order.IOrderService;
import com.swpproject.koi_care_system.ultis.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VnPayService {
    private final VnPayConfig vnPayConfig;
    private final IOrderService orderService;
    private final OrderRepository orderRepository;
    @Value("${payment.vnPay.secretKey}")
    private String secretKey;
    @Value("${payment.vnPay.url}")
    private String vnp_PayUrl;
    public VnPayDto createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        Long userId = Long.parseLong(request.getParameter("userId"));
        //Long orderId = orderService.getUserOrders(userId).getLast().getId();
        Long orderId = 1L;
        vnpParamsMap.put("vnp_OrderInfo","Thanh toan cho don hang # "+ orderId);
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(secretKey, hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;
        return VnPayDto.builder()
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
    public VnPayDto createVnPayPaymentViaOrderId(HttpServletRequest request) {
        Order order = orderRepository.findByOrderId(Long.parseLong(request.getParameter("orderId")));
        long amount = ((Order) order).getTotalAmount().longValue();
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_OrderInfo","Thanh toan cho don hang # "+ order.getOrderId());
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(secretKey, hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;
        return VnPayDto.builder()
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
}