package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.common.ExcelHelper;
import com.ifmo.lessons.diploma.entity.Customer;
import com.ifmo.lessons.diploma.entity.CustomerProduct;
import com.ifmo.lessons.diploma.entity.Product;
import com.ifmo.lessons.diploma.form.CustomerProductExcel;
import com.ifmo.lessons.diploma.service.CustomerProductForm;
import com.ifmo.lessons.diploma.service.CustomerProductService;
import com.ifmo.lessons.diploma.service.CustomerService;
import com.ifmo.lessons.diploma.service.ProductService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        model.addAttribute("title", "Customer products");
        model.addAttribute("customer", customer);
        model.addAttribute("list", list);
        return "customer-product-list";
    }

    @GetMapping("/{id}/edit")
    public String editByCustomer(@PathVariable int id, Model model) {
        Customer customer = customerService.getById(id);
        List<CustomerProduct> list = service.getByCustomer(id);
        CustomerProductForm form = new CustomerProductForm();
        form.setList(list);
        model.addAttribute("title", "Customer products");
        model.addAttribute("customer", customer);
        model.addAttribute("form", form);
        return "customer-product-list-edit";
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

    @GetMapping(value = "/{id}/excel", produces = ExcelHelper.TYPE)
    public @ResponseBody byte[] excel(@PathVariable int id) {
        ExcelHelper eh = new ExcelHelper(getFields());
        List<CustomerProduct> list = service.getByCustomer(id);
        List<CustomerProductExcel> listExcel = new ArrayList<>();
        for (CustomerProduct cp : list) {
            listExcel.add(new CustomerProductExcel(cp));
        }
        try (Workbook wb = eh.getWb(listExcel); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            wb.write(os);
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Workbook wb = new XSSFWorkbook();
        Sheet sh = wb.createSheet("Customer-material");
        Row header = sh.createRow(0);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Customer");
        headerCell = header.createCell(1);
        headerCell.setCellValue("Product");
        headerCell = header.createCell(2);
        headerCell.setCellValue("Number");
        headerCell = header.createCell(3);
        headerCell.setCellValue("Price");

        List<CustomerProduct> list = service.getByCustomer(id);
        int rowCount = 0;
        for (CustomerProduct cp : list) {
            rowCount++;
            Row row = sh.createRow(rowCount);
            // Customer Id
            Cell cell = row.createCell(0);
            cell.setCellValue(cp.getCustomer().getId());
            // Product Id
            cell = row.createCell(1);
            cell.setCellValue(cp.getProduct().getId());
            //
            cell = row.createCell(2);
            cell.setCellValue(cp.getNumber());
            //
            cell = row.createCell(3);
            cell.setCellValue(cp.getPrice());
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            wb.write(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream is = new ByteArrayInputStream(stream.toByteArray());
        try {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    private Map<String, String> getFields() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("Customer ID", "customerId");
        fields.put("Customer Name", "customerName");
        fields.put("Product ID", "productId");
        fields.put("Product Name", "productName");
        fields.put("Number", "number");
        fields.put("Price", "price");
        return fields;
    }
}
