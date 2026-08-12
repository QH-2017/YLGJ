package com.ylgj.controller;

import com.ylgj.commons.Result;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件上传/下载控制器
 * - 体检资料上传
 * - 预约设置模板下载
 * - 预约设置模板上传
 */
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Value("${upload.path:./uploads/}")
    private String uploadPath;

    /**
     * 体检资料上传
     */
    @PostMapping("/uploadHealthData")
    public Result uploadHealthData(@RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "memberId", required = false) String memberId,
                                   @RequestParam(value = "description", required = false) String description) {
        if (file.isEmpty()) {
            return new Result(false, "请选择要上传的文件");
        }

        try {
            // 确保上传目录存在
            File dir = new File(uploadPath + "health-data/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalName = file.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf("."));
            String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 保存文件
            File dest = new File(dir, savedName);
            file.transferTo(dest.getAbsoluteFile());

            // 保存元数据到 JSON 文件
            String uploadTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Map<String, String> meta = new HashMap<>();
            meta.put("originalName", originalName);
            meta.put("savedName", savedName);
            meta.put("fileSize", String.valueOf(file.getSize()));
            meta.put("fileType", file.getContentType());
            meta.put("uploadTime", uploadTime);
            meta.put("memberId", memberId != null ? memberId : "");
            meta.put("description", description != null ? description : "");
            saveMetadata(meta);

            // 返回文件信息
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("originalName", originalName);
            fileInfo.put("savedName", savedName);
            fileInfo.put("fileSize", file.getSize());
            fileInfo.put("fileType", file.getContentType());
            fileInfo.put("uploadTime", uploadTime);

            return new Result(true, "上传成功", fileInfo);
        } catch (Exception e) {
            return new Result(false, "上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取已上传的体检资料列表
     */
    @GetMapping("/listHealthData")
    public Result listHealthData() {
        List<Map<String, String>> metaList = loadAllMetadata();
        // 反转列表，最新的在前
        Collections.reverse(metaList);
        return new Result(true, "查询成功", metaList);
    }

    /**
     * 下载文件
     */
    @GetMapping("/downloadHealthData/{savedName}")
    public void downloadHealthData(@PathVariable String savedName, HttpServletResponse response) {
        try {
            // 从元数据中查找原始文件名
            String originalName = savedName;
            List<Map<String, String>> metaList = loadAllMetadata();
            for (Map<String, String> meta : metaList) {
                if (savedName.equals(meta.get("savedName"))) {
                    originalName = meta.get("originalName");
                    break;
                }
            }

            File file = new File(uploadPath + "health-data/" + savedName);
            if (!file.exists()) {
                response.setStatus(404);
                return;
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(originalName, "UTF-8"));

            try (InputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/deleteHealthData/{savedName}")
    public Result deleteHealthData(@PathVariable String savedName) {
        try {
            // 删除实际文件
            File file = new File(uploadPath + "health-data/" + savedName);
            if (file.exists()) {
                file.delete();
            }
            // 删除元数据记录
            removeMetadata(savedName);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            return new Result(false, "删除失败：" + e.getMessage());
        }
    }

    // ==================== 元数据管理（JSON 文件） ====================

    private File getMetadataFile() {
        return new File(uploadPath + "health-data/metadata.json");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> loadAllMetadata() {
        File metaFile = getMetadataFile();
        if (!metaFile.exists()) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(metaFile)) {
            byte[] data = fis.readAllBytes();
            if (data.length == 0) return new ArrayList<>();
            String json = new String(data, "UTF-8");
            // 简单 JSON 解析（避免引入额外依赖）
            return parseJsonArray(json);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private synchronized void saveMetadata(Map<String, String> meta) {
        List<Map<String, String>> list = loadAllMetadata();
        list.add(meta);
        writeMetadata(list);
    }

    private synchronized void removeMetadata(String savedName) {
        List<Map<String, String>> list = loadAllMetadata();
        list.removeIf(m -> savedName.equals(m.get("savedName")));
        writeMetadata(list);
    }

    private void writeMetadata(List<Map<String, String>> list) {
        try {
            File metaFile = getMetadataFile();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Map<String, String> m = list.get(i);
                sb.append("{");
                int count = 0;
                for (Map.Entry<String, String> entry : m.entrySet()) {
                    if (count > 0) sb.append(",");
                    sb.append("\"").append(escapeJson(entry.getKey())).append("\":\"").append(escapeJson(entry.getValue())).append("\"");
                    count++;
                }
                sb.append("}");
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            try (FileOutputStream fos = new FileOutputStream(metaFile)) {
                fos.write(sb.toString().getBytes("UTF-8"));
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private List<Map<String, String>> parseJsonArray(String json) {
        List<Map<String, String>> result = new ArrayList<>();
        json = json.trim();
        if (!json.startsWith("[")) return result;
        json = json.substring(1, json.length() - 1).trim();
        if (json.isEmpty()) return result;

        String[] objects = json.split("\\},\\s*\\{");
        for (String obj : objects) {
            obj = obj.replace("{", "").replace("}", "").trim();
            if (obj.isEmpty()) continue;
            Map<String, String> map = new HashMap<>();
            String[] pairs = obj.split(",");
            for (String pair : pairs) {
                int colonIdx = pair.indexOf(':');
                if (colonIdx > 0) {
                    String key = pair.substring(0, colonIdx).replace("\"", "").trim();
                    String value = pair.substring(colonIdx + 1).replace("\"", "").trim();
                    map.put(key, value);
                }
            }
            if (!map.isEmpty()) result.add(map);
        }
        return result;
    }

    /**
     * 下载预约设置模板
     */
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建Excel模板
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("预约设置");

        // 设置表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

        // 创建表头行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"预约日期(yyyy-MM-dd)", "可预约人数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5000);
        }

        // 添加示例数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        for (int i = 1; i <= 5; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(sdf.format(cal.getTime()));
            row.createCell(1).setCellValue(50);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment;filename=" + URLEncoder.encode("预约设置模板.xlsx", "UTF-8"));

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 上传预约设置模板（批量导入）
     */
    @PostMapping("/uploadOrderSetting")
    public Result uploadOrderSetting(@RequestParam("excelFile") MultipartFile file) {
        if (file.isEmpty()) {
            return new Result(false, "请选择要上传的文件");
        }

        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            int successCount = 0;
            int failCount = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String dateStr = row.getCell(0).getStringCellValue();
                    double number = row.getCell(1).getNumericCellValue();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date orderDate = sdf.parse(dateStr);

                    // 这里可以保存到数据库
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                }
            }

            workbook.close();

            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failCount", failCount);

            return new Result(true, "导入完成", result);
        } catch (Exception e) {
            return new Result(false, "导入失败：" + e.getMessage());
        }
    }
}
