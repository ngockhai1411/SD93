var token = localStorage.getItem("token");
var size = 10;
async function loadCategory(page, param) {
    if (param == null) {
        param = "";
    }
    var url = 'http://localhost:8080/api/category/public/search?page=' + page + '&size=' + size + '&q=' + param;
    const response = await fetch(url, {
        method: 'GET'
    });
    var result = await response.json();
    console.log(result)
    var list = result.content;
    var totalPage = result.totalPages;

    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<tr>
                    <td>${list[i].id}</td>
                    <td><img class="imgcate" src="${list[i].imageBanner}"></td>
                    <td>${list[i].name} ${list[i].isPrimary==true?'<span class="dmchinh"> <i class="fa fa-check-circle"></i> danh mục chính</span>':''}</td>
                    <td>${list[i].categoryParentName == null?'':list[i].categoryParentName}</td>
                    <td class="sticky-col">
                        <i onclick="deleteCategory(${list[i].id})" class="fa fa-trash-alt iconaction"></i>
                        <a href="addcategory?id=${list[i].id}"><i class="fa fa-edit iconaction"></i></a>
                    </td>
                </tr>`
    }
    document.getElementById("listcategory").innerHTML = main
    var mainpage = ''
    for (i = 1; i <= totalPage; i++) {
        mainpage += `<li onclick="loadCategory(${(Number(i) - 1)},${param})" class="page-item"><a class="page-link" href="#listsp">${i}</a></li>`
    }
    document.getElementById("pageable").innerHTML = mainpage
}



async function loadACategory() {
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    if (id != null) {
        var url = 'http://localhost:8080/api/category/admin/findById?id=' + id;
        const response = await fetch(url, {
            method: 'GET',
            headers: new Headers({
                'Authorization': 'Bearer ' + token
            })
        });
        var result = await response.json();
        document.getElementById("catename").value = result.name
        document.getElementById("imgpreview").src = result.imageBanner
        result.isPrimary == true ? document.getElementById("primaryCate").checked = true : false;
        linkImage = result.imageBanner
        if (result.categoryParentId != null) {
            await loadAllCategory();
            document.getElementById("listdpar").value = result.categoryParentId
            document.getElementById("select2-listdpar-container").innerText = result.categoryParentName
        }
    }
}

async function loadAllCategory() {
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    var url = 'http://localhost:8080/api/category/public/findPrimaryCategory';
    const response = await fetch(url, {
        method: 'GET'
    });
    var list = await response.json();
    var main = '<option value="-1">Chọn danh mục cha</option>';
    for (i = 0; i < list.length; i++) {
        if (list[i].id != id) {
            main += `<option value="${list[i].id}">${list[i].name}</option>`
        }
    }
    document.getElementById("listdpar").innerHTML = main
    const ser = $("#listdpar");
    ser.select2({
        placeholder: "Chọn danh mục cha",
    });
}


var linkImage = ''
async function saveCategory() {
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    var catename = document.getElementById("catename").value
    var cateparent = document.getElementById("listdpar").value
    var primaryCate = document.getElementById("primaryCate").checked

    // Kiểm tra nếu tên danh mục bị bỏ trống
    if (!catename) {
        toastr.warning("Vui lòng nhập tên danh mục.");
        return; // Dừng thực thi hàm nếu tên danh mục trống
    }


    const filePath = document.getElementById('fileimage')
    const formData = new FormData()
    formData.append("file", filePath.files[0])
    var urlUpload = 'http://localhost:8080/api/public/upload-file';
    const res = await fetch(urlUpload, {
        method: 'POST',
        body: formData
    });
    if (res.status < 300) {
        linkImage = await res.text();
    }
    var url = 'http://localhost:8080/api/category/admin/create';
    if (id != null) {
        url = 'http://localhost:8080/api/category/admin/update';
    }
    var category = null;
    if (cateparent != -1) {
        category = {
            "id": id,
            "name": catename,
            "imageBanner": linkImage,
            "isPrimary": primaryCate,
            "category": {
                "id": cateparent
            }
        }
    } else {
        category = {
            "id": id,
            "name": catename,
            "isPrimary": primaryCate,
            "imageBanner": linkImage
        }
    }
    const response = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(category)
    });
    if (response.status < 300) {
        swal({
                title: "Thông báo",
                text: "thêm/sửa danh mục thành công!",
                type: "success"
            },
            function() {
                window.location.href = 'danhmuc'
            });
    }
    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}

async function deleteCategory(id) {
    var con = confirm("Bạn chắc chắn muốn xóa danh mục này?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/category/admin/delete?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("xóa danh mục thành công!");
        await new Promise(r => setTimeout(r, 1000));
        window.location.reload();
    }
    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}


async function loadAllCategorySelect() {
    var url = 'http://localhost:8080/api/category/public/findAllList';
    const response = await fetch(url, {
        method: 'GET'
    });
    var list = await response.json();
    var main = '<option value="-1">Tất cả danh mục</option>';
    for (i = 0; i < list.length; i++) {
        main += `<option value="${list[i].id}">${list[i].name}</option>`
    }
    document.getElementById("listdpar").innerHTML = main
    const ser = $("#listdpar");
    ser.select2({
        placeholder: "Chọn danh mục",
    });
}