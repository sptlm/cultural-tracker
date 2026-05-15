package com.culturalnavigator.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = statusCode == null ? 500 : Integer.parseInt(statusCode.toString());
        model.addAttribute("message", HttpStatus.resolve(status) == null ? "Ошибка" : HttpStatus.valueOf(status).getReasonPhrase());
        if (status == 403) {
            return "error/403";
        }
        if (status == 400) {
            return "error/400";
        }
        if (status == 404) {
            return "error/404";
        }
        return "error/500";
    }
}
