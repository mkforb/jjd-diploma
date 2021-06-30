package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.common.ExcelHelper;
import com.ifmo.lessons.diploma.entity.Parent;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by User on 28.06.2021.
 */
public abstract class AbstractController {

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

    protected boolean checkExcelFile(MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            addErrorMessage(redirectAttributes, "Пустой файл");
            return false;
        }
        if (!ExcelHelper.hasExcelFormat(file)) {
            addErrorMessage(redirectAttributes, "Неверный формат файла");
            return false;
        }
        return true;
    }

    protected void addSuccessMessage(RedirectAttributes redirectAttributes, String text) {
        String value = (String) redirectAttributes.getFlashAttributes().get("msgSuccess");
        if (value == null) {
            value = text;
        } else {
            value = value + " " + text;
        }
        redirectAttributes.addFlashAttribute("msgSuccess", value);
    }

    protected void addErrorMessage(RedirectAttributes redirectAttributes, String text) {
        String value = (String) redirectAttributes.getFlashAttributes().get("msgError");
        if (value == null) {
            value = text;
        } else {
            value = value + " " + text;
        }
        redirectAttributes.addFlashAttribute("msgError", value);
    }

    protected abstract String getTitle(Parent parent);
    protected abstract Map<String, String> getFields();
}
