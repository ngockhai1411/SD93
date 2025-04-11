var token = localStorage.getItem("token");
var size = 10;
var currentPage = 0; // Biến để theo dõi trang hiện tại

async function loadInvoice(page) {
    var start = document.getElementById("start").value
    var end = document.getElementById("end").value
    var type = document.getElementById("type").value
    var trangthai = document.getElementById("trangthai").value
    var sort = document.getElementById("sort").value
    var url = 'http://localhost:8080/api/invoice/admin/find-all?page=' + page + '&size=' + size + '&sort=' + sort;
    if (start != "" && end != "") {
        url += '&from=' + start + '&to=' + end;
    }
    if (type != -1) {
        url += '&paytype=' + type;
    }
    if (trangthai != -1) {
        url += '&status=' + trangthai
    }
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
        })
    });
    var result = await response.json();
    console.log(result)
    var list = result.content;
    var totalPage = result.totalPages;
    var main = '';
    for (i = 0; i < list.length; i++) {
        let paymentStatusDisplay;
        if (list[i].status.name === "Đã nhận đơn hàng") {
            paymentStatusDisplay = '<span class="dathanhtoan">Đã thanh toán</span>';
        } else {
            paymentStatusDisplay = list[i].payType != 'PAYMENT_DELIVERY' ? '<span class="dathanhtoan">Đã thanh toán</span>' : '<span class="chuathanhtoan">Thanh toán khi nhận hàng(COD)</span>';
        }
        main += `<tr>
                    <td>${list[i].id}</td>
                    <td>${list[i].createdTime}<br>${list[i].createdDate}</td>
                    <td>${list[i].address}</td>
                    <td>${formatmoney(list[i].totalAmount)}<br>
                    ${list[i].voucher == null ? '' : `Đã giảm: ${formatmoney(list[i].voucher.discount)}`}
                    </td>
                    <td>${paymentStatusDisplay}</td>
                    <td>${list[i].status.name}</td>
                    <td>${list[i].payType}</td>
                    <td class="sticky-col">
                        <i onclick="loadDetailInvoice(${list[i].id})" data-bs-toggle="modal" data-bs-target="#modaldeail" class="fa fa-eye iconaction"></i>
                        <i onclick="openStatus(${list[i].id},${list[i].status.id})" data-bs-toggle="modal" data-bs-target="#capnhatdonhang" class="fa fa-edit iconaction"></i><br>
                        <a target="_blank" href="in-don?id=${list[i].id}"><i class="fa fa-print iconaction"></i></a>
                    </td>
                </tr>`
    }
    document.getElementById("listinvoice").innerHTML = main
    var mainpage = ''
    for (i = 1; i <= totalPage; i++) {
        mainpage += `<li onclick="changePage(${(Number(i) - 1)})" class="page-item"><a class="page-link" href="#listsp">${i}</a></li>`
    }
    document.getElementById("pageable").innerHTML = mainpage

    // Hiển thị trang đã chọn
    var activePageElement = document.querySelector(`#pageable li:nth-child(${currentPage + 1})`);
    if (activePageElement) {
        activePageElement.classList.add('active');
    }
}

function changePage(newPage) {
    currentPage = newPage;
    loadInvoice(currentPage);
}

function startInvoicePollingAdmin() {
    loadInvoice(currentPage); // Load initial invoices with current page
    setInterval(() => {
        loadInvoice(currentPage); // Keep the current page
    }, 1000); // Refresh every 5 seconds (adjust as needed)
}

async function loadDetailInvoice(id) {
    var url = 'http://localhost:8080/api/invoice-detail/admin/find-by-invoice?idInvoice=' + id;
    const res = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await res.json();
    var main = ''
    for (i = 0; i < list.length; i++) {
        main += `<tr>
                    <td><img src="${list[i].product.imageBanner}" class="imgdetailacc"></td>
                    <td>
                        <a href="">${list[i].productName}</a><br>
                        <span>${list[i].colorName} / ${list[i].productSize.sizeName}</span><br>
                        <span>Mã sản phẩm: ${list[i].product.code}</span><br>
                        <span class="slmobile">SL: ${list[i].quantity}</span>
                    </td>
                    <td>${formatmoney(list[i].price)}</td>
                    <td class="sldetailacc">${list[i].quantity}</td>
                    <td class="pricedetailacc yls">${formatmoney(list[i].price * list[i].quantity)}</td>
                </tr>`
    }
    document.getElementById("listDetailinvoice").innerHTML = main

    var url = 'http://localhost:8080/api/invoice/admin/find-by-id?idInvoice=' + id;
    const resp = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var result = await resp.json();
    document.getElementById("ngaytaoinvoice").innerHTML = result.createdTime + " " + result.createdDate
    result.payType != "PAYMENT_DELIVERY" ? document.getElementById("loaithanhtoan").innerHTML = "Đã thanh toán" : "Thanh toán khi nhận hàng"
    result.payType == "PAYMENT_MOMO" ? document.getElementById("loaithanhtoan").innerHTML = "Thanh toán qua momo" : "Thanh toán khi nhận hàng (COD)"
    result.payType == "PAYMENT_VNPAY" ? document.getElementById("loaithanhtoan").innerHTML = "Thanh toán qua vnpay" : "Thanh toán khi nhận hàng (COD)"
    result.payType == "PAYMENT_GPAY" ? document.getElementById("loaithanhtoan").innerHTML = "Thanh toán qua gpay" : "Thanh toán khi nhận hàng (COD)"
    result.payType == "PAY_COUNTER" ? document.getElementById("loaithanhtoan").innerHTML = "Thanh toán tại quầy" : "Thanh toán khi nhận hàng (COD)"
    document.getElementById("ttvanchuyen").innerHTML = result.status.name
    document.getElementById("tennguoinhan").innerHTML = result.receiverName
    document.getElementById("addnhan").innerHTML = result.address
    document.getElementById("addnhan").innerHTML = result.address
    document.getElementById("phonenhan").innerHTML = result.phone
    document.getElementById("phonenhan").innerHTML = result.phone
    document.getElementById("ghichunh").innerHTML = result.note == "" || result.note == null ? 'Không có ghi chú' : result.note
}

function openStatus(idinvoice, idstatus) {
    document.getElementById("iddonhangupdate").value = idinvoice
    document.getElementById("trangthaiupdate").value = idstatus
}

async function updateStatus() {
    var idtrangthai = document.getElementById("trangthaiupdate").value
    var idinvoice = document.getElementById("iddonhangupdate").value
    var url = 'http://localhost:8080/api/invoice/admin/update-status?idInvoice=' + idinvoice + '&idStatus=' + idtrangthai;
    const res = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (res.status < 300) {
        toastr.success("Cập nhật trạng thái đơn hàng thành công!");
        $("#capnhatdonhang").modal("hide")
        loadInvoice(currentPage)
    }
    if (res.status == exceptionCode) {
        var result = await res.json()
        toastr.warning(result.defaultMessage);
    }
}

async function loadStatusUpdate() {
    var url = 'http://localhost:8080/api/status/admin/all';
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
        })
    });
    var list = await response.json();
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<option value="${list[i].id}">${list[i].name}</option>`
    }
    document.getElementById("trangthaiupdate").innerHTML = main
}

async function loadAllStatus() {
    var url = 'http://localhost:8080/api/status/admin/all';
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
        })
    });
    var list = await response.json();
    var main = '<option value="-1">--- Tất cả ---</option>';
    for (i = 0; i < list.length; i++) {
        main += `<option value="${list[i].id}">${list[i].name}</option>`
    }
    document.getElementById("trangthai").innerHTML = main
}

async function createInvoice() {
    // Kiểm tra số lượng sản phẩm
    for (var i = 0; i < listProductTam.length; i++) {
        if (listProductTam[i].quantity < 1) {
            toastr.error("Số lượng sản phẩm " + listProductTam[i].product.name + " phải lớn hơn hoặc bằng 1.");
            return; // Dừng tạo đơn hàng nếu có sản phẩm không hợp lệ
        }
    }

    var con = confirm("Xác nhận");
    if (con == false) {
        return;
    }

    var fullname = document.getElementById("fullname").value;
    var phone = document.getElementById("phone").value;

    // Validate số điện thoại
    const phoneRegex = /^[0-9]+$/;
    let phoneError = false; // Flag to track phone validation errors

    if (phone) {

        if (!phoneRegex.test(phone)) {
            toastr.warning("Số điện thoại không hợp lệ. Vui lòng nhập số.");
            phoneError = true;
        } else if (phone.length < 10 || phone.length > 11) {
            toastr.warning("Số điện thoại phải có 10 hoặc 11 số.");
            phoneError = true;
        } else if (phone[0] !== '0') {
            toastr.warning("Số điện thoại phải bắt đầu bằng số 0.");
            phoneError = true;
        }
    }

    if (phoneError) {
        return; // Stop further execution if phone is invalid
    }

    var listId = [];
    var tongtientt = 0;
    for (var i = 0; i < listProductTam.length; i++) {
        var obj = {
            "idProductSize": listProductTam[i].productSize.id,
            "quantity": listProductTam[i].quantity
        }
        listId.push(obj);
        tongtientt = Number(tongtientt) + Number(listProductTam[i].product.price) * Number(listProductTam[i].quantity);
    }
    var km = document.getElementById("khuyenmai").value;
    if (km != -1) {
        const response = await fetch('http://localhost:8080/api/voucher/admin/findById?id=' + km, {
            method: 'GET',
            headers: new Headers({
                'Authorization': 'Bearer ' + token,
            }),
        });
        var result = await response.json();
        if (result.minAmount > tongtientt) {
            toastr.error("Đơn hàng phải đạt tối thiểu " + formatmoney(result.minAmount) + " để có thể áp khuyến mại này");
            return;
        }
    }
    var payload = {
        "listProductSize": listId,
        "fullName": fullname,
        "phone": phone,
        "voucherId": km
    }
    const res = await fetch('http://localhost:8080/api/invoice/admin/pay-counter', {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(payload)
    });
    if (res.status < 300) {
        var id = await res.text();
        swal({
                title: "Thông báo",
                text: "Thành công",
                type: "success"
            },
            function() {
                window.open('/admin/in-don?id=' + id, '_blank');
                window.location.href = 'invoice'
            });
    }
    if (res.status == exceptionCode) {
        var result = await res.json()
        toastr.warning(result.defaultMessage);
    }
}

async function loadDetailInvoicePrint() {
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    var url = 'http://localhost:8080/api/invoice-detail/admin/find-by-invoice?idInvoice=' + id;
    const res = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await res.json();
    var main = ''
    var tongTienTam = 0;
    for (i = 0; i < list.length; i++) {
        tongTienTam = Number(tongTienTam) + Number(list[i].price * list[i].quantity);
        main += `<tr>
                    <td>${Number(i) + Number(1)}</td>
                    <td>${list[i].productName}</td>
                    <td>${list[i].productSize.sizeName}</td>
                    <td>${list[i].colorName}</td>
                    <td>${list[i].quantity}</td>
                    <td>${formatmoney(list[i].price)}</td>
                    <td>${formatmoney(list[i].price * list[i].quantity)}</td>
                </tr>`
    }
    document.getElementById("listDetailinvoice").innerHTML = main
    document.getElementById("tongtam").innerHTML = formatmoney(tongTienTam)
    document.getElementById("tongTientt").innerHTML = formatmoney(tongTienTam)

    var url = 'http://localhost:8080/api/invoice/admin/find-by-id?idInvoice=' + id;
    const resp = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var result = await resp.json();
    document.getElementById("mahoadon").innerHTML = "#" + result.id
    document.getElementById("ngayTao").innerHTML = result.createdDate
    if (result.voucher != null) {
        document.getElementById("giamgia").innerHTML = "- " + formatmoney(result.voucher.discount)
        document.getElementById("tongTientt").innerHTML = formatmoney(tongTienTam - result.voucher.discount)
    }
    if (result.receiverName != null) {
        document.getElementById("tenkhachhang").innerHTML = `<p>Khách Hàng</p>
            <span>${result.receiverName}</span>`
    }
}

// Call the polling function to start refreshing the data when the page loads
startInvoicePollingAdmin();

document.getElementById("sort").addEventListener("change", function() {
    loadInvoice(currentPage)
})