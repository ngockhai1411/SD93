var token = localStorage.getItem("token");

function formatDollar(num) {
    var p = num.toFixed(2).split(".");
    return p[0].split("").reverse().reduce(function(acc, num, i, orig) {
        return num + (num != "-" && i && !(i % 3) ? "," : "") + acc;
    }, "") + "." + p[1];
}

function getDateTime() {
    var now = new Date();
    var year = now.getFullYear();
    var month = now.getMonth() + 1;
    var day = now.getDate();
    var hour = now.getHours();
    var minute = now.getMinutes();
    var second = now.getSeconds(); //
    var a = 0;
    //
    if (month.toString().length == 1) {
        month = '0' + month;
    }
    if (day.toString().length == 1) {
        day = '0' + day;
    }
    if (hour.toString().length == 1) {
        hour = '0' + hour;
    }
    if (minute.toString().length == 1) {
        minute = '0' + minute;
    }
    if (second.toString().length == 1) {
        second = '0' + second;
    }
    var dateTime = year + '/' + month + '/' + day + ' ' + hour + ':' +
        minute + ':' + second;
    return dateTime;
}
setInterval(function() {
    currentTime = getDateTime();
    document.getElementById("digital-clock").innerHTML = currentTime;
}, 1000);

var date = new Date();

var current_day = date.getDay();

var day_name = '';

switch (current_day) {
    case 0:
        day_name = "Chủ nhật";
        break;
    case 1:
        day_name = "Thứ hai";
        break;
    case 2:
        day_name = "Thứ ba";
        break;
    case 3:
        day_name = "Thứ tư";
        break;
    case 4:
        day_name = "Thứ năm";
        break;
    case 5:
        day_name = "Thứ sáu";
        break;
    case 6:
        day_name = "Thứ bảy";
}

async function thongke() {
    var url = 'http://localhost:8080/api/statistic/admin/revenue-this-month';
    const response = await fetch(url, {
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var result = await response.text();
    document.getElementById("doanhThu").innerHTML = formatmoney(result)

    var url = 'http://localhost:8080/api/statistic/admin/number-admin';
    const res = await fetch(url, {
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var result = await res.text();
    document.getElementById("soLuongNV").innerHTML = result


    var url = 'http://localhost:8080/api/statistic/admin/number-product';
    const resp = await fetch(url, {
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var result = await resp.text();
    document.getElementById("soLuongMH").innerHTML = result


    var url = 'http://localhost:8080/api/statistic/admin/revenue-today';
    const respo = await fetch(url, {
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var result = await respo.text();
    document.getElementById("doanhThuNgay").innerHTML = formatmoney(result)

    var url = 'http://localhost:8080/api/statistic/admin/number-invoice-today-finish';
    const respon = await fetch(url, {
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var result = await respon.text();
    document.getElementById("donhoanthanh").innerHTML = (result)

    var url = 'http://localhost:8080/api/product/admin/san-pham-ban-chay-thang-nay';
    const respons = await fetch(url, {
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await respons.json();
    var main = '';
    for (i = 0; i < list.length; i++) {
        var listdm = '<div class="listtag">'
        for (j = 0; j < list[i].productCategories.length; j++) {
            listdm += `<a href="" class="tagcauhoi">.${list[i].productCategories[j].category.name}</a>`;
        }
        listdm += '</div>';
        main += `<tr>
                    <td>#${list[i].id}</td>
                    <td><img src="${list[i].imageBanner}" style="width: 100px;"></td>
                    <td>${list[i].code}</td>
                    <td>${listdm}</td>
                    <td>
                        Thương hiệu: ${list[i].trademark == null?'': list[i].trademark.name}<br>
                        Chất liệu: ${list[i].material == null?'': list[i].material.name}
                        Đế giày: ${list[i].sole == null?'': list[i].sole.name}
                    </td>
                    <td>${list[i].name}</td>
                    <td>${formatmoney(list[i].price)}</td>
                    <td>${list[i].quantitySold}</td>
                </tr>`
    }
    document.getElementById("listproduct").innerHTML = main

}