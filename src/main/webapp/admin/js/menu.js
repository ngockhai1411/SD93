const exceptionCode = 417;
var token = localStorage.getItem("token");
$(document).ready(function() {
    function loadmenu() {
        var content =
            `<a class="nav-link" href="index">
                <div class="sb-nav-link-icon"><i class="fa fa-database iconmenu"></i></div>
                Tổng quan
            </a>
            <a class="nav-link" href="taikhoan">
                <div class="sb-nav-link-icon"><i class="fas fa-user-alt iconmenu"></i></div>
                Tài khoản
            </a>
            <a class="nav-link" href="danhmuc">
                <div class="sb-nav-link-icon"><i class="fas fa-table iconmenu"></i></div>
                Danh mục
            </a>
            <a class="nav-link" href="banner">
                <div class="sb-nav-link-icon"><i class="fas fa-image iconmenu"></i></div>
                Banner
            </a>
            <a class="nav-link" href="product">
                <div class="sb-nav-link-icon"><i class="fas fa-tshirt iconmenu"></i></div>
                Sản phẩm
            </a>
            <a class="nav-link" href="invoice">
                <div class="sb-nav-link-icon"><i class="fa fa-shopping-cart iconmenu"></i></div>
                Đơn hàng
            </a>
            <a class="nav-link" href="importproduct">
                <div class="sb-nav-link-icon"><i class="fas fa-file iconmenu"></i></div>
                Nhập hàng
            </a>
            <a class="nav-link" href="voucher">
                <div class="sb-nav-link-icon"><i class="fa fa-ticket-alt iconmenu"></i></div>
                Voucher
            </a>
            <a class="nav-link" href="blog">
                <div class="sb-nav-link-icon"><i class="fas fa-newspaper iconmenu"></i></div>
                Bài viết
            </a>
            <a class="nav-link" href="doanhthu">
                <div class="sb-nav-link-icon"><i class="fas fa-chart-bar iconmenu"></i></div>
                Doanh thu
            </a>
            <a class="nav-link" href="chat">
                <div class="sb-nav-link-icon"><i class="fas fa-chart-bar iconmenu"></i></div>
                Tin nhắn
            </a>
            <a onclick="dangXuat()" class="nav-link" href="#">
                <div class="sb-nav-link-icon"><i class="fas fa-sign-out-alt iconmenu"></i></div>
                Đăng xuất
            </a>
           `

        var menu =
            `<nav class="sb-sidenav accordion sb-sidenav-dark" id="sidenavAccordion">
        <div class="sb-sidenav-menu">
            <div class="nav">
                ${content}
            </div>
        </div>
    </nav>`
        document.getElementById("layoutSidenav_nav").innerHTML = menu
    }
});



function formatmoney(money) {
    const VND = new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
    });
    return VND.format(money);
}
