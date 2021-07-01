package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.common.ExcelHelper;
import com.ifmo.lessons.diploma.common.Unit;
import com.ifmo.lessons.diploma.entity.Parent;
import com.ifmo.lessons.diploma.entity.Product;
import com.ifmo.lessons.diploma.service.ProductService;
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
 * Created by User on 14.06.2021.
 */
@Controller
@RequestMapping("/product")
public class ProductController extends AbstractController {
    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public String list(Model model) {
        List<Product> products = service.getAll();
        model.addAttribute("title", "Товары");
        model.addAttribute("products", products);
        return "product-list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        Product product = new Product();
        model.addAttribute("title", getTitle(product));
        model.addAttribute("product", product);
        model.addAttribute("uoms", Unit.values());
        return "product-add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Product product = null;
        try {
            product = service.getById(id);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            addErrorMessage(redirectAttributes, "Товар не найден");
            return "redirect:/product/list";
        }
        model.addAttribute("title", getTitle(product));
        model.addAttribute("product", product);
        model.addAttribute("uoms", Unit.values());
        return "product-add";
    }

    @PostMapping("/save")
    public String save(@Valid Product product, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", getTitle(product));
            model.addAttribute("uoms", Unit.values());
            return "product-add";
        }
        service.save(product);
        addSuccessMessage(redirectAttributes, "Товар сохранен");
        return "redirect:/product/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        Product product = null;
        try {
            product = service.getById(id);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            addErrorMessage(redirectAttributes, "Товар не найден");
            return "redirect:/product/list";
        }
        try {
            service.delete(product);
        } catch (IllegalArgumentException e) {
            addErrorMessage(redirectAttributes, e.getMessage());
            return "redirect:/product/list";
        }
        addSuccessMessage(redirectAttributes, "Товар удален");
        return "redirect:/product/list";
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
            return "redirect:/product/list";
        }
        ExcelHelper eh = new ExcelHelper(getFields());
        try (InputStream is = file.getInputStream(); Workbook wb = WorkbookFactory.create(is)) {
            List<Product> list = eh.getList(wb, Product.class);
            for (Product product : list) {
                Product fromDb = service.getBySku(product.getSku());
                if (fromDb != null) {
                    product.setId(fromDb.getId());
                }
                service.save(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addSuccessMessage(redirectAttributes, "Файл загружен");
        return "redirect:/product/list";
    }

    @Override
    protected String getTitle(Parent parent) {
        if (parent.getId() == 0) {
            return "Добавить товар";
        }
        return "Изменить товар";
    }

    @Override
    protected Map<String, String> getFields() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("Артикул", "sku");
        fields.put("Название", "name");
        fields.put("Цена", "price");
        fields.put("Запас", "stock");
        fields.put("ЕИ", "uom");
        return fields;
    }
}
