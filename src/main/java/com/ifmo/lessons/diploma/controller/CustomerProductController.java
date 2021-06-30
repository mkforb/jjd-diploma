package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.common.ExcelHelper;
import com.ifmo.lessons.diploma.entity.Customer;
import com.ifmo.lessons.diploma.entity.CustomerProduct;
import com.ifmo.lessons.diploma.entity.Parent;
import com.ifmo.lessons.diploma.entity.Product;
import com.ifmo.lessons.diploma.form.CustomerProductExcel;
import com.ifmo.lessons.diploma.form.CustomerProductForm;
import com.ifmo.lessons.diploma.form.CustomerProductsForm;
import com.ifmo.lessons.diploma.service.CustomerProductService;
import com.ifmo.lessons.diploma.service.CustomerService;
import com.ifmo.lessons.diploma.service.ProductService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 20.06.2021.
 */
@Controller
@RequestMapping("/customer-product")
public class CustomerProductController extends AbstractController {
    private final CustomerProductService service;
    private final CustomerService customerService;
    private final ProductService productService;

    @Autowired
    public CustomerProductController(CustomerProductService service, CustomerService customerService, ProductService productService) {
        this.service = service;
        this.customerService = customerService;
        this.productService = productService;
    }

    @GetMapping("/{id}/list")
    public String getByCustomer(@PathVariable int id, Model model) {
        CustomerProductsForm form = new CustomerProductsForm();
        Customer customer = customerService.getById(id);
        form.setCustomer(customer);
        form.setList(service.getByCustomer(id));
        model.addAttribute("title", "Товары клиента " + customer.getName());
        model.addAttribute("form", form);
        return "customer-product-list";
    }

    @GetMapping("/{id}/list/edit")
    public String editByCustomer(@PathVariable int id, Model model) {
        CustomerProductsForm form = new CustomerProductsForm();
        Customer customer = customerService.getById(id);
        form.setCustomer(customer);
        form.setList(service.getByCustomer(id));
        model.addAttribute("title", "Товары клиента " + customer.getName());
        model.addAttribute("form", form);
        return "customer-product-list-edit";
    }

    @PostMapping("/{id}/list/save")
    public String save(@PathVariable int id, CustomerProductsForm form, RedirectAttributes redirectAttributes) {
        //form.getList().forEach(cp -> System.out.println(cp.getCustomer().getId() + " " + cp.getProduct().getId() + " " + cp.getNumber() + " " + cp.getPrice()));
        form.getList().forEach(cp -> {
            cp.getKey().setCustomerId(cp.getCustomer().getId());
            cp.getKey().setProductId(cp.getProduct().getId());
            service.save(cp);
        });
        addSuccessMessage(redirectAttributes, "Данные сохранены");
        return "redirect:/customer-product/" + id + "/list";
    }

    @GetMapping("/{id}/add")
    public String add(@PathVariable int id, Model model) {
        CustomerProductForm cpf = new CustomerProductForm();
        cpf.setProductEdit(true);
        cpf.getCustomerProduct().setCustomer(customerService.getById(id));
        cpf.setProducts(productService.getAll());
        model.addAttribute("title", "Добавить товар клиента " + cpf.getCustomerProduct().getCustomer().getName());
        model.addAttribute("customerProductForm", cpf);
        return "customer-product-add";
    }

    @GetMapping("/{cId}/edit/{pId}")
    public String edit(@PathVariable int cId, @PathVariable int pId, Model model) {
        CustomerProductForm cpf = new CustomerProductForm();
        cpf.setProductEdit(false);
        cpf.setCustomerProduct(service.getByPk(cId, pId));
        cpf.setProducts(productService.getAll());
        model.addAttribute("title", "Изменить товар клиента " + cpf.getCustomerProduct().getCustomer().getName());
        model.addAttribute("customerProductForm", cpf);
        return "customer-product-add";
    }

    @PostMapping("/{cId}/save/{pId}")
    public String save(@PathVariable int cId, @PathVariable int pId, @Valid CustomerProductForm customerProductForm, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            if (customerProductForm.getCustomerProduct().getProduct() == null) {
                customerProductForm.getCustomerProduct().setProduct(new Product());
            }
            customerProductForm.setProducts(productService.getAll());
            model.addAttribute("title", "Изменить товар клиента " + customerProductForm.getCustomerProduct().getCustomer().getName());
            return "customer-product-add";
        }
        if (customerProductForm.isProductEdit()) {
            try {
                CustomerProduct cp = service.getByPk(customerProductForm.getCustomerProduct().getCustomer().getId(), customerProductForm.getCustomerProduct().getProduct().getId());
                if (cp != null) {
                    ObjectError error = new FieldError("customerProductForm", "customerProduct.product", "Такой товар у клиента уже есть");
                    result.addError(error);
                    customerProductForm.setProducts(productService.getAll());
                    return "customer-product-add";
                    //addErrorMessage(redirectAttributes, "Такой товар у клиента уже есть");
                    //return "redirect:/customer-product/" + cId + "/list";
                }
            } catch (IllegalArgumentException e) {

            }
        }
        CustomerProduct customerProduct = customerProductForm.getCustomerProduct();
        customerProduct.getKey().setCustomerId(customerProduct.getCustomer().getId());
        customerProduct.getKey().setProductId(customerProduct.getProduct().getId());
        service.save(customerProduct);
        addSuccessMessage(redirectAttributes, "Материал клиента сохранен");
        return "redirect:/customer-product/" + cId + "/list";
    }

    @GetMapping("/{cId}/delete/{pId}")
    public String delete(@PathVariable int cId, @PathVariable int pId, RedirectAttributes redirectAttributes) {
        CustomerProduct customerProduct = null;
        try {
            customerProduct = service.getByPk(cId, pId);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            addErrorMessage(redirectAttributes, "Запись не найдена");
            return "redirect:/customer-product/" + cId + "/list";
        }
        service.delete(customerProduct);
        addSuccessMessage(redirectAttributes, "Запись удалена");
        return "redirect:/customer-product/" + cId + "/list";
    }

    @GetMapping(value = "/{id}/list/download", produces = ExcelHelper.TYPE)
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
        return null;
    }

    @PostMapping("/upload/{id}")
    public String upload(@PathVariable int id, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String redirectPath = "redirect:/customer-product/" + id + "/list";
        if (!checkExcelFile(file, redirectAttributes)) {
            return redirectPath;
        }
        ExcelHelper eh = new ExcelHelper(getFields());
        List<CustomerProductExcel> list = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook wb = WorkbookFactory.create(is)) {
            list = eh.getList(wb, CustomerProductExcel.class);
        } catch (IOException e) {
            e.printStackTrace();
            addErrorMessage(redirectAttributes, e.getMessage());
            return redirectPath;
        }
        for (CustomerProductExcel cpe : list) {
            // Проверить клиента
            try {
                Customer c = customerService.getById(cpe.getCustomerId());
            } catch (IllegalArgumentException e) {
                addErrorMessage(redirectAttributes, "В файле несуществующий ID клиента " + cpe.getCustomerId());
                return redirectPath;
            }
            // Проверить товар
            try {
                Product p = productService.getById(cpe.getProductId());
            } catch (IllegalArgumentException e) {
                addErrorMessage(redirectAttributes, "В файле несуществующий ID товара " + cpe.getProductId());
                return redirectPath;
            }
        }
        for (CustomerProductExcel cpe : list) {
            // Загрузка
            CustomerProduct cp = new CustomerProduct();
            cp.getKey().setCustomerId(cpe.getCustomerId());
            cp.getKey().setProductId(cpe.getProductId());
            cp.getCustomer().setId(cpe.getCustomerId());
            cp.getProduct().setId(cpe.getProductId());
            cp.setNumber(cpe.getNumber());
            cp.setPrice(cpe.getPrice());
            service.save(cp);
        }
        addSuccessMessage(redirectAttributes, "Файл загружен");
        return redirectPath;
    }

    @Override
    protected String getTitle(Parent parent) {
        return null;
    }

    @Override
    protected Map<String, String> getFields() {
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
