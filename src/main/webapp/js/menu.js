var token = localStorage.getItem("token");
const exceptionCode = 417;
var tokenFcm = "";
async function loadMenu() {
    var dn = `<span class="nav-item dropdown pointermenu gvs">
                <i class="fa fa-user" class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false"> Tài khoản</i>
                <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <li><a class="dropdown-item" href="account">Tài khoản</a></li>
                    <li onclick="logout()"><a class="dropdown-item" href="#">Đăng xuất</a></li>
                </ul>
            </span>`
    if (token == null) {
        dn = `<a href="login" class="pointermenu gvs"><i class="fa fa-user"> Đăng ký/ Đăng nhập</i></a>`
        if(document.getElementById("btnchatbottom")){
            document.getElementById("btnchatbottom").style.display = 'none'
        }
    }
    var menu =
        `
    <nav class="navbar navbar-expand-lg">
        <div class="container-fluid">
            <a class="navbar-brand d-none d-lg-block" href="/">
                <img style="width: 140px;" src="image/logo.png">
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <a class="navbar-brand navbar-toggler" href="index"><img style="width: 70px;" src="image/logo.png"></a>
            <span>
                <i data-bs-toggle="modal" data-bs-target="#modalsearch" class="fa fa-search navbar-toggler"></i>
                <i class="fa fa-shopping-bag navbar-toggler"> <span id="slcartmenusm" class="slcartmenusm">0</span></i>
            </span>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0" id="mainmenut">
<!--                <li class="nav-item"><a class="nav-link menulink" href="index"><img style="width: 140px;" src="image/logo.png"></a></li>-->
                <li class="nav-item"><a class="nav-link menulink" href="about" style="font-size: 20px">Giới Thiệu</a></li>
                <li class="nav-item"><a class="nav-link menulink" href="blog" style="font-size: 20px">Tin Tức</a></li>
            </ul>
            <div class="d-flex">
                <a href="#" data-bs-toggle="modal" data-bs-target="#modalsearch" class="pointermenu gvs"><i class="fa fa-search"></i></a>
                ${dn}
                <a href="cart" class="pointermenu"><i class="fa fa-shopping-bag"><span class="slcartmenu" id="slcartmenu">0</span> Giỏ hàng</i></a>
            </div>
        </div>
    </nav>`
    document.getElementById("menu").innerHTML = menu
    loadCategoryMenu();
    loadCartMenu();
    try { loadFooter(); } catch (error) {}
}


async function loadCategoryMenu() {
    var url = 'http://localhost:8080/api/category/public/findPrimaryCategory';
    const response = await fetch(url, {});
    var list = await response.json();
    var main = ''
    for (i = 0; i < list.length; i++) {
        main += `<li class="nav-item dropdown ddtog">
        <a class="nav-link menulink ddtog" href="#" id="cate1" role="button" data-bs-toggle="dropdown" aria-expanded="false" style="font-size: 20px">${list[i].name}</a>
        <ul class="dropdown-menu" aria-labelledby="cate1">`
        var listChild = list[i].categories;
        var mainChild = ''
        for (j = 0; j < listChild.length; j++) {
            mainChild += `<li><a class="dropdown-item" href="product?category=${listChild[j].id}" style="font-size: 13px">${listChild[j].name}</a></li>`
        }
        main += mainChild
        main += ` </ul></li>`
    }
    document.getElementById("mainmenut").innerHTML += main;
}

async function searchMenu() {
    var texts = document.getElementById("inputsearchmenu").value
    document.getElementById("listproductsearch").style.display = 'block'
    if (texts.length == 0) {
        document.getElementById("listproductsearch").style.display = 'none'
        return;
    }
    var url = 'http://localhost:8080/api/product/public/findByParam?page=0&size=50&q=' + texts;
    const response = await fetch(url, {});
    var result = await response.json();
    var list = result.content;
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<a href="detail?id=${list[i].id}&name=${list[i].alias}" class="tenspsearch"><div class="singlesearch col-md-12">
                    <div class="row">
                        <div class="col-2"><img class="imgprosearch" src="${list[i].imageBanner}"></div>
                        <div class="col-10">
                            <span class="tenspsearch">${list[i].name}</span><br>
                            <span class="tenspsearch">${formatmoney(list[i].price)}</span>
                        </div>
                    </div>
                </div></a>`
    }
    document.getElementById("listproductmn").innerHTML = main;
}
async function searchMenuMobile() {
    var texts = document.getElementById("inputsearchmobile").value
    if (texts.length == 0) {
        document.getElementById("listproductsearchmobile").innerHTML = '';
        return;
    }
    var url = 'http://localhost:8080/api/product/public/findByParam?page=0&size=50&q=' + texts;
    const response = await fetch(url, {});
    var result = await response.json();
    var list = result.content;
    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<div class="singlesearch col-md-12">
                    <div class="p40"><a href="detail?id=${list[i].id}&name=${list[i].alias}"><img class="imgprosearchp" src="${list[i].imageBanner}"></a></div>
                    <div class="p60">
                        <a href="detail?id=${list[i].id}&name=${list[i].alias}"><span class="tenspsearch">${list[i].name}</span><br>
                        <span class="tenspsearch">${formatmoney(list[i].price)}</span></a>
                    </div>
                </div>`
    }
    document.getElementById("listproductsearchmobile").innerHTML = main;
}

function loadFooter() {
    var foo = `<footer class="text-center text-lg-start text-muted">
    <section class="d-flex justify-content-center justify-content-lg-between p-4 border-bottom">
        <div class="me-5 d-none d-lg-block"><span>Theo dõi chúng tôi tại:</span></div>
        <div>
        <a href="" class="me-4 text-reset"><i class="fab fa-facebook-f"></i></a>
        <a href="" class="me-4 text-reset"><i class="fab fa-twitter"></i></a>
        <a href="" class="me-4 text-reset"><i class="fab fa-google"></i></a>
        <a href="" class="me-4 text-reset"><i class="fab fa-instagram"></i></a>
        <a href="" class="me-4 text-reset"><i class="fab fa-linkedin"></i></a>
        <a href="" class="me-4 text-reset"><i class="fab fa-github"></i></a>
        </div>
    </section>
    <section class="">
        <div class=" text-center text-md-start mt-5">
        <div class="row mt-3">
            <div class="col-md-3 col-lg-4 col-xl-3 mx-auto mb-4">
            <h6 class="text-uppercase fw-bold mb-4"><i class="fas fa-shoe-prints me-3"></i>
Sneaker Easy-Shop</h6>
            <p style="font-family: bold; font-size: 18px">
                Chúng tôi tự hào là đơn vị cung cấp các sản phẩm giày thời trang chất lượng cao với mức giá phải chăng, phù hợp cho cả nam, nữ và giới trẻ hiện đại. Với sự đa dạng về mẫu mã, kiểu dáng và màu sắc, bộ sưu tập của chúng tôi luôn được cập nhật để bắt kịp xu hướng thời trang mới nhất. Dù bạn yêu thích phong cách năng động, cá tính hay thanh lịch, tinh tế, chúng tôi đều có những lựa chọn hoàn hảo dành riêng cho bạn. Hãy để chúng tôi đồng hành cùng bạn trong hành trình thể hiện cá tính và phong cách riêng, giúp bạn tự tin tỏa sáng ở bất kỳ nơi đâu!
            </p>
            </div>
            <div class="col-md-2 col-lg-2 col-xl-2 mx-auto mb-4">
            <h6 class="text-uppercase fw-bold mb-4">Sản phẩm</h6>
            <p><a href="#!" class="text-reset" style="font-family: bold; font-size: 18px">Uy tín</a></p>
            <p><a href="#!" class="text-reset" style="font-family: bold; font-size: 18px">Chất lượng</a></p>
            <p><a href="#!" class="text-reset" style="font-family: bold; font-size: 18px">Nguồn gốc rõ ràng</a></p>
            <p><a href="#!" class="text-reset" style="font-family: bold; font-size: 18px">Giá rẻ</a></p>
            </div>
            <div class="col-md-3 col-lg-2 col-xl-2 mx-auto mb-4">
            <h6 class="text-uppercase fw-bold mb-4" >Dịch vụ</h6>
            <p><a href="#!" class="text-reset" style="font-family: bold; font-size: 18px">24/7</a></p>
            <p><a href="#!" class="text-reset" style="font-family: bold; font-size: 18px">Bảo hành 6 tháng</a></p>
            <p><a href="#!" class="text-reset" style="font-family: bold; font-size: 18px">Free ship</a></p>
            <p><a href="#!" class="text-reset" style="font-family: bold; font-size: 18px">Lỗi 1 đổi 1</a></p>
            </div>
            <div class="col-md-4 col-lg-3 col-xl-3 mx-auto mb-md-0 mb-4">
            <h6 class="text-uppercase fw-bold mb-4">Liên hệ</h6>
            <p style="font-family: bold; font-size: 18px"><i class="fas fa-home me-3" ></i>Trịnh Văn Bô, Hà nội, Việt Nam</p>
            <p style="font-family: bold; font-size: 18px"><i class="fas fa-envelope me-3" ></i> easyshop@gmail.com</p>
            <p style="font-family: bold; font-size: 18px"><i class="fas fa-phone me-3" ></i> + 84 234 567 88</p>
            <p style="font-family: bold; font-size: 18px"><i class="fas fa-print me-3"></i> + 84 234 567 89</p>
            </div>
        </div>
        </div>
    </section>
    </footer>`
    foo +=
        `<div class="modal fade" id="modalsearch" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen-xxl-down modelreplay">
        <div class="modal-content">
            <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLabel" style="display: block;text-align: center !important;">Tìm kiếm</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="searchmenu searchsm">
                    <input id="inputsearchmobile" onkeyup="searchMenuMobile()" class="imputsearchmenu" placeholder="Tìm kiếm sản phẩm...">
                    <button class="btnsearchmenu"><i class="fa fa-search"></i></button>
                </div>

                <div id="listproductsearchmobile" class="row">
                </div>
            </div>
        </div>
        </div>
    </div>`
    document.getElementById("footer").innerHTML = foo;
    try {
        loadMyChat();
    }
    catch (e){

    }
}

async function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.href = '/logout'
}


function formatmoney(money) {
    const VND = new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
    });
    return VND.format(money);
}

async function loadCartMenu() {
    var listcart = localStorage.getItem("product_cart");
    if (listcart == null) {
        return;
    }
    var list = JSON.parse(localStorage.getItem("product_cart"));
    document.getElementById("slcartmenusm").innerHTML = list.length
    document.getElementById("slcartmenu").innerHTML = list.length
}




var stompClient = null;

$( document ).ready(function() {
    var user = localStorage.getItem("user");
    if(user != null){
        user = JSON.parse(user)
        var username = user.username;
        connect(username);
    }
});

function connect(username) {
    var socket = new SockJS('/hello');
    stompClient = Stomp.over(socket);
    stompClient.connect({ username: username, }, function() {
        console.log('Web Socket is connected');
        stompClient.subscribe('/users/queue/messages', function(message) {
            // var Idsender = message.headers.sender
            appendRecivers(message.body)
        });

    });
}


$( document ).ready(function() {
    $("#sendmess").click(function() {
        stompClient.send("/app/hello/-10", {}, $("#contentmess").val());
        append()
    });
    $('#contentmess').keypress(function (e) {
        var key = e.which;
        if(key == 13)  // the enter key code
        {
            stompClient.send("/app/hello/-10", {}, $("#contentmess").val());
            append()
        }
    });
});

// nối vào đoạn chat ngay sau khi gửi
function append() {
    var tinhan = `<p class="mychat">${$("#contentmess").val()}</p>`
    document.getElementById('listchat').innerHTML += tinhan;
    var scroll_to_bottom = document.getElementById('scroll-to-bottom');
    scroll_to_bottom.scrollTop = scroll_to_bottom.scrollHeight;
    document.getElementById("contentmess").value = ''
}

function appendRecivers (message) {
    var cont = `<p class="adminchat">${message}</p>`
    document.getElementById('listchat').innerHTML += cont;
    var scroll_to_bottom = document.getElementById('scroll-to-bottom');
    scroll_to_bottom.scrollTop = scroll_to_bottom.scrollHeight;
}


async function loadMyChat() {
    var url = 'http://localhost:8080/api/chat/user/my-chat';
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await response.json();
    var main = '';
    for (i = 0; i < list.length; i++) {
        if(list[i].sender.authorities.name == "ROLE_USER"){
            main += `<p class="mychat">${list[i].content}</p>`
        }
        else{
            main += `<p class="adminchat">${list[i].content}</p>`
        }
    }
    document.getElementById("listchat").innerHTML = main
}

function toggleChat() {
    var chatBox = document.getElementById("chat-box");
    var btnopenchat = document.getElementById("btnopenchat");
    if (chatBox.style.display === "none" || chatBox.style.display === "") {
        chatBox.style.display = "block";
        chatBox.style.bottom = "20px";
        btnopenchat.style.display = 'none'
    }
    else {
        chatBox.style.display = "none";
        btnopenchat.style.display = ''
    }
}