var idColorCart = -1;
var listcart = localStorage.getItem("product_cart");
async function addCart(product, check) {
    var inputElement = document.getElementById("inputslcart");
    var quan = parseInt(inputElement.value, 10);

    if (isNaN(quan) || quan < 1) {
        toastr.error("Số lượng sản phẩm phải lớn hơn hoặc bằng 1.");
        return;
    }

    // phần còn lại của code addCart
    var sizeId = null;
    try {
        sizeId = document.querySelector('input[name="sizepro"]:checked').value
    } catch (error) {
        toastr.error("Bạn chưa chọn kích thước sản phẩm");
        throw error
    }
    var color = null;
    var size = null;
    var listColor = product.productColors;
    for (i = 0; i < listColor.length; i++) {
        if (listColor[i].id == idColorCart) {
            color = listColor[i];
        }
    }
    var listSize = color.productSizes;
    for (i = 0; i < listSize.length; i++) {
        if (listSize[i].id == sizeId) {
            size = listSize[i];
        }
    }
    var obj = {
        "product": product,
        "color": color,
        "size": size,
        "quantiy": document.getElementById("inputslcart").value
    }
    if (localStorage.getItem("product_cart") == null) {
        var listproduct = [];
        listproduct.push(obj);
        window.localStorage.setItem('product_cart', JSON.stringify(listproduct));
        loadCartMenu();
        if(check != null){
            window.location.href = 'cart'
        }
        return;
    } else {
        var list = JSON.parse(localStorage.getItem("product_cart"));
        console.log(list)
        for (i = 0; i < list.length; i++) {
            if (Number(list[i].size.id) == Number(sizeId)) {
                list[i].quantiy = Number(list[i].quantiy) + Number(obj.quantiy)
                window.localStorage.setItem('product_cart', JSON.stringify(list));
                toastr.success("Thêm giỏ hàng thành công");
                return;
            }
        }
        list.push(obj);
        window.localStorage.setItem('product_cart', JSON.stringify(list));
        toastr.success("Thêm giỏ hàng thành công");
        loadCartMenu();
        if(check != null){
            window.location.href = 'cart'
        }
    }
}

async function loadAllCart() {
    var listcart = localStorage.getItem("product_cart");
    if (listcart == null) {
        return;
    }
    var list = JSON.parse(localStorage.getItem("product_cart"));
    console.log(list)
    var main = ''
    var total = 0;
    for (i = 0; i < list.length; i++) {
        var price = list[i].quantiy * list[i].product.price;
        if (price < 0 || isNaN(price)) {
            price = 0;
        }
        main += `<tr>
                    <td>
                        <a href="detail?id=${list[i].product.id}&name=${list[i].product.alias}"><img class="imgprocart" src="${list[i].product.imageBanner}"></a>
                        <div class="divnamecart">
                            <a href="detail?id=${list[i].product.id}&name=${list[i].product.alias}" class="nameprocart">${list[i].product.name}</a>
                            <p class="sizecart">${list[i].color.colorName} / ${list[i].size.sizeName}</p>
                        </div>
                    </td>
                    <td><p class="boldcart">${formatmoney(list[i].product.price)}</p></td>
                    <td>
                        <div class="clusinp"><button onclick="upDownQuantity(${list[i].size.id},-1)" class="cartbtn"> - </button>
                        <input value="${list[i].quantiy}" class="inputslcart" type="number" min="1">
                        <button onclick="upDownQuantity(${list[i].size.id},1)" class="cartbtn"> + </button></div>
                    </td>
                    <td>
                        <div class="tdpricecart">
                            <p class="boldcart">${formatmoney(price)}</p>
                            <p onclick="remove(${list[i].color.id})" class="delcart"><i class="fa fa-trash-o facartde"></i></p>
                        </div>
                    </td>
                </tr>`
        total += Number(price)
    }
    document.getElementById("listcartDes").innerHTML = main
    loadAllCartMobile();
    document.getElementById("slcart").innerHTML = list.length
    document.getElementById("tonggiatien").innerHTML = formatmoney(total)

    // Add event listeners to the quantity inputs
    const quantityInputs = document.querySelectorAll('#listcartDes .inputslcart');
    quantityInputs.forEach(input => {
        input.addEventListener("change", function () {
            var quan = parseInt(this.value, 10);
            if (isNaN(quan) || quan < 1) {
                toastr.error("Số lượng sản phẩm phải lớn hơn hoặc bằng 1.");
                this.value = 1; // set input value to 1
                this.focus();
            }
            // Update the quantity in the listProductTam
            var list = JSON.parse(localStorage.getItem("product_cart"));
            for (var j = 0; j < list.length; j++) {
                if (list[j].size.id == this.parentNode.parentNode.parentNode.querySelector('button').onclick.toString().match(/\d+/)[0]) {
                    list[j].quantiy = this.value;
                    break;
                }
            }
            window.localStorage.setItem('product_cart', JSON.stringify(list));
            loadAllCart();
        });
    });
}

async function loadAllCartMobile() {
    var list = JSON.parse(localStorage.getItem("product_cart"));
    var main = ''
    for (i = 0; i < list.length; i++) {
        main += `<tr>
        <td>
            <a href="detail?id=${list[i].product.id}&name=${list[i].product.alias}"><img class="imgprocmobile" src="${list[i].product.imageBanner}"></a>
        </td>
        <td class="tdinforcart">
            <a href="detail?id=${list[i].product.id}&name=${list[i].product.alias}" class="nameprocmobile">${list[i].product.name}</a>
            <p class="sizecmobile">${list[i].color.colorName} / ${list[i].size.sizeName}</p>
            <div class="clusinpmobile">
                <button onclick="upDownQuantity(${list[i].size.id},-1)" class="cartbtn"> - </button>
                <input value="${list[i].quantiy}" class="inputslcart">
                <button onclick="upDownQuantity(${list[i].size.id},1)" class="cartbtn"> + </button>
            </div>
        </td>
        <td class="tdinforcart">
            <i onclick="remove(${list[i].color.id})" class="fa fa-trash-o facartde cartdels"></i>
            <p class="boldcart pricecmobile">${formatmoney(list[i].quantiy * list[i].product.price)}&ThinSpace;</p>
        </td>
    </tr>`
    }
    document.getElementById("tablemobilecart").innerHTML = main
}

async function remove(id) {
    var list = JSON.parse(localStorage.getItem("product_cart"));
    var remainingArr = list.filter(data => data.color.id != id);
    window.localStorage.setItem('product_cart', JSON.stringify(remainingArr));
    toastr.success("Xóa sản phẩm thành công");
    loadAllCart();
}

function upDownQuantity(idsize, quantiy) {
    var list = JSON.parse(localStorage.getItem("product_cart"));
    for (i = 0; i < list.length; i++) {
        if (list[i].size.id == idsize) {
            list[i].quantiy = Number(list[i].quantiy) + Number(quantiy);
        }
    }
    window.localStorage.setItem('product_cart', JSON.stringify(list));

    var list = JSON.parse(localStorage.getItem("product_cart"));
    var remainingArr = list.filter(data => data.quantiy != 0);
    window.localStorage.setItem('product_cart', JSON.stringify(remainingArr));
    loadAllCart();
}

async function productLqCart() {
    var listcart = localStorage.getItem("product_cart");
    if (listcart == null) {
        return;
    }
    var list = JSON.parse(localStorage.getItem("product_cart"));
    var listCategory = [];
    for (i = 0; i < list.length; i++) {
        var listcate = list[i].product.productCategories;
        for (j = 0; j < listcate.length; j++) {
            listCategory.push(listcate[j].category.id);
        }
    }
    console.log(listCategory)
    var payload = {
        "listIdCategory":listCategory
    }
    var url = 'http://localhost:8080/api/product/public/searchFull?page=0&size=6';
    const res = await fetch(url, {
        method: 'POST',
        headers: new Headers({ 'Content-Type': 'application/json' }),
        body: JSON.stringify(payload)
    });
    var resultLq = await res.json();

    var list = resultLq.content
    var main = ''
    var licart = JSON.parse(listcart);
    for (i = 0; i < list.length; i++) {
        var choses = false;
        for (j = 0; j < licart.length; j++) {
            if (licart[j].product.id == list[i].id) {
                choses = true;
            }
        }
        if (choses == true) {
            continue;
        }

        var listimg = ''
        for (j = 0; j < list[i].productImages.length; j++) {
            listimg += `<div class="divimgsmpro"><img class="imgsmpro" src="${list[i].productImages[j].linkImage}"></div>`
        }
        main += `<div class="col-lg-3 col-md-3 col-sm-6 col-6">
                <a href="detail?id=${list[i].id}&name=${list[i].alias}" class="linkpro">
                    <div class="singlepro">
                        <div class="productsold"><span class="reviewsp">Đã bán: ${list[i].quantitySold}</span></div>
                        <img src="${list[i].imageBanner}" class="imgpro">
                        <span class="proname">${list[i].name}</span>
                        <span class="proprice">${formatmoney(list[i].price)}</span>
                        <div class="listimgpro">${listimg}</div>
                    </div>
                </a>
            </div>`
    }
    document.getElementById("listproductgycart").innerHTML = main;
}