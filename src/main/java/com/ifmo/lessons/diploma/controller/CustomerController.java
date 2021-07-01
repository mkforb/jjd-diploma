package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.common.ExcelHelper;
import com.ifmo.lessons.diploma.entity.Customer;
import com.ifmo.lessons.diploma.entity.Parent;
import com.ifmo.lessons.diploma.service.CustomerService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 15.06.2021.
 */
@Controller
@RequestMapping("/customer")
public class CustomerController extends AbstractController {
    private final CustomerService service;

    @Autowired
    public CustomerController(CustomerService service) {
        this.service = service;
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
            return "redirect:/customer/list";
        }
        model.addAttribute("title", getTitle(customer));
        model.addAttribute("customer", customer);
        return "customer-add";
    }

    @PostMapping("/save")
    public String save(@Valid Customer customer, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", getTitle(customer));
            return "customer-add";
        }
        service.save(customer);
        addSuccessMessage(redirectAttributes, "Клиент сохранен");
        return "redirect:/customer/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        Customer customer = null;
        try {
            customer = service.getById(id);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            addErrorMessage(redirectAttributes, e.getMessage());
            return "redirect:/customer/list";
        }
        try {
            service.delete(customer);
        } catch (IllegalArgumentException e) {
            addErrorMessage(redirectAttributes, e.getMessage());
            return "redirect:/customer/list";
        }
        addSuccessMessage(redirectAttributes, "Клиент удален");
        return "redirect:/customer/list";
    }

    @GetMapping(value = "/list/download", produces = ExcelHelper.TYPE)
    public @ResponseBody byte[] download() {
        ExcelHelper eh = new ExcelHelper(getFields());
        try (Workbook wb = eh.getWb(service.getAll()); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            wb.write(os);
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (!checkExcelFile(file, redirectAttributes)) {
            return "redirect:/customer/list";
        }
        ExcelHelper eh = new ExcelHelper(getFields());
        try (InputStream is = file.getInputStream(); Workbook wb = WorkbookFactory.create(is)) {
            List<Customer> list = eh.getList(wb, Customer.class);
            for (Customer customer : list) {
                Customer fromDb = service.getByName(customer.getName());
                if (fromDb != null) {
                    customer.setId(fromDb.getId());
                }
                service.save(customer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addSuccessMessage(redirectAttributes, "Файл загружен");
        return "redirect:/customer/list";
    }

    @Override
    protected String getTitle(Parent parent) {
        if (parent.getId() == 0) {
            return "Добавить клиента";
        }
        return "Изменить клиента";
    }

    @Override
    protected Map<String, String> getFields() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("Название", "name");
        fields.put("Адрес", "address");
        return fields;
    }
}
