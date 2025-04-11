// const exceptionCode = 417;
var token = localStorage.getItem("token");
async function checkroleUser() {
    var token = localStorage.getItem("token");
    var url = 'http://localhost:8080/api/user/check-role-user';
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status > 300) {
        window.location.replace('login')
    }
}
var total = 0;
var listSize = [];

function loadCartCheckOut() {
    var listcart = localStorage.getItem("product_cart");
    if (listcart == null) {
        alert("Bạn chưa có sản phẩm nào trong giỏ hàng!");
        window.location.replace("cart");
        return;
    }
    var list = JSON.parse(localStorage.getItem("product_cart"));
    if (list.length == 0) {
        alert("Bạn chưa có sản phẩm nào trong giỏ hàng!");
        window.location.replace("cart");
    }
    var main = ''
    for (i = 0; i < list.length; i++) {
        total += Number(list[i].quantiy * list[i].product.price);
        var obj = {
            "idProductSize": list[i].size.id,
            "quantity": list[i].quantiy
        }
        listSize.push(obj);
        main += `<div class="row">
                    <div class="col-lg-2 col-md-3 col-sm-3 col-3 colimgcheck">
                        <img src="${list[i].product.imageBanner}" class="procheckout">
                        <span class="slpro">${list[i].quantiy}</span>
                    </div>
                    <div class="col-lg-7 col-md-6 col-sm-6 col-6">
                        <span class="namecheck">${list[i].product.name}</span>
                        <span class="colorcheck">${list[i].color.colorName} / ${list[i].size.sizeName}</span>
                    </div>
                    <div class="col-lg-3 col-md-3 col-sm-3 col-3 pricecheck">
                        <span>${formatmoneyCheck(list[i].quantiy * list[i].product.price)}</span>
                    </div>
                </div>`
    }
    document.getElementById("listproductcheck").innerHTML = main;
    document.getElementById("totalAmount").innerHTML = formatmoneyCheck(total);
    document.getElementById("totalfi").innerHTML = formatmoneyCheck(total + 20000);
}

function formatmoneyCheck(money) {
    const VND = new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
    });
    return VND.format(money);
}

var voucherId = null;
var voucherCode = null;
var discountVou = 0;
async function loadVoucher() {
    var code = document.getElementById("codevoucher").value
    var url = 'http://localhost:8080/api/voucher/public/findByCode?code=' + code + '&amount=' + (total - Number(20000));
    const response = await fetch(url, {});
    var result = await response.json();
    if (response.status == exceptionCode) {
        var mess = result.defaultMessage
        document.getElementById("messerr").innerHTML = mess;
        document.getElementById("blockmessErr").style.display = 'block';
        document.getElementById("blockmess").style.display = 'none';
        voucherCode = null;
        voucherId = null;
        discountVou = 0;
        document.getElementById("moneyDiscount").innerHTML = formatmoneyCheck(0);
        document.getElementById("totalfi").innerHTML = formatmoneyCheck(total + 20000);
    }
    if (response.status < 300) {
        voucherId = result.id;
        voucherCode = result.code;
        discountVou = result.discount;
        document.getElementById("blockmessErr").style.display = 'none';
        document.getElementById("blockmess").style.display = 'block';
        document.getElementById("moneyDiscount").innerHTML = formatmoneyCheck(result.discount);
        document.getElementById("totalfi").innerHTML = formatmoneyCheck(total - result.discount + 20000);
    }

}

function checkout() {
    var con = confirm("Xác nhận đặt hàng!");
    if (con == false) {
        return;
    }
    var paytype = $('input[name=paytype]:checked').val()
    if (paytype == "momo") {
        requestPayMentMomo()
    }
    if (paytype == "cod") {
        paymentCod();
    }
    if (paytype == "vnpay") {
        requestPayMentVnpay();
    }
    if (paytype == "gpay") {
        requestPayMentGpay();
    }
}

async function requestPayMentMomo() {
    var ghichu = document.getElementById("ghichudonhang").value;
    window.localStorage.setItem('ghichudonhang', ghichu);
    window.localStorage.setItem('voucherCode', voucherCode);
    window.localStorage.setItem('paytype', "MOMO");
    window.localStorage.setItem('sodiachi', document.getElementById("sodiachi").value);
    var returnurl = 'http://localhost:8080/payment';

    var urlinit = 'http://localhost:8080/api/urlpayment';
    var paymentDto = {
        "content": "Thanh toán đơn hàng Easy-Shop",
        "returnUrl": returnurl,
        "notifyUrl": returnurl,
        "codeVoucher": voucherCode,
        "listProductSize": listSize
    }
    console.log(paymentDto)
    const res = await fetch(urlinit, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(paymentDto)
    });
    var result = await res.json();
    if (res.status < 300) {
        window.open(result.url, '_blank');
    }
    if (res.status == exceptionCode) {
        toastr.warning(result.defaultMessage);
    }

}


async function requestPayMentVnpay() {
    var ghichu = document.getElementById("ghichudonhang").value;
    window.localStorage.setItem('ghichudonhang', ghichu);
    window.localStorage.setItem('voucherCode', voucherCode);
    window.localStorage.setItem('paytype', "VNPAY");
    window.localStorage.setItem('sodiachi', document.getElementById("sodiachi").value);
    var returnurl = 'http://localhost:8080/payment';

    var urlinit = 'http://localhost:8080/api/vnpay/urlpayment';
    var paymentDto = {
        "content": "Thanh toán đơn hàng Easy-Shop",
        "returnUrl": returnurl,
        "notifyUrl": returnurl,
        "codeVoucher": voucherCode,
        "listProductSize": listSize
    }
    console.log(paymentDto)
    const res = await fetch(urlinit, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(paymentDto)
    });
    var result = await res.json();
    if (res.status < 300) {
        window.open(result.url, '_blank');
    }
    if (res.status == exceptionCode) {
        toastr.warning(result.defaultMessage);
    }

}

async function requestPayMentGpay() {
    console.log("gpay")
    var ghichu = document.getElementById("ghichudonhang").value;
    window.localStorage.setItem('ghichudonhang', ghichu);
    window.localStorage.setItem('voucherCode', voucherCode);
    window.localStorage.setItem('paytype', "GPAY");
    window.localStorage.setItem('sodiachi', document.getElementById("sodiachi").value);
    var returnurl = 'http://localhost:8080/payment';

    var urlinit = 'http://localhost:8080/api/gpay/urlpayment';
    var paymentDto = {
        "content": "Thanh toán đơn hàng EasyShop",
        "returnUrl": returnurl,
        "notifyUrl": returnurl,
        "codeVoucher": voucherCode,
        "listProductSize": listSize
    }
    console.log(paymentDto)
    const res = await fetch(urlinit, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(paymentDto)
    });
    var result = await res.json();
    if (res.status < 300) {
        window.open(result.url, '_blank');
    }
    if (res.status == exceptionCode) {
        toastr.warning(result.defaultMessage);
    }

}


async function paymentOnline() {
    var list = JSON.parse(localStorage.getItem("product_cart"));
    var uls = new URL(document.URL)
    var orderId = uls.searchParams.get("orderId");
    var requestId = uls.searchParams.get("requestId");
    var vnpOrderInfo = uls.searchParams.get("vnp_OrderInfo");
    var merchantOrderId = uls.searchParams.get("merchant_order_id");
    var statusGpay = uls.searchParams.get("status");
    var note = window.localStorage.getItem("ghichudonhang");
    for (i = 0; i < list.length; i++) {
        var obj = {
            "idProductSize": list[i].size.id,
            "quantity": list[i].quantiy
        }
        listSize.push(obj);
    }
    var paytype = window.localStorage.getItem("paytype")
    var type = "PAYMENT_MOMO";
    var urlVnpay = null
    if(paytype == "VNPAY"){
        type = "PAYMENT_VNPAY"
        const currentUrl = window.location.href;
        const parsedUrl = new URL(currentUrl);
        const queryStringWithoutQuestionMark = parsedUrl.search.substring(1);
        urlVnpay = queryStringWithoutQuestionMark
    }
    if(paytype == "GPAY"){
        type = "PAYMENT_GPAY"
    }
    var orderDto = {
        "payType": type,
        "userAddressId": window.localStorage.getItem("sodiachi"),
        "voucherCode": window.localStorage.getItem("voucherCode"),
        "note": note,
        "requestIdMomo": requestId,
        "orderIdMomo": orderId,
        "vnpOrderInfo": vnpOrderInfo,
        "statusGpay": statusGpay,
        "merchantOrderId": merchantOrderId,
        "urlVnpay": urlVnpay,
        "listProductSize": listSize
    }
    console.log(orderDto)
    var url = 'http://localhost:8080/api/invoice/user/create';
    var token = localStorage.getItem("token");
    const res = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(orderDto)
    });
    var result = await res.json();
    if (res.status < 300) {
        document.getElementById("thanhcong").style.display = 'block'
        window.localStorage.removeItem("product_cart")
    }
    if (res.status == exceptionCode) {
        document.getElementById("thatbai").style.display = 'block'
        document.getElementById("thanhcong").style.display = 'none'
        document.getElementById("errormess").innerHTML = result.defaultMessage
    }

}



async function paymentCod() {
    var note = document.getElementById("ghichudonhang").value;
    var orderDto = {
        "payType": "PAYMENT_DELIVERY",
        "userAddressId": document.getElementById("sodiachi").value,
        "voucherCode": voucherCode,
        "note": note,
        "listProductSize": listSize
    };
    var url = 'http://localhost:8080/api/invoice/user/create';
    var token = localStorage.getItem("token");
    const res = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(orderDto)
    });

    var result = await res.json();
    if (res.status < 300) {
        swal({
                title: "Thông báo",
                text: "Đặt hàng thành công!",
                type: "success"
            },
            function() {
                window.localStorage.removeItem("product_cart");
                window.location.replace("account#invoice");
            });
    }
    if (res.status == exceptionCode) {
        toastr.warning(result.defaultMessage);
        return;
    }
}