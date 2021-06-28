package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.common.ExcelHelper;
import com.ifmo.lessons.diploma.common.Unit;
import com.ifmo.lessons.diploma.entity.Product;
import com.ifmo.lessons.diploma.service.ProductService;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by User on 14.06.2021.
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;

        // Test
        Product product1 = new Product();
        product1.setName("Prod 1");
        product1.setSku("PROD-001");
        product1.setPrice(105);
        product1.setStock(8);
        product1.setUom("ШТ");
        service.save(product1);

        product1 = new Product();
        product1.setName("Prod 2");
        product1.setSku("PROD-002");
        product1.setPrice(59);
        product1.setStock(0);
        product1.setUom("ШТ");
        service.save(product1);
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
        model.addAttribute("title", "Добавить товар");
        model.addAttribute("product", new Product());
        model.addAttribute("uoms", Unit.values());
        return "product-add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        Product product = service.getById(id);
        model.addAttribute("title", "Изменить товар");
        model.addAttribute("product", product);
        model.addAttribute("uoms", Unit.values());
        return "product-add";
    }

    @PostMapping("/save")
    public String save(@Valid Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("uoms", Unit.values());
            return "product-add";
        }
        service.save(product);
        return "redirect:/product/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        Product product = service.getById(id);
        service.delete(product);
        return "redirect:/product/list";
    }

    @GetMapping(value = "/list/template", produces = ExcelHelper.TYPE)
    public @ResponseBody byte[] template() {
        ExcelHelper eh = new ExcelHelper(getFields());
        try (Workbook wb = eh.getTemplate(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            wb.write(os);
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping(value = "/list/download", produces = ExcelHelper.TYPE)
    public @ResponseBody byte[] download() {
        ExcelHelper eh = new ExcelHelper(getFields());
        /*String[] fields = {"SKU", "Name", "Price", "Stock", "UoM"};
        Workbook wb = new XSSFWorkbook();
        Sheet sh = wb.createSheet();
        Row row = sh.createRow(0);
        for (int i = 0; i < fields.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(fields[i]);
        }
        List<Product> list = service.getAll();
        int i = 0;
        for (Product pr : list) {
            i++;
            row = sh.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(pr.getSku());
            cell = row.createCell(1);
            cell.setCellValue(pr.getName());
            cell = row.createCell(2);
            cell.setCellValue(pr.getPrice());
            cell = row.createCell(3);
            cell.setCellValue(pr.getStock());
            cell = row.createCell(4);
            cell.setCellValue(pr.getUom());
        }*/
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
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("MESSAGE", "Пустой файл");
            return "redirect:/product/list";
        }
        if (!ExcelHelper.hasExcelFormat(file)) {
            redirectAttributes.addFlashAttribute("MESSAGE", "Неверный формат файла");
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
        redirectAttributes.addFlashAttribute("MESSAGE", "Файл загружен");
        return "redirect:/product/list";
    }

    private Map<String, String> getFields() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("SKU", "sku");
        fields.put("Name", "name");
        fields.put("Price", "price");
        fields.put("Stock", "stock");
        fields.put("UoM", "uom");
        return fields;
    }
}
