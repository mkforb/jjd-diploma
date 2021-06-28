package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.entity.Customer;
import com.ifmo.lessons.diploma.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by User on 15.06.2021.
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService service;

    @Autowired
    public CustomerController(CustomerService service) {
        this.service = service;

        // Test
        Customer customer1 = new Customer();
        customer1.setName("ООО Продукт Трейд");
        customer1.setAddress("СПб, ул. Тверская, 11");
        service.save(customer1);
    }

    @GetMapping("/list")
    public String list(Model model) {
        List<Customer> customers = service.getAll();
        model.addAttribute("title", "Клиенты");
        model.addAttribute("customers", customers);
        return "customer-list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        Customer customer = new Customer();
        model.addAttribute("title", getTitle(customer));
        model.addAttribute("customer", customer);
        return "customer-add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        Customer customer = null;
        try {
            customer = service.getById(id);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        model.addAttribute("title", getTitle(customer));
        model.addAttribute("customer", customer);
        return "customer-add";
    }

    @PostMapping("/save")
    public String save(@Valid Customer customer, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("title", getTitle(customer));
            return "customer-add";
        }
        service.save(customer);
        return "redirect:/customer/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        Customer customer = service.getById(id);
        service.delete(customer);
        return "redirect:/customer/list";
    }

    private String getTitle(Customer customer) {
        if (customer.getId() == 0) {
            return "Добавить клиента";
        }
        return "Изменить клиента";
    }
}
