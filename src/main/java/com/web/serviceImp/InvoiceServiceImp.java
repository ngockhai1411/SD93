package com.web.serviceImp;

import com.web.config.Environment;
import com.web.dto.request.InvoiceRequest;
import com.web.dto.request.InvoiceRequestCounter;
import com.web.dto.request.ProductSizeRequest;
import com.web.dto.request.PushNotificationRequest;
import com.web.dto.response.InvoiceResponse;
import com.web.entity.*;
import com.web.enums.PayType;
import com.web.exception.MessageException;
import com.web.firebase.PushNotificationService;
import com.web.mapper.InvoiceMapper;
import com.web.models.QueryStatusTransactionResponse;
import com.web.processor.QueryTransactionStatus;
import com.web.repository.*;
import com.web.servive.InvoiceService;
import com.web.servive.VoucherService;
import com.web.utils.CommonPage;
import com.web.utils.Contains;
import com.web.utils.StatusUtils;
import com.web.utils.UserUtils;
import com.web.vnpay.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Component
public class InvoiceServiceImp implements InvoiceService {

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private HistoryPayRepository historyPayRepository;

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private InvoiceStatusRepository invoiceStatusRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private CommonPage commonPage;

    @Autowired
    private PushNotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VNPayService vnPayService;

    @Override
    public InvoiceResponse create(InvoiceRequest invoiceRequest) {
        if(invoiceRequest.getPayType().equals(PayType.PAYMENT_MOMO)){
            if(invoiceRequest.getRequestIdMomo() == null || invoiceRequest.getOrderIdMomo() == null){
                throw new MessageException("orderid and requestid require");
            }
            if(historyPayRepository.findByOrderIdAndRequestId(invoiceRequest.getOrderIdMomo(), invoiceRequest.getRequestIdMomo()).isPresent()){
                throw new MessageException("Đơn hàng đã được thanh toán");
            }
            Environment environment = Environment.selectEnv("dev");
            try {
                QueryStatusTransactionResponse queryStatusTransactionResponse = QueryTransactionStatus.process(environment, invoiceRequest.getOrderIdMomo(), invoiceRequest.getRequestIdMomo());
                System.out.println("qqqq-----------------------------------------------------------"+queryStatusTransactionResponse.getMessage());
                if(queryStatusTransactionResponse.getResultCode() != 0){
                    throw new MessageException("Đơn hàng chưa được thanh toán");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new MessageException("Đơn hàng chưa được thanh toán");
            }
        }

        if(invoiceRequest.getPayType().equals(PayType.PAYMENT_VNPAY)){
            if(invoiceRequest.getVnpOrderInfo() == null){
                throw new MessageException("vnpay order infor require");
            }
            if(historyPayRepository.findByOrderIdAndRequestId(invoiceRequest.getVnpOrderInfo(), invoiceRequest.getVnpOrderInfo()).isPresent()){
                throw new MessageException("Đơn hàng đã được thanh toán");
            }
            int paymentStatus = vnPayService.orderReturnByUrl(invoiceRequest.getUrlVnpay());
            if(paymentStatus != 1){
                throw new MessageException("Thanh toán thất bại");
            }
        }
        if(invoiceRequest.getPayType().equals(PayType.PAYMENT_GPAY)){
            if(historyPayRepository.findByOrderIdAndRequestId(invoiceRequest.getMerchantOrderId(), invoiceRequest.getMerchantOrderId()).isPresent()){
                throw new MessageException("Đơn hàng đã được thanh toán");
            }
//            int paymentStatus = vnPayService.orderReturnByUrl(invoiceRequest.getUrlVnpay());
            if(!invoiceRequest.getStatusGpay().equals("ORDER_SUCCESS")){
                throw new MessageException("Thanh toán thất bại");
            }
        }

        if(invoiceRequest.getUserAddressId() == null){
            throw new MessageException("user address id require");
        }
        Optional<UserAddress> userAddress = userAddressRepository.findById(invoiceRequest.getUserAddressId());
        if(userAddress.isEmpty()){
            throw new MessageException("user address not found");
        }
        if(userAddress.get().getUser().getId() != userUtils.getUserWithAuthority().getId()){
            throw new MessageException("access deneid");
        }
        if(invoiceRequest.getListProductSize() == null){
            throw new MessageException("product size require");
        }
        if(invoiceRequest.getListProductSize().size() < 1){
            throw new MessageException("lenght of product size must > 0");
        }
        Double totalAmount = 0D;
        for(ProductSizeRequest p : invoiceRequest.getListProductSize()){
            if(p.getIdProductSize() == null){
                throw new MessageException("id product size require");
            }
            Optional<ProductSize> productSize = productSizeRepository.findById(p.getIdProductSize());
            if(productSize.isEmpty()){
                throw new MessageException("product size: "+p.getIdProductSize()+" not found");
            }
            if(productSize.get().getQuantity() < p.getQuantity()){
                throw new MessageException("Sản phẩm "+productSize.get().getProductColor().getProduct().getName()+" màu "+productSize.get().getProductColor().getColorName()
                        +" size "+productSize.get().getSizeName()
                        +" chỉ còn "+productSize.get().getQuantity()+" sản phẩm");
            }
            totalAmount += productSize.get().getProductColor().getProduct().getPrice() * p.getQuantity();
        }


        Invoice invoice = new Invoice();
        invoice.setCreatedDate(new Date(System.currentTimeMillis()));
        invoice.setCreatedTime(new Time(System.currentTimeMillis()));
        invoice.setUserAddress(userAddress.get());
        invoice.setNote(invoiceRequest.getNote());
        invoice.setPhone(userAddress.get().getPhone());
        invoice.setAddress(userAddress.get().getStreetName()+", "+userAddress.get().getWards().getName()+", "+userAddress.get().getWards().getDistricts().getName()+". "+userAddress.get().getWards().getDistricts().getProvince().getName());
        invoice.setReceiverName(userAddress.get().getFullname());
        invoice.setPayType(invoiceRequest.getPayType());
        invoice.setStatus(statusRepository.findById(StatusUtils.DANG_CHO_XAC_NHAN).get());
        if(invoiceRequest.getVoucherCode() != null){
            if(!invoiceRequest.getVoucherCode().equals("null") && !invoiceRequest.getVoucherCode().equals("")){
                System.out.println("voucher use === "+invoiceRequest.getVoucherCode());
                Optional<Voucher> voucher = voucherService.findByCode(invoiceRequest.getVoucherCode(), totalAmount);
                if(voucher.isPresent()){
                    totalAmount = totalAmount - voucher.get().getDiscount();
                    invoice.setVoucher(voucher.get());
                }
            }
        }
        invoice.setTotalAmount(totalAmount + 20000D);
        Invoice result = invoiceRepository.save(invoice);

        for(ProductSizeRequest p : invoiceRequest.getListProductSize()){
            ProductSize productSize = productSizeRepository.findById(p.getIdProductSize()).get();
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            invoiceDetail.setInvoice(result);
            invoiceDetail.setPrice(productSize.getProductColor().getProduct().getPrice());
            invoiceDetail.setQuantity(p.getQuantity());
            invoiceDetail.setProductSize(productSize);
            invoiceDetailRepository.save(invoiceDetail);
            try {
                productSize.getProductColor().getProduct().setQuantitySold(productSize.getProductColor().getProduct().getQuantitySold() + p.getQuantity());
                productSize.setQuantity(productSize.getQuantity() - p.getQuantity());
                productRepository.save(productSize.getProductColor().getProduct());
            }catch (Exception e){}
        }

        if(invoiceRequest.getPayType().equals(PayType.PAYMENT_MOMO) || invoiceRequest.getPayType().equals(PayType.PAYMENT_VNPAY)){
            HistoryPay historyPay = new HistoryPay();
            historyPay.setInvoice(result);
            if (invoiceRequest.getPayType().equals(PayType.PAYMENT_MOMO)){
                historyPay.setRequestId(invoiceRequest.getRequestIdMomo());
                historyPay.setOrderId(invoiceRequest.getOrderIdMomo());
            }
            if (invoiceRequest.getPayType().equals(PayType.PAYMENT_VNPAY)){
                historyPay.setRequestId(invoiceRequest.getVnpOrderInfo());
                historyPay.setOrderId(invoiceRequest.getVnpOrderInfo());
            }
            if (invoiceRequest.getPayType().equals(PayType.PAYMENT_GPAY)){
                historyPay.setRequestId(invoiceRequest.getMerchantOrderId());
                historyPay.setOrderId(invoiceRequest.getMerchantOrderId());
            }
            historyPay.setCreatedTime(new Time(System.currentTimeMillis()));
            historyPay.setCreatedDate(new Date(System.currentTimeMillis()));
            historyPay.setTotalAmount(totalAmount);
            historyPayRepository.save(historyPay);
        }

        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice(result);
        invoiceStatus.setCreatedDate(new Date(System.currentTimeMillis()));
        invoiceStatus.setCreatedTime(new Time(System.currentTimeMillis()));
        invoiceStatus.setStatus(statusRepository.findById(StatusUtils.DANG_CHO_XAC_NHAN).get());
        invoiceStatusRepository.save(invoiceStatus);

        PushNotificationRequest request = new PushNotificationRequest("Vừa có đơn đặt hàng mới: ","Đơn hàng mới"+result.getId(),"newinvoice",null);
        List<User> users = userRepository.getUserByRole(Contains.ROLE_ADMIN);
        for(User u : users){
            if(u.getTokenFcm() != null){
                if(!u.getTokenFcm().equals("")){
                    request.setToken(u.getTokenFcm());
                    notificationService.sendPushNotificationToToken(request);
                }
            }
        }
        return invoiceMapper.invoiceToInvoiceResponse(result);
    }

    @Override
    public InvoiceResponse updateStatus(Long invoiceId, Long statusId) {
        Optional<Status> status = statusRepository.findById(statusId);
        if(status.isEmpty()){
            throw new MessageException("status id not found");
        }
        Long idSt = status.get().getId();
        if(idSt == StatusUtils.DANG_CHO_XAC_NHAN){
            throw new MessageException("không thể cập nhật trạng thái này");
        }
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        if(invoice.isEmpty()){
            throw new MessageException("Không tìm thấy id hóa đơn");
        }
        if(invoiceStatusRepository.findByInvoiceAndStatus(invoiceId, statusId).isPresent()){
            throw new MessageException("Trạng thái đơn hàng này đã được cập nhật");
        }
        if(invoice.get().getStatus().getId() == StatusUtils.DA_NHAN){
            throw new MessageException("Đơn hàng đã nhận, không thể cập nhật trạng thái");
        }
        if(invoice.get().getStatus().getId() == StatusUtils.DA_HUY){
            throw new MessageException("Đơn hàng đã hủy, không thể cập nhật trạng thái");
        }
        if(invoice.get().getStatus().getId() == StatusUtils.KHONG_NHAN_HANG){
            throw new MessageException("Đơn hàng không được nhận, không thể cập nhật trạng thái");
        }

        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice(invoice.get());
        invoiceStatus.setCreatedDate(new Date(System.currentTimeMillis()));
        invoiceStatus.setCreatedTime(new Time(System.currentTimeMillis()));
        Status sta = statusRepository.findById(statusId).get();
        invoiceStatus.setStatus(statusRepository.findById(statusId).get());
        invoiceStatusRepository.save(invoiceStatus);
        invoice.get().setStatus(status.get());
        invoiceRepository.save(invoice.get());

        // Kiểm tra nếu trạng thái là "Đã hủy" hoặc "Không nhận hàng"
        if(sta.getId() == StatusUtils.KHONG_NHAN_HANG || sta.getId() == StatusUtils.DA_HUY){
            List<InvoiceDetail> list = invoiceDetailRepository.findByInvoiceId(invoiceId);
            for(InvoiceDetail i : list){
                // Lấy product từ product size
                Product product = i.getProductSize().getProductColor().getProduct();

                // Cộng lại số lượng vào ProductSize
                i.getProductSize().setQuantity(i.getQuantity() + i.getProductSize().getQuantity());
                productSizeRepository.save(i.getProductSize());

                //Trừ số lượng đã bán từ Product
                if(product.getQuantitySold() != null && product.getQuantitySold() >= i.getQuantity()){
                    product.setQuantitySold(product.getQuantitySold() - i.getQuantity());
                    productRepository.save(product);
                }

            }
        }
        return invoiceMapper.invoiceToInvoiceResponse(invoice.get());
    }

    @Override
    public List<InvoiceResponse> findByUser() {
        User user = userUtils.getUserWithAuthority();
        List<Invoice> invoices = invoiceRepository.findByUser(user.getId());
        List<InvoiceResponse> list = invoiceMapper.invoiceListToInvoiceResponseList(invoices);
        return list;
    }

    @Override
    public Page<InvoiceResponse> findAll(Date from, Date to, Pageable pageable) {
        if(from == null || to == null){
            from = Date.valueOf("2000-01-01");
            to = Date.valueOf("2200-01-01");
        }
        Page<Invoice> page = invoiceRepository.findByDate(from, to,pageable);
        List<InvoiceResponse> list = invoiceMapper.invoiceListToInvoiceResponseList(page.getContent());
        Page<InvoiceResponse> result = commonPage.restPage(page,list);
        return result;
    }

    @Override
    public InvoiceResponse cancelInvoice(Long invoiceId) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        if(invoice.isEmpty()){
            throw new MessageException("invoice id not found");
        }
        if(invoice.get().getUserAddress().getUser().getId() != userUtils.getUserWithAuthority().getId()){
            throw new MessageException("access denied");
        }
        if(!invoice.get().getPayType().equals(PayType.PAYMENT_DELIVERY)){
            throw new MessageException("Đơn hàng đã được thanh toán, không thể hủy");
        }
        Long idSt = invoice.get().getStatus().getId();
        if(idSt == StatusUtils.DA_GUI || idSt == StatusUtils.DA_NHAN || idSt == StatusUtils.DA_HUY || idSt == StatusUtils.KHONG_NHAN_HANG){
            throw new MessageException(invoice.get().getStatus().getName()+ " không thể hủy hàng");
        }
        invoice.get().setStatus(statusRepository.findById(StatusUtils.DA_HUY).get());
        Invoice result = invoiceRepository.save(invoice.get());
        List<InvoiceDetail> list  = invoiceDetailRepository.findByInvoiceId(invoiceId);
        for(InvoiceDetail i : list){
            // Lấy product từ product size
            Product product = i.getProductSize().getProductColor().getProduct();

            i.getProductSize().setQuantity(i.getQuantity() + i.getProductSize().getQuantity());
            productSizeRepository.save(i.getProductSize());

            //Trừ số lượng đã bán từ Product
            if(product.getQuantitySold() != null && product.getQuantitySold() >= i.getQuantity()){
                product.setQuantitySold(product.getQuantitySold() - i.getQuantity());
                productRepository.save(product);
            }
        }
        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice(invoice.get());
        invoiceStatus.setCreatedDate(new Date(System.currentTimeMillis()));
        invoiceStatus.setCreatedTime(new Time(System.currentTimeMillis()));
        invoiceStatus.setStatus(statusRepository.findById(StatusUtils.DA_HUY).get());
        invoiceStatusRepository.save(invoiceStatus);
        return invoiceMapper.invoiceToInvoiceResponse(result);
    }

    @Override
    public InvoiceResponse findById(Long invoiceId) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        if(invoice.isEmpty()){
            throw new MessageException("invoice id not found");
        }
        if(invoice.get().getUserAddress().getUser().getId() != userUtils.getUserWithAuthority().getId()){
            throw new MessageException("access denied");
        }
        return invoiceMapper.invoiceToInvoiceResponse(invoice.get());
    }

    @Override
    public InvoiceResponse findByIdForAdmin(Long invoiceId) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        if(invoice.isEmpty()){
            throw new MessageException("invoice id not found");
        }
        return invoiceMapper.invoiceToInvoiceResponse(invoice.get());
    }

    @Override
    public Page<InvoiceResponse> findAllFull(Date from, Date to, PayType payType, Long statusId, Pageable pageable) {
        if(from == null || to == null){
            from = Date.valueOf("2000-01-01");
            to = Date.valueOf("2200-01-01");
        }
        Page<Invoice> page = null;
        if(payType == null && statusId == null){
            page = invoiceRepository.findByDate(from, to,pageable);
        }
        if(payType == null && statusId != null){
            page = invoiceRepository.findByDateAndStatus(from, to, statusId,pageable);
        }
        if(payType != null && statusId == null){
            page = invoiceRepository.findByDateAndPaytype(from, to,payType,pageable);
        }
        if(payType != null && statusId != null){
            page = invoiceRepository.findByDateAndPaytypeAndStatus(from, to,payType,statusId,pageable);
        }

        List<InvoiceResponse> list = invoiceMapper.invoiceListToInvoiceResponseList(page.getContent());
        Page<InvoiceResponse> result = commonPage.restPage(page,list);
        return result;
    }

    @Override
    public Long payCounter(InvoiceRequestCounter invoiceRequestCounter) {
        if(invoiceRequestCounter.getListProductSize().isEmpty()){
            throw new MessageException("Hãy chọn 1 sản phẩm");
        }
        Double totalAmount = 0D;
        for(ProductSizeRequest p : invoiceRequestCounter.getListProductSize()){
            Optional<ProductSize> productSize = productSizeRepository.findById(p.getIdProductSize());
            if(productSize.get().getQuantity() < p.getQuantity()){
                throw new MessageException("Tạo đơn hàng thất bại size: "+productSize.get().getSizeName()+" màu "+productSize.get().getProductColor().getColorName()+" "+productSize.get().getProductColor().getProduct().getName()+" chỉ còn "+productSize.get().getQuantity()+ " sản phẩm");
            }
            totalAmount += productSize.get().getProductColor().getProduct().getPrice() * p.getQuantity();
        }

        Voucher voucher = null;
        if(invoiceRequestCounter.getVoucherId() != null){
            if(invoiceRequestCounter.getVoucherId() != -1){
                voucher = voucherRepository.findById(invoiceRequestCounter.getVoucherId()).get();
                if(voucher.getMinAmount() > totalAmount){
                    throw new MessageException("Đơn hàng phải đạt tối thiểu "+voucher.getMinAmount()+" để áp dụng voucher này");
                }
                else{
                    totalAmount = totalAmount - voucher.getDiscount();
                }
            }
        }

        Invoice invoice = new Invoice();
        invoice.setCreatedDate(new Date(System.currentTimeMillis()));
        invoice.setCreatedTime(new Time(System.currentTimeMillis()));
        invoice.setPhone(invoiceRequestCounter.getPhone());
        invoice.setReceiverName(invoiceRequestCounter.getFullName());
        invoice.setPayType(PayType.PAY_COUNTER);
        invoice.setStatus(statusRepository.findById(StatusUtils.DA_NHAN).get());
        invoice.setTotalAmount(totalAmount);
        invoice.setVoucher(voucher);
        Invoice result = invoiceRepository.save(invoice);

        for(ProductSizeRequest p : invoiceRequestCounter.getListProductSize()){
            ProductSize productSize = productSizeRepository.findById(p.getIdProductSize()).get();
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            invoiceDetail.setInvoice(result);
            invoiceDetail.setPrice(productSize.getProductColor().getProduct().getPrice());
            invoiceDetail.setQuantity(p.getQuantity());
            invoiceDetail.setProductSize(productSize);
            invoiceDetailRepository.save(invoiceDetail);
            productSize.setQuantity(productSize.getQuantity() - p.getQuantity());
            productSizeRepository.save(productSize);
            try {
                if(productSize.getProductColor().getProduct().getQuantitySold() == null){
                    productSize.getProductColor().getProduct().setQuantitySold(0);
                }
                productSize.getProductColor().getProduct().setQuantitySold(productSize.getProductColor().getProduct().getQuantitySold() + p.getQuantity());
//                productSize.setQuantity(productSize.getQuantity() - p.getQuantity());
                productRepository.save(productSize.getProductColor().getProduct());
            }catch (Exception e){}
        }

        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice(result);
        invoiceStatus.setCreatedDate(new Date(System.currentTimeMillis()));
        invoiceStatus.setCreatedTime(new Time(System.currentTimeMillis()));
        invoiceStatus.setStatus(statusRepository.findById(StatusUtils.DA_NHAN).get());
        invoiceStatusRepository.save(invoiceStatus);
        return invoice.getId();
    }
}
