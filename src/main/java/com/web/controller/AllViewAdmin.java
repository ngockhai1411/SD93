package com.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class AllViewAdmin {


    @RequestMapping(value = {"/addblog"}, method = RequestMethod.GET)
    public String addblog() {
        return "admin/addblog.html";
    }

    @RequestMapping(value = {"/addcategory"}, method = RequestMethod.GET)
    public String addcategory() {
        return "admin/addcategory.html";
    }

    @RequestMapping(value = {"/addimportproduct"}, method = RequestMethod.GET)
    public String addimportproduct() {
        return "admin/addimportproduct.html";
    }

    @RequestMapping(value = {"/addproduct"}, method = RequestMethod.GET)
    public String addproduct() {
        return "admin/addproduct.html";
    }

    @RequestMapping(value = {"/addvoucher"}, method = RequestMethod.GET)
    public String addvoucher() {
        return "admin/addvoucher.html";
    }

    @RequestMapping(value = {"/blog"}, method = RequestMethod.GET)
    public String blog() {
        return "admin/blog.html";
    }

    @RequestMapping(value = {"/danhmuc"}, method = RequestMethod.GET)
    public String danhmuc() {
        return "admin/danhmuc.html";
    }

    @RequestMapping(value = {"/banner"}, method = RequestMethod.GET)
    public String banner() {
        return "admin/banner.html";
    }

    @RequestMapping(value = {"/chat"}, method = RequestMethod.GET)
    public String chat() {
        return "admin/chat.html";
    }

    @RequestMapping(value = {"/doanhthu"}, method = RequestMethod.GET)
    public String doanhthu() {
        return "admin/doanhthu.html";
    }

    @RequestMapping(value = {"/importproduct"}, method = RequestMethod.GET)
    public String importproduct() {
        return "admin/importproduct.html";
    }

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "admin/index.html";
    }

    @RequestMapping(value = {"/invoice"}, method = RequestMethod.GET)
    public String invoice() {
        return "admin/invoice.html";
    }

    @RequestMapping(value = {"/create-invoice"}, method = RequestMethod.GET)
    public String createInvoice() {
        return "admin/createinvoice.html";
    }

    @RequestMapping(value = {"/product"}, method = RequestMethod.GET)
    public String product() {
        return "admin/product.html";
    }

    @RequestMapping(value = {"/taikhoan"}, method = RequestMethod.GET)
    public String taikhoan() {
        return "admin/taikhoan.html";
    }

    @RequestMapping(value = {"/voucher"}, method = RequestMethod.GET)
    public String voucher() {
        return "admin/voucher.html";
    }

    @RequestMapping(value = {"/trademark"}, method = RequestMethod.GET)
    public String trademark() {
        return "admin/trademark.html";
    }

    @RequestMapping(value = {"/sole"}, method = RequestMethod.GET)
    public String sole() {
        return "admin/sole.html";
    }

    @RequestMapping(value = {"/material"}, method = RequestMethod.GET)
    public String material() {
        return "admin/material.html";
    }

    @RequestMapping(value = {"/in-don"}, method = RequestMethod.GET)
    public String Indon() {
        return "admin/indon.html";
    }
}
