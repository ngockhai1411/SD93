const listFile = [];
var size = 10;
var currentPage = 0; // Thêm biến để theo dõi trang hiện tại

async function loadProduct(page, param, listcate) {
    if (param == null) {
        param = "";
    }
    var result = null;
    if (listcate == null) {
        var url = 'http://localhost:8080/api/product/public/findByParam?page=' + page + '&size=' + size + '&q=' + param;
        const response = await fetch(url, {
            method: 'GET'
        });
        result = await response.json();
    } else {
        var url = 'http://localhost:8080/api/product/public/searchFull?page=' + page + '&size=' + size;
        const response = await fetch(url, {
            method: 'POST',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify(listcate)
        });
        result = await response.json();
    }
    console.log(result)
    var list = result.content;
    var totalPage = result.totalPages;
    currentPage = page; // Cập nhật trang hiện tại sau khi load xong

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
                    Thương hiệu: ${list[i].trademark == null ? '' : list[i].trademark.name}<br>
                    Chất liệu: ${list[i].material == null ? '' : list[i].material.name}
                    Đế giày: ${list[i].sole == null ? '' : list[i].sole.name}
                </td>
                <td>${list[i].name}</td>
                <td>${formatmoney(list[i].price)}</td>
                <td>${list[i].createdTime}<br>${list[i].createdDate}</td>
                <td>${list[i].quantitySold}</td>
                <td class="sticky-col">
                    <a href="addproduct?id=${list[i].id}"><i class="fa fa-edit iconaction"></i><br></a>
                </td>
            </tr>`
    }
    document.getElementById("listproduct").innerHTML = main
    var mainpage = '';
    for (i = 1; i <= totalPage; i++) {
        mainpage += `<li onclick="goToPage(${i - 1}, '${param}', ${JSON.stringify(listcate)})" class="page-item"><a class="page-link" href="#listsp">${i}</a></li>`;
    }
    document.getElementById("pageable").innerHTML = mainpage
}

// Hàm chuyển trang (để dễ quản lý)
function goToPage(page, param, listcate) {
    loadProduct(page, param, listcate);
}

window.onload = function() {
    loadAllCategorySelect();
    loadProduct(currentPage, "", null);
}


async function loadAProduct() {
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    if (id != null) {
        var url = 'http://localhost:8080/api/product/admin/findById?id=' + id;
        const response = await fetch(url, {
            method: 'GET',
            headers: new Headers({
                'Authorization': 'Bearer ' + token
            })
        });
        var result = await response.json();
        console.log(result)
        document.getElementById("codesp").value = result.code
        document.getElementById("namesp").value = result.name
        document.getElementById("alias").value = result.alias
        document.getElementById("price").value = result.price
        document.getElementById("anhdathem").style.display = 'block'
        linkbanner = result.imageBanner
        document.getElementById("imgpreproduct").src = result.imageBanner
        tinyMCE.get('editor').setContent(result.description)
        var main = ''
        for (i = 0; i < result.productImages.length; i++) {
            main += `<div id="imgdathem${result.productImages[i].id}" class="col-md-3 col-sm-4 col-4">
                        <img style="width: 90%;" src="${result.productImages[i].linkImage}" class="image-upload">
                        <button onclick="deleteProductImage(${result.productImages[i].id})" class="btn btn-danger form-control">Xóa ảnh</button>
                    </div>`
        }
        document.getElementById("listanhdathem").innerHTML = main
        await loadAllCategorySelect();
        var listCate = []
        for (i = 0; i < result.productCategories.length; i++) {
            listCate.push(result.productCategories[i].category.id);
        }
        console.log(listCate)
        $("#listdpar").val(listCate).change();;
        var color = result.productColors;
        var mcl = ''
        for (i = 0; i < color.length; i++) {
            var blocks = '';
            var size = color[i].productSizes;
            for (j = 0; j < size.length; j++) {
                blocks += ` <div class="singelsizeblock">
                            <input value="${size[j].id}" type="hidden" class="idsize">
                            <input value="${size[j].sizeName}" placeholder="tên size" class="sizename">
                            <input value="${size[j].quantity}" placeholder="Số lượng" class="sizequantity">
                            <i onclick="deleteProductSize(${size[j].id})" class="fa fa-trash-alt trashsize"></i>
                        </div>`
            }
            mcl += `<div class="col-sm-6">
            <div class="singlecolor row">
                <div class="col-12"><i onclick="deleteProductColor(${color[i].id})" class="fa fa-trash pointer"></i></div>
                <div class="col-8 inforcolor">
                    <input type="hidden" value="${color[i].id}" class="idcolor">
                    <label class="lb-form">Tên màu:</label>
                    <input type="text" value="${color[i].colorName}" class="form-control colorName">
                    <label class="lb-form">Ảnh màu</label>
                    <input onchange="priviewImg(this)" type="file" class="form-control fileimgclo">
                </div>
                <div class="col-4 divimgpre">
                    <img class="imgpreview" src="${color[i].linkImage}" style="width: 100%;">
                </div>
                <span onclick="addSizeBlock(this)" class="pointer btnaddsize"><i class="fa fa-plus"></i> Thêm size</span>
                <div class="listsizeblock">
                   ${blocks}
                </div>
            </div></div>`
        }
        document.getElementById("listcolorblock").innerHTML = mcl
        document.getElementById("thuonghieu").value = result.trademark.id;
        document.getElementById("chatlieu").value = result.material.id;
        document.getElementById("degiay").value = result.sole.id;
    }
}

var linkbanner = '';
async function saveProduct() {
    document.getElementById("loading").style.display = 'block'
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");

    var url = 'http://localhost:8080/api/product/admin/create';
    if (id != null) {
        url = 'http://localhost:8080/api/product/admin/update';
    }
    var codesp = document.getElementById("codesp").value
    var namesp = document.getElementById("namesp").value
    var price = document.getElementById("price").value
    var alias = document.getElementById("alias").value
    var listdpar = $("#listdpar").val();
    var description = tinyMCE.get('editor').getContent()

    // Validate
    let hasError = false;
    if (!codesp) {
        toastr.warning("Vui lòng nhập mã sản phẩm.");
        document.getElementById("loading").style.display = 'none';
        return; // Dừng lại và không kiểm tra các lỗi khác
    }
    if (!namesp) {
        toastr.warning("Vui lòng nhập tên sản phẩm.");
        document.getElementById("loading").style.display = 'none';
        return;
    }
    if (!price) {
        toastr.warning("Vui lòng nhập giá sản phẩm.");
        document.getElementById("loading").style.display = 'none';
        return;
    }

    const priceRegex = /^[0-9]+$/;
    if (!priceRegex.test(price) || Number(price) < 0) {
        toastr.warning("Giá sản phẩm không hợp lệ. Vui lòng nhập là số và lớn hơn hoặc bằng 0.");
        document.getElementById("loading").style.display = 'none';
        return;
    }

    if (listdpar.length == 0) {
        toastr.warning("Vui lòng chọn danh mục sản phẩm.");
        document.getElementById("loading").style.display = 'none';
        return;
    }
    // Validate ảnh nền và ảnh phụ
    const imgbannerInput = document.getElementById("imgbanner");
    if (id == null) {
        if (!imgbannerInput || !imgbannerInput.files || imgbannerInput.files.length === 0) {
            toastr.warning("Vui lòng chọn ảnh nền.");
            document.getElementById("loading").style.display = 'none';
            return;
        }

        if (listFile.length === 0) {
            toastr.warning("Vui lòng chọn ít nhất một ảnh phụ.");
            document.getElementById("loading").style.display = 'none';
            return;
        }
    }

    if(hasError == true) {
        return;
    }


    await uploadFile(document.getElementById("imgbanner"));
    var listLinkImg = await uploadMultipleFileNotResp();

    // Gọi loadColor() và kiểm tra kết quả
    var colorValidationResult = await loadColor();
    if (colorValidationResult === false) {
        return; // Dừng nếu có lỗi validation trong loadColor()
    }


    var product = {
        "id": id,
        "code": codesp,
        "alias": alias,
        "name": namesp,
        "imageBanner": linkbanner,
        "price": price,
        "description": description,
        "listCategoryIds": listdpar,
        "linkLinkImages": listLinkImg,
        "colors": listColor,
        "trademark": {
            "id":document.getElementById("thuonghieu").value
        },
        "material": {
            "id":document.getElementById("chatlieu").value
        },
        "sole": {
            "id":document.getElementById("degiay").value
        },
    }
    console.log(product)
    const response = await fetch(url, {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(product)
    });
    var result = await response.json();
    console.log(result)
    if (response.status < 300) {
        swal({
                title: "Thông báo",
                text: "thêm/sửa sản phẩm thành công",
                type: "success"
            },
            function() {
                document.getElementById("loading").style.display = 'none'
                window.location.reload();
            });
    } else {
        swal({
                title: "Thông báo",
                text: "thêm/sửa sản phẩm thất bại",
                type: "error"
            },
            function() {
                document.getElementById("loading").style.display = 'none'
                window.location.reload();
            });
    }
}


async function deleteProduct(id) {
    var con = confirm("Bạn chắc chắn muốn xóa sản phẩm này?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/product/admin/delete?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("xóa sản phẩm thành công!");
        await new Promise(r => setTimeout(r, 1000));
        window.location.reload();
    }
    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}


async function deleteProductImage(id) {
    var con = confirm("Bạn muốn xóa ảnh này?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/product-image/admin/delete?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("xóa ảnh thành công!");
        document.getElementById("imgdathem" + id).style.display = 'none';
    }
    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}

async function deleteProductSize(id) {
    var con = confirm("Bạn muốn xóa size này?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/product-size/admin/delete?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("xóa thành công!");
        await new Promise(r => setTimeout(r, 1000));
        loadAProduct();
    }
    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}

async function deleteProductColor(id) {
    var con = confirm("Bạn muốn xóa màu này?");
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/product-color/admin/delete?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        toastr.success("xóa thành công!");
        await new Promise(r => setTimeout(r, 1000));
        loadAProduct();
    }
    if (response.status == exceptionCode) {
        var result = await response.json()
        toastr.warning(result.defaultMessage);
    }
}


var listColor = [];
var listLinkImgCt = [];
async function loadColor() {
    var list = document.getElementById("listcolorblock").getElementsByClassName("singlecolor");
    var listF = [];
    listColor = []; // Reset listColor before each call
    let errors = []; // Array to store validation errors

    for (i = 0; i < list.length; i++) {
        var singleColor = list[i];
        var idcolor = singleColor.getElementsByClassName("idcolor")[0];
        var colorNameInput = singleColor.getElementsByClassName("colorName")[0];
        var fileimgcloInput = singleColor.getElementsByClassName("fileimgclo")[0];
        var divimgpre = singleColor.getElementsByClassName("divimgpre")[0];
        var imgpreview = divimgpre.getElementsByClassName("imgpreview")[0];

        // Validate tên màu
        var colorName = colorNameInput.value;
        if (!colorName) {
            toastr.warning("Vui lòng nhập tên màu.");
            document.getElementById("loading").style.display = 'none';
            return false; // Dừng lại nếu có lỗi tên màu
        }

        // Validate ảnh màu
        if (!fileimgcloInput || (!fileimgcloInput.files || fileimgcloInput.files.length === 0) && !imgpreview.src) {
            toastr.warning("Vui lòng chọn ảnh màu cho " + colorName);
            document.getElementById("loading").style.display = 'none';
            return false;
        }


        listF.push(fileimgcloInput);
        var obj = {
            "id": idcolor.value == '' ? null : idcolor.value,
            "colorName": colorName,
            "hasFile": fileimgcloInput.files.length == 0 ? false : true,
            "linkImage": null
        }
        var listsizes = []
        var sizeblockList = singleColor.getElementsByClassName("singelsizeblock");
        for (j = 0; j < sizeblockList.length; j++) {
            var size = sizeblockList[j];
            var idsize = size.getElementsByClassName("idsize")[0];
            var sizeNameInput = size.getElementsByClassName("sizename")[0];
            var sizeQuantityInput = size.getElementsByClassName("sizequantity")[0];

            // Validate tên size
            var sizeName = sizeNameInput.value
            const sizeNameRegex = /^[0-9]+$/;
            if (sizeName == "" || !sizeName || sizeName == null) {
                errors.push("Vui lòng nhập tên size cho màu " + colorName);
            }

            else if (Number(sizeName) < 0) {
                errors.push("Tên size phải lớn hơn hoặc bằng 0 cho màu: "+colorName);
            }
            else if (!sizeNameRegex.test(sizeName) ) {
                errors.push("Vui lòng nhập tên size phải là số cho màu " + colorName);
            }
            // Validate số lượng và định dạng của size
            var sizeQuantity = sizeQuantityInput.value
            const quantityRegex = /^[0-9]+$/;
            if (sizeQuantity == "" || !sizeQuantity || sizeQuantity == null) {
                errors.push("Vui lòng nhập số lượng size cho màu " + colorName +" size:"+sizeName);
            }
            else if (Number(sizeQuantity) < 0) {
                errors.push("Số lượng size phải lớn hơn hoặc bằng 0 cho màu: "+colorName +" size:"+sizeName);
            }
            else if(!quantityRegex.test(sizeQuantity) ) {
                errors.push("Số lượng size phải là số cho màu: "+colorName +" size:"+sizeName);
            }


            var objsize = {
                "id": idsize.value == '' ? null : idsize.value,
                "sizeName": sizeName,
                "quantity": sizeQuantity,
            }
            listsizes.push(objsize)
        }
        console.log(listsizes);
        obj.size = listsizes;
        listColor.push(obj)
    }

    if (errors.length > 0) {
        errors.forEach(error => {
            toastr.warning(error);
            document.getElementById("loading").style.display = 'none'
        });
        return false;
    }

    var listImg = await uploadMultipleFile(listF);
    for (i = 0; i < listImg.length; i++) {
        for (j = 0; j < listColor.length; j++) {
            if (listColor[j].hasFile == true) {
                if (listColor[j].linkImage == null) {
                    listColor[j].linkImage = listImg[i].link;
                    break;
                }
            }
        }
    }

    console.log(listColor)
    return true;
}

async function uploadMultipleFile(listF) {
    const formData = new FormData()
    for (i = 0; i < listF.length; i++) {
        formData.append("file", listF[i].files[0])
    }
    var urlUpload = 'http://localhost:8080/api/public/upload-multiple-file-order-response';
    const res = await fetch(urlUpload, {
        method: 'POST',
        body: formData
    });
    return await res.json();
}

async function uploadMultipleFileNotResp() {
    const formData = new FormData()
    for (i = 0; i < listFile.length; i++) {
        formData.append("file", listFile[i])
    }
    var urlUpload = 'http://localhost:8080/api/public/upload-multiple-file';
    const res = await fetch(urlUpload, {
        method: 'POST',
        body: formData
    });
    if (res.status < 300) {
        return await res.json();
    } else {
        return [];
    }
}


async function uploadFile(filePath) {
    const formData = new FormData()
    formData.append("file", filePath.files[0])
    var urlUpload = 'http://localhost:8080/api/public/upload-file';
    const res = await fetch(urlUpload, {
        method: 'POST',
        body: formData
    });
    if (res.status < 300) {
        linkbanner = await res.text();
    }
}

function priviewImg(e) {
    var dv = e.parentNode.parentNode;
    var img = dv.getElementsByClassName("divimgpre")[0].getElementsByClassName("imgpreview")[0]
    const [file] = e.files
    if (file) {
        img.src = URL.createObjectURL(file)
    }
}

function addBlockColor() {
    var main =
        `<div class="col-sm-6">
        <div class="singlecolor row">
            <div class="col-12"><i onclick="removeColorBlock(this)" class="fa fa-trash pointer"></i></div>
            <div class="col-8 inforcolor">
                <input type="hidden" class="idcolor">
                <label class="lb-form">Tên màu:</label>
                <input type="text" class="form-control colorName">
                <label class="lb-form">Ảnh màu</label>
                <input onchange="priviewImg(this)" type="file" class="form-control fileimgclo">
            </div>
            <div class="col-4 divimgpre">
                <img class="imgpreview" src="" style="width: 100%;">
            </div>
            <span onclick="addSizeBlock(this)" class="pointer btnaddsize"><i class="fa fa-plus"></i> Thêm size</span>
            <div class="listsizeblock">
                <!-- <div class="singelsizeblock">
                    <input type="hidden" class="idsize">
                    <input placeholder="tên size" class="sizename">
                    <input placeholder="Số lượng" value="0" class="sizequantity">
                    <i onclick="removeInputSize(this)" class="fa fa-trash-alt trashsize"></i>
                </div> -->
            </div>
        </div></div>`
    document.getElementById("listcolorblock").insertAdjacentHTML('beforeend', main);
}

function addSizeBlock(e) {
    var dv = e.parentNode;
    var listSize = dv.getElementsByClassName("listsizeblock")[0];
    var main = `<div class="singelsizeblock">
                    <input type="hidden" class="idsize">
                    <input placeholder="tên size" class="sizename">
                    <input placeholder="Số lượng" value="0" class="sizequantity">
                    <i onclick="removeInputSize(this)" class="fa fa-trash-alt trashsize"></i>
                </div>`
    listSize.insertAdjacentHTML('beforeend', main);
}

function removeColorBlock(e) {
    var dv = e.parentNode.parentNode;
    dv.remove();
}

function removeInputSize(e) {
    var dv = e.parentNode;
    dv.remove();
}

async function loadAllCategorySelect() {
    var url = 'http://localhost:8080/api/category/public/findAllList';
    const response = await fetch(url, {
        method: 'GET'
    });
    var list = await response.json();
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<option value="${list[i].id}">${list[i].name}</option>`
    }
    document.getElementById("listdpar").innerHTML = main
    const ser = $("#listdpar");
    ser.select2({
        placeholder: "Chọn danh mục sản phẩm",
    });
}

function loadInit() {
    $('input#choosefile').change(function() {
        var files = $(this)[0].files;
    });
    document.querySelector('#choosefile').addEventListener("change", previewImages);

    function previewImages() {
        var files = $(this)[0].files;
        for (i = 0; i < files.length; i++) {
            listFile.push(files[i]);
        }

        var preview = document.querySelector('#preview');

        // if (this.files) {
        //     [].forEach.call(this.files, readAndPreview);
        // }
        for (i = 0; i < files.length; i++) {
            readAndPreview(files[i]);
        }

        function readAndPreview(file) {

            if (!/\.(jpe?g|png|gif)$/i.test(file.name)) {
                return alert(file.name + " is not an image");
            }

            var reader = new FileReader(file);

            reader.addEventListener("load", function() {
                document.getElementById("chon-anhs").className = 'col-sm-3';
                document.getElementById("chon-anhs").style.height = '100px';
                document.getElementById("chon-anhs").style.marginTop = '5px';
                document.getElementById("choose-image").style.height = '120px';
                document.getElementById("numimage").innerHTML = '';
                document.getElementById("camera").style.fontSize = '20px';
                document.getElementById("camera").style.marginTop = '40px';
                document.getElementById("camera").className = 'fas fa-plus';
                document.getElementById("choose-image").style.width = '90%';

                var div = document.createElement('div');
                div.className = 'col-md-3 col-sm-6 col-6';
                div.style.height = '120px';
                div.style.paddingTop = '5px';
                div.marginTop = '100px';
                preview.appendChild(div);

                var img = document.createElement('img');
                img.src = this.result;
                img.style.height = '85px';
                img.style.width = '90%';
                img.className = 'image-upload';
                img.style.marginTop = '5px';
                div.appendChild(img);

                var button = document.createElement('button');
                button.style.height = '30px';
                button.style.width = '90%';
                button.innerHTML = 'xóa'
                button.className = 'btn btn-warning';
                div.appendChild(button);

                button.addEventListener("click", function() {
                    div.remove();
                    console.log(listFile.length)
                    for (i = 0; i < listFile.length; i++) {
                        if (listFile[i] === file) {
                            listFile.splice(i, 1);
                        }
                    }
                    console.log(listFile.length)
                });
            });

            reader.readAsDataURL(file);

        }

    }

}



async function loadAllProductList(){
    var search = document.getElementById("search").value
    var url = 'http://localhost:8080/api/product/public/findAll-list?search=' + search
    const response = await fetch(url, {
        method: 'GET'
    });
    var list = await response.json();
    var main = "";
    for(i=0; i<list.length; i++){
        main += `<tr>
                  <td>${list[i].code}</td>
                  <td>${list[i].name}</td>
                  <td>${formatmoney(list[i].price)}</td>
                  <td><button onclick="loadChiTietMauSac(${list[i].id})" data-bs-toggle="modal" data-bs-target="#addtk" class="btn btn-primary">Chọn</button></td>
                </tr>`
    }
    document.getElementById("listproduct").innerHTML = main;
}

async function loadChiTietMauSac(idproduct){
    var url = 'http://localhost:8080/api/product/public/findById?id=' + idproduct
    const response = await fetch(url, {
        method: 'GET'
    });
    var result = await response.json();
    console.log(result)
    var list = result.productColors
    document.getElementById("listspchitiet").innerHTML = "";
    var main = document.getElementById("listspchitiet");
    for(i=0; i<list.length; i++){
        var trfirst = document.createElement("tr");
        var tdfirst = document.createElement("td");
        tdfirst.rowSpan = list[i].productSizes.length
        tdfirst.innerHTML = list[i].colorName
        trfirst.appendChild(tdfirst)

        if(list[i].productSizes.length > 0){
            var td1 = document.createElement("td");
            td1.innerHTML = list[i].productSizes[0].sizeName;
            var td2 = document.createElement("td");
            td2.innerHTML = list[i].productSizes[0].quantity;
            var td3 = document.createElement("td");

            var checked = checkTonTai(list[i].productSizes[0].id)
            var ck =''
            var onc = '';
            if(checked == true){
                onc = `removeTam(${list[i].productSizes[0].id}, this)`
                ck = 'checked'
            }
            else{
                onc = `addTam(${list[i].productSizes[0].id}, this)`
            }
            td3.innerHTML = `<input onchange="${onc}"  ${ck}  type="checkbox">`


            trfirst.appendChild(td1)
            trfirst.appendChild(td2)
            trfirst.appendChild(td3)
        }
        main.appendChild(trfirst)
        if(list[i].productSizes.length > 1){
            for(j=1; j< list[i].productSizes.length; j++){
                var trsecond = document.createElement("tr");
                var td1 = document.createElement("td");
                td1.innerHTML = list[i].productSizes[j].sizeName;
                var td2 = document.createElement("td");
                td2.innerHTML = list[i].productSizes[j].quantity;
                var td3 = document.createElement("td");
                var checked = checkTonTai(list[i].productSizes[j].id)
                var onc = '';
                var ck =''
                if(checked == true){
                    onc = `removeTam(${list[i].productSizes[j].id}, this)`
                    ck = 'checked'
                }
                else{
                    onc = `addTam(${list[i].productSizes[j].id}, this)`
                }
                td3.innerHTML = `<input onchange="${onc}" ${ck} type="checkbox">`
                td3.onchange = function (){
                    if(checked == true){
                        removeTam(list[i].productSizes[j].id)
                    }
                    else{
                        addTam(list[i].productSizes[j].id)
                    }
                }
                trsecond.appendChild(td1)
                trsecond.appendChild(td2)
                trsecond.appendChild(td3)
                main.appendChild(trsecond)
            }
        }

    }
}

var listProductTam = [];
async function addTam(idsize, e){
    if(e.checked == false){
        removeTam(idsize)
        return;
    }
    if(checkTonTai(idsize) == true){
        return;
    }
    var url = 'http://localhost:8080/api/product-size/public/find-by-id?id=' + idsize
    const response = await fetch(url, {
        method: 'GET'
    });
    var result = await response.json();
    result.quantity = 1;
    listProductTam.push(result);
    loadSizeProduct();
}

function checkTonTai(idsize){
    for(var k=0; k< listProductTam.length; k++){
        if(listProductTam[k].productSize.id == idsize){
            return true;
        }
    }
    return false;
}

function removeTam(idsize){
    for(i=0; i< listProductTam.length; i++){
        if(listProductTam[i].productSize.id == idsize){
            listProductTam.splice(i, 1);
        }
    }
    loadSizeProduct();
}

function loadSizeProduct() {
    var main = '';
    var tongtientt = 0;
    for (i = 0; i < listProductTam.length; i++) {
        tongtientt = Number(tongtientt) + Number(listProductTam[i].product.price) * Number(listProductTam[i].quantity);
        main += `<tr>
            <td>Size: ${listProductTam[i].productSize.sizeName}, Màu: ${listProductTam[i].productColor.colorName}<br>${listProductTam[i].product.name}</td>
            <td>${formatmoney(listProductTam[i].product.price)}</td>
            <td>
                <div class="clusinp"><button onclick="upDownQuantity(${listProductTam[i].productSize.id},-1)" class="cartbtn"> - </button>
                <input value="${listProductTam[i].quantity}" class="inputslcart" type="number" min="1">
                <button onclick="upDownQuantity(${listProductTam[i].productSize.id},1)" class="cartbtn"> + </button></div>
            </td>
            <td><i onclick="removeTam(${listProductTam[i].productSize.id})" class="fa fa-trash-alt pointer"></i></td>
        </tr>`;
    }
    document.getElementById("listproducttam").innerHTML = main;
    document.getElementById("tongtientt").innerHTML = formatmoney(tongtientt);

    // Add event listeners to the quantity inputs
    const quantityInputs = document.querySelectorAll('#listproducttam .inputslcart');
    quantityInputs.forEach(input => {
        input.addEventListener("change", function () {
            var quan = parseInt(this.value, 10);
            if (isNaN(quan) || quan < 1) {
                toastr.error("Số lượng sản phẩm phải lớn hơn hoặc bằng 1.");
                this.value = 1;
                this.focus();
            }

            // Update the quantity in the listProductTam
            for (var j = 0; j < listProductTam.length; j++) {
                var inputId = this.parentNode.parentNode.parentNode.querySelector('i').onclick.toString()
                var idSize =  inputId.match(/\d+/)[0]
                if (listProductTam[j].productSize.id == idSize) {
                    listProductTam[j].quantity = Number(this.value);
                    break;
                }
            }
            loadSizeProduct();
        });
    });
}

function upDownQuantity(idsize, quantity) {
    for (i = 0; i < listProductTam.length; i++) {
        if (listProductTam[i].productSize.id == idsize) {
            var newQuantity = Number(listProductTam[i].quantity) + Number(quantity);
            if(newQuantity < 1){
                toastr.error("Số lượng sản phẩm phải lớn hơn hoặc bằng 1.");
                return;
            }
            listProductTam[i].quantity = newQuantity;
            if (listProductTam[i].quantity == 0) {
                removeTam(idsize)
            }
            break;
        }
    }
    loadSizeProduct();
}

async function loadSelect() {
    var response = await fetch('http://localhost:8080/api/material/public/all', {
    });
    var list = await response.json();
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<option value="${list[i].id}">${list[i].name}</option>`
    }
    document.getElementById("chatlieu").innerHTML = main

    var response = await fetch('http://localhost:8080/api/trademark/public/all', {
    });
    var list = await response.json();
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<option value="${list[i].id}">${list[i].name}</option>`
    }
    document.getElementById("thuonghieu").innerHTML = main

    var response = await fetch('http://localhost:8080/api/sole/public/all', {
    });
    var list = await response.json();
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<option value="${list[i].id}">${list[i].name}</option>`
    }
    document.getElementById("degiay").innerHTML = main
}
