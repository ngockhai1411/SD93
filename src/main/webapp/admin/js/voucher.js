var token = localStorage.getItem("token");
var size = 10;

async function loadVoucher(page, start, end) {
    var url = 'http://localhost:8080/api/voucher/admin/findAll-page?page=' + page + '&size=' + size;
    if (start != null && start != "" && end != null && end != "" && start != 'null' && end != 'null') {
        url += '&start=' + start + '&end=' + end
    }
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
        }),
    });
    var result = await response.json();
    console.log(result)
    var list = result.content;
    var totalPage = result.totalPages;
    var main = '';
    for (i = 0; i < list.length; i++) {
        // Chuyển đổi ngày bắt đầu và kết thúc thành đối tượng Date
        const startDate = new Date(list[i].startDate);
        const endDate = new Date(list[i].endDate);
        const today = new Date();

        // Lấy timestamp theo múi giờ địa phương
        const startDateTimestamp = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()).getTime();
        const endDateTimestamp = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()).getTime();
        const todayTimestamp = new Date(today.getFullYear(), today.getMonth(), today.getDate()).getTime();

        let statusText;
        if (startDateTimestamp > todayTimestamp) {
            statusText = '<span class="not-yet">Chưa áp dụng</span>';
        }
        else if (endDateTimestamp < todayTimestamp) {
            statusText = '<span class="expired">Hết hạn</span>';
        } else if (list[i].block == true) {
            statusText = '<span class="locked">Đã khóa</span>'
        } else {
            statusText = '<span class="actived">Đang hoạt động</span>';
        }
        main += `<tr>
                    <td>${list[i].id}</td>
                    <td>${list[i].code}</td>
                    <td>${list[i].name}</td>
                    <td>${formatmoney(list[i].minAmount)}</td>
                    <td>${formatmoney(list[i].discount)}</td>
                    <td>${list[i].startDate}</td>
                    <td>${list[i].endDate}</td>
                    <td>${statusText}</td>
                    <td class="sticky-col">
                        <i onclick="deleteVoucher(${list[i].id})" class="fa fa-trash iconaction"></i><br>
                        <a href="addvoucher?id=${list[i].id}"><i class="fa fa-edit iconaction"></i></a>
                    </td>
                </tr>`
    }
    document.getElementById("listvoucher").innerHTML = main
    var mainpage = ''
    for (i = 1; i <= totalPage; i++) {
        mainpage += `<li onclick="loadVoucher(${(Number(i) - 1)},'${start}','${end}')" class="page-item"><a class="page-link" href="#listsp">${i}</a></li>`
    }
    document.getElementById("pageable").innerHTML = mainpage
}

async function filter() {
    var start = document.getElementById("start").value
    var end = document.getElementById("end").value
    if (start != "" && end != "") {
        loadVoucher(0, start, end);
    }
}


async function loadAVoucher() {
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    if (id != null) {
        var url = 'http://localhost:8080/api/voucher/admin/findById?id=' + id;
        const response = await fetch(url, {
            method: 'GET',
            headers: new Headers({
                'Authorization': 'Bearer ' + token
            })
        });
        var result = await response.json();
        document.getElementById("code").value = result.code
        document.getElementById("namevoucher").value = result.name
        document.getElementById("minamount").value = result.minAmount
        document.getElementById("discount").value = result.discount
        document.getElementById("from").value = result.startDate
        document.getElementById("to").value = result.endDate
        result.block == true ? document.getElementById("lockvoucher").checked = true : false;
    }
}



async function saveVoucher() {
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    var code = document.getElementById("code").value
    var namevoucher = document.getElementById("namevoucher").value
    var minamount = document.getElementById("minamount").value
    var discount = document.getElementById("discount").value
    var from = document.getElementById("from").value
    var to = document.getElementById("to").value
    var lockvoucher = document.getElementById("lockvoucher").checked

    // Kiểm tra nếu các trường dữ liệu bị trống
    if (!code || !namevoucher || !minamount || !discount || !from || !to) {
        toastr.warning("Vui lòng điền đầy đủ tất cả các trường.");
        return;
    }

    // Kiểm tra số tiền tối thiểu và giảm giá phải lớn hơn hoặc bằng 0
    if (Number(minamount) < 0) {
        toastr.warning("Số tiền đơn hàng tối thiểu phải lớn hơn hoặc bằng 0.");
        return;
    }
    if (Number(discount) < 0) {
        toastr.warning("Số tiền giảm giá phải lớn hơn hoặc bằng 0.");
        return;
    }


    // Kiểm tra điều kiện giảm giá
    if (Number(discount) >= Number(minamount)) {
        toastr.warning("Số tiền giảm giá phải nhỏ hơn số tiền đơn hàng tối thiểu");
        return;
    }
    // Lấy ngày hiện tại theo múi giờ địa phương
    const today = new Date();
    const todayTimestamp = new Date(today.getFullYear(), today.getMonth(), today.getDate()).getTime();

    // Chuyển đổi ngày thành timestamp
    const startDate = new Date(from);
    const endDate = new Date(to);
    const startDateTimestamp = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()).getTime();
    const endDateTimestamp = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()).getTime();

    // Kiểm tra ngày bắt đầu phải lớn hơn hoặc bằng ngày hiện tại
    if (startDateTimestamp < todayTimestamp) {
        toastr.warning("Ngày bắt đầu phải lớn hơn hoặc bằng ngày hiện tại.");
        return;
    }

    // Kiểm tra ngày kết thúc phải lớn hơn hoặc bằng ngày hiện tại
    if (endDateTimestamp < todayTimestamp) {
        toastr.warning("Ngày kết thúc phải lớn hơn hoặc bằng ngày hiện tại.");
        return;
    }


    // Kiểm tra ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu
    if (endDateTimestamp < startDateTimestamp) {
        toastr.warning("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.");
        return;
    }

    var url = 'http://localhost:8080/api/voucher/admin/create';
    if (id != null) {
        url = 'http://localhost:8080/api/voucher/admin/update';
    }

    var voucher = {
        "id": id,
        "code": code,
        "name": namevoucher,
        "discount": discount,
        "minAmount": minamount,
        "startDate": from,
        "endDate": to,
        "block": lockvoucher
    }

    const response = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(voucher)
    });

    if (response.status < 300) {
        swal({
                title: "Thông báo",
                text: "Thêm/Sửa voucher thành công!",
                type: "success"
            },
            function () {
                window.location.href = 'voucher'
            });
    }

    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}



async function deleteVoucher(id) {
    var con = confirm("Bạn chắc chắn muốn xóa voucher này?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/voucher/admin/delete?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("xóa voucher thành công!");
        await new Promise(r => setTimeout(r, 1000));
        window.location.reload();
    }
    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}


async function loadVoucherDonHang() {
    const response = await fetch('http://localhost:8080/api/voucher/admin/voucher-kha-dung', {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
        }),
    });
    var list = await response.json();
    var main = '<option value="-1">--- Chọn khuyến mại ---</option>';
    for (i = 0; i < list.length; i++) {
        main += `<option value="${list[i].id}">${list[i].name} - tối thiểu: ${formatmoney(list[i].minAmount)} - giảm giá: ${formatmoney(list[i].discount)}</option>`
    }
    document.getElementById("khuyenmai").innerHTML = main
}