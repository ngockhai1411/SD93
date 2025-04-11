package com.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/employee")
public class AllViewEmployee {

    @RequestMapping(value = {"/invoice"}, method = RequestMethod.GET)
    public String invoice() {
        return "employee/invoice.html";
    }

    @RequestMapping(value = {"/create-invoice"}, method = RequestMethod.GET)
    public String createInvoice() {
        return "employee/createinvoice.html";
    }

    @RequestMapping(value = {"/chat"}, method = RequestMethod.GET)
    public String chat() {
        return "employee/chat.html";
    }

    @RequestMapping(value = {"/danhmuc"}, method = RequestMethod.GET)
    public String danhmuc() {
        return "employee/danhmuc.html";
    }

    @RequestMapping(value = {"/banner"}, method = RequestMethod.GET)
    public String banner() {
        return "employee/banner.html";
    }

    @RequestMapping(value = {"/product"}, method = RequestMethod.GET)
    public String product() {
        return "employee/product.html";
    }

    @RequestMapping(value = {"/trademark"}, method = RequestMethod.GET)
    public String trademark() {
        return "employee/trademark.html";
    }

    @RequestMapping(value = {"/sole"}, method = RequestMethod.GET)
    public String sole() {
        return "employee/sole.html";
    }

    @RequestMapping(value = {"/material"}, method = RequestMethod.GET)
    public String material() {
        return "employee/material.html";
    }

    @RequestMapping(value = {"/in-don"}, method = RequestMethod.GET)
    public String Indon() {
        return "employee/indon.html";
    }

    @RequestMapping(value = {"/addcategory"}, method = RequestMethod.GET)
    public String addcategory() {
        return "employee/addcategory.html";
    }
}
