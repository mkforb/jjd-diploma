package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.common.ExcelHelper;
import com.ifmo.lessons.diploma.entity.Parent;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by User on 28.06.2021.
 */
public abstract class AbstractController {

    @GetMapping(value = "/list/template", produces = ExcelHelper.TYPE)
    public @ResponseBody
    byte[] template() {
        ExcelHelper eh = new ExcelHelper(getFields());
        try (Workbook wb = eh.getTemplate(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            wb.write(os);
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract String getTitle(Parent parent);
    protected abstract Map<String, String> getFields();
}
