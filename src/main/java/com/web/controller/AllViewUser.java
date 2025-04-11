package com.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AllViewUser {

    @RequestMapping(value = {"/about"}, method = RequestMethod.GET)
    public String about(Model model) {
        return "user/about.html";
    }

    @RequestMapping(value = {"/account"}, method = RequestMethod.GET)
    public String account() {
        return "user/account.html";
    }

    @RequestMapping(value = {"/blog"}, method = RequestMethod.GET)
    public String blog() {
        return "user/blog.html";
    }

    @RequestMapping(value = {"/blogdetail"}, method = RequestMethod.GET)
    public String blogdetail() {
        return "user/blogdetail.html";
    }

    @RequestMapping(value = {"/cart"}, method = RequestMethod.GET)
    public String cart() {
        return "user/cart.html";
    }

    @RequestMapping(value = {"/checkout"}, method = RequestMethod.GET)
    public String checkout() {
        return "user/checkout.html";
    }

    @RequestMapping(value = {"/confirm"}, method = RequestMethod.GET)
    public String confirm() {
        return "user/confirm.html";
    }

    @RequestMapping(value = {"/detail"}, method = RequestMethod.GET)
    public String detail() {
        return "user/detail.html";
    }

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String home() {
        return "redirect:/index";
    }

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "user/index.html";
    }

    @RequestMapping(value = {"/forgot"}, method = RequestMethod.GET)
    public String forgot() {
        return "user/forgot.html";
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String login() {
        return "user/login.html";
    }

    @RequestMapping(value = {"/payment"}, method = RequestMethod.GET)
    public String payment() {
        return "user/payment.html";
    }

    @RequestMapping(value = {"/product"}, method = RequestMethod.GET)
    public String product() {
        return "user/product.html";
    }

    @RequestMapping(value = {"/regis"}, method = RequestMethod.GET)
    public String regis() {
        return "user/regis.html";
    }


}
