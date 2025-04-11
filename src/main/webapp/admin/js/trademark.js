let existingTrademarkNames = []; // Lưu trữ tên thương hiệu hiện có

async function loadResult() {
    var url = 'http://localhost:8080/api/trademark/public/all';
    const response = await fetch(url, {
    });
    var list = await response.json();

    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<tr>
                    <td>${list[i].id}</td>
                    <td>${list[i].name}</td>
                    <td class="sticky-col">
                        <i onclick="deleteResult(${list[i].id})" class="fa fa-trash-alt iconaction"></i>
                        <a href="#" data-bs-toggle="modal" data-bs-target="#addtk" onclick="loadAResult(${list[i].id})"><i class="fa fa-edit iconaction"></i></a>
                    </td>
                </tr>`
    }
    document.getElementById("listresult").innerHTML = main
    // Tải danh sách tên thương hiệu khi trang được tải
    loadExistingTrademarkNames()
}

async function loadExistingTrademarkNames() {
    var url = 'http://localhost:8080/api/trademark/public/all';
    const response = await fetch(url);
    const list = await response.json();
    existingTrademarkNames = list.map(item => item.name.toLowerCase());
}


function clearData() {
    document.getElementById("idres").value = "";
    document.getElementById("name").value = "";
    document.getElementById("name-error").textContent = "";
}

async function loadAResult(id) {
    const response = await fetch('http://localhost:8080/api/trademark/public/findById?id=' + id, {
    });
    var result = await response.json();
    document.getElementById("idres").value = result.id;
    document.getElementById("name").value = result.name;
    document.getElementById("name-error").textContent = "";
}

async function saveResult() {
    const newName = document.getElementById("name").value.trim(); // Lấy giá trị input
    const nameErrorSpan =  document.getElementById("name-error"); // DOM tới span thông báo lỗi
    nameErrorSpan.textContent = ""; // reset thông báo lỗi

    if (!newName) {
        nameErrorSpan.textContent = "Tên thương hiệu không được để trống."; // Hiển thị lỗi
        return; // Dừng việc gửi request
    }


    const newNameLower = newName.toLowerCase();
    const isDuplicate = existingTrademarkNames.includes(newNameLower);


    if (isDuplicate) {
        nameErrorSpan.textContent = "Tên thương hiệu đã tồn tại.";
        return; // Không gửi request nếu có trùng
    }

    document.getElementById("name-error").textContent = ""; // Xóa thông báo lỗi cũ (nếu có)
    var payload = {
        "id": document.getElementById("idres").value,
        "name": document.getElementById("name").value,
    }

    const response = await fetch('http://localhost:8080/api/trademark/admin/create', {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(payload)
    });


    if (response.status < 300) {
        toastr.success("Thành công");
        loadResult();
        $('#addtk').modal('hide')
    }
    if (response.status == exceptionCode) {
        var result = await response.json();
        if (result.defaultMessage) {
            document.getElementById("name-error").textContent = result.defaultMessage;
        }
    }
}


async function deleteResult(id) {
    var con = confirm("Bạn chắc chắn muốn xóa item này?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/trademark/admin/delete?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("xóa thành công!");
        loadResult();
    }
    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}