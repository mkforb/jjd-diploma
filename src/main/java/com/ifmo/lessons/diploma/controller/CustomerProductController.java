package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.entity.Customer;
import com.ifmo.lessons.diploma.entity.CustomerProduct;
import com.ifmo.lessons.diploma.entity.Product;
import com.ifmo.lessons.diploma.service.CustomerProductForm;
import com.ifmo.lessons.diploma.service.CustomerProductService;
import com.ifmo.lessons.diploma.service.CustomerService;
import com.ifmo.lessons.diploma.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by User on 20.06.2021.
 */
@Controller
@RequestMapping("/customer-product")
public class CustomerProductController {
    private final CustomerProductService service;
    private final CustomerService customerService;

    @Autowired
    public CustomerProductController(CustomerProductService service, CustomerService customerService) {
        this.service = service;
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public String getByCustomer(@PathVariable int id, Model model) {
        // Test
        List<CustomerProduct> list1 = service.getByCustomer(1);
        if (list1.isEmpty()) {
            CustomerProduct customerProduct = new CustomerProduct();
            customerProduct.getCustomer().setId(1);
            customerProduct.getProduct().setId(1);
            customerProduct.setNumber("PROD1-AA");
            customerProduct.setPrice(140);
            service.save(customerProduct);

            customerProduct = new CustomerProduct();
            customerProduct.getCustomer().setId(1);
            customerProduct.getProduct().setId(2);
            customerProduct.setNumber("PROD2-AA");
            customerProduct.setPrice(69);
            service.save(customerProduct);
        }

        Customer customer = customerService.getById(id);
        List<CustomerProduct> list = service.getByCustomer(id);
        CustomerProductForm form = new CustomerProductForm();
        form.setList(list);
        model.addAttribute("title", "Customer products");
        model.addAttribute("customer", customer);
        model.addAttribute("form", form);
        return "customer-product";
    }

    @PostMapping("/{id}/save")
    public String save(@PathVariable int id, CustomerProductForm form, Model model) {
        form.getList().forEach(cp -> System.out.println(cp.getCustomer().getId() + " " + cp.getProduct().getId() + " " + cp.getNumber() + " " + cp.getPrice()));
        form.getList().forEach(cp -> {
            cp.getKey().setCustomerId(cp.getCustomer().getId());
            cp.getKey().setProductId(cp.getProduct().getId());
            service.save(cp);
        });
        return "redirect:/customer-product/" + id;
    }

    @GetMapping("/{id}/add")
    public String add(@PathVariable int id, Model model) {
        CustomerProduct cp = new CustomerProduct();
        cp.getCustomer().setId(id);
        //cp.setCustomer(customerService.getById(id));
        model.addAttribute("title", "Add customer product");
        model.addAttribute("cp", cp);
        return "customer-product-add";
    }

    @GetMapping("/{cId}/edit/{pId}")
    public String edit(@PathVariable int cId, @PathVariable int pId, Model model) {
        CustomerProduct cp = service.getByPk(cId, pId);
        System.out.println(cp.getKey().getCustomerId());
        model.addAttribute("title", "Edit customer product");
        model.addAttribute("cp", cp);
        return "customer-product-add";
    }

    @PostMapping("/{cId}/save/{pId}")
    public String save(@PathVariable int cId, @PathVariable int pId, CustomerProduct cp, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Edit");
            model.addAttribute("cp", cp);
            return "customer-product-add";
        }
        //System.out.println(cp.getCustomer().getId() + " " + cp.getProduct().getId());
        cp.getKey().setCustomerId(cId);
        cp.getKey().setProductId(pId);
        service.save(cp);
        //CustomerProduct cp1 = service.getByPk(cId, pId);
        //cp1.setNumber(cp.getNumber());
        //cp1.setPrice(cp.getPrice());
        //service.save(cp1);
        return "redirect:/customer-product/" + cId;
    }
}
