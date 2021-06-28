package com.ifmo.lessons.diploma.common;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 27.06.2021.
 */
public class ExcelHelper {
    public static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private final Map<String, String> fields;

    public ExcelHelper(Map<String, String> fields) {
        this.fields = fields;
    }

    public Workbook getTemplate() {
        Workbook wb = new XSSFWorkbook();
        Sheet sh = wb.createSheet();
        Row row = sh.createRow(0);
        int i = 0;
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            Cell cell = row.createCell(i);
            cell.setCellValue(entry.getKey());
            i++;
        }
        return wb;
    }

    public <OT> Workbook getWb(List<OT> list) {
        Workbook wb = getTemplate();
        Sheet sh = wb.getSheetAt(0);
        int rowNum = 0;
        for (OT o : list) {
            rowNum++;
            Row row = sh.createRow(rowNum);
            int i = 0;
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                Cell cell = row.createCell(i);
                String getMethodName = "get" + capitalize(entry.getValue());
                try {
                    Method getMethod = o.getClass().getDeclaredMethod(getMethodName);
                    try {
                        Object getResult = getMethod.invoke(o);
                        switch (getResult.getClass().getName()) {
                            case "java.lang.String":
                                cell.setCellValue((String) getResult);
                                break;
                            case "java.lang.Integer":
                                cell.setCellValue((Integer) getResult);
                                break;
                            case "java.lang.Double":
                                cell.setCellValue((double) getResult);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
        return wb;
    }

    public <OT> List<OT> getList(Workbook wb, Class<?> cl) {
        List<OT> list = new ArrayList<>();
        Sheet sh = wb.getSheetAt(0);
        List<String> listField = new ArrayList<>();
        sh.rowIterator().forEachRemaining(cells -> {
            if (cells.getRowNum() == 0) {
                // В первой строке заголовок
                cells.cellIterator().forEachRemaining(cell -> {
                    listField.add(fields.get(cell.getStringCellValue()));
                });
                return;
            }
            try {
                OT o = (OT) cl.getDeclaredConstructor().newInstance();
                int i = 0;
                for (String field : listField) {
                    Method setMethod = getSetterMethod(cl, field);
                    Cell cell = cells.getCell(i);
                    CellType cellType = cell.getCellType();
                    System.out.println(setMethod.getName());
                    System.out.println(cellType);
                    if (cellType == CellType.STRING) {
                        String value = cell.getStringCellValue();
                        setMethod.invoke(o, value);
                    } else if (cellType == CellType.NUMERIC) {
                        Double value = cell.getNumericCellValue();
                        setMethod.invoke(o, value);
                    }
                    i++;
                }
                list.add(o);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    private Class<?> getGetterReturnType(Class<?> cl, String fieldName) {
        String methodGetName = "get" + capitalize(fieldName);
        String methodIsName = "is" + capitalize(fieldName);
        Method method = null;
        try {
            method = cl.getDeclaredMethod(methodGetName);
        } catch (NoSuchMethodException ignored) {

        }
        if (method == null) {
            try {
                method = cl.getDeclaredMethod(methodIsName);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        return method.getReturnType();
    }

    private Method getSetterMethod(Class<?> cl, String fieldName) throws NoSuchMethodException {
        // Получить тип, возвращаемый соответствующим геттером
        Class<?> paramType = getGetterReturnType(cl, fieldName);
        // Найти сеттер
        return cl.getDeclaredMethod("set" + capitalize(fieldName), paramType);
    }

    private String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }
}
