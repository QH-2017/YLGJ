package com.ylgj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylgj.commons.Result;
import com.ylgj.mapper.ReportMapper;
import com.ylgj.pojo.*;
import com.ylgj.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired private MemberService memberService;
    @Autowired private OrderService orderService;
    @Autowired private SetmealService setmealService;
    @Autowired private ReportMapper reportMapper;

    /**
     * 运营数据统计
     */
    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData() {
        Map<String, Object> reportData = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        // 会员统计
        long totalMember = memberService.count();
        long todayNewMember = memberService.count(new QueryWrapper<Member>().ge("regTime", today));
        long thisWeekNewMember = memberService.count(new QueryWrapper<Member>().ge("regTime", today.minusDays(7)));
        long thisMonthNewMember = memberService.count(new QueryWrapper<Member>().ge("regTime", today.withDayOfMonth(1)));

        // 预约统计（SQL COUNT 聚合，避免全表拉取后在内存统计）
        long todayOrderNumber = orderService.count(new QueryWrapper<Order>().eq("orderDate", today));
        long thisWeekOrderNumber = orderService.count(new QueryWrapper<Order>().ge("orderDate", today.minusDays(7)));
        long thisMonthOrderNumber = orderService.count(new QueryWrapper<Order>().ge("orderDate", today.withDayOfMonth(1)));
        long thisMonthVisitsNumber = orderService.count(new QueryWrapper<Order>()
            .ge("orderDate", today.withDayOfMonth(1)).eq("orderStatus", "已到诊"));
        long thisWeekVisitsNumber = orderService.count(new QueryWrapper<Order>()
            .ge("orderDate", today.minusDays(7)).eq("orderStatus", "已到诊"));
        long todayVisitsNumber = orderService.count(new QueryWrapper<Order>()
            .eq("orderDate", today).eq("orderStatus", "已到诊"));

        reportData.put("reportDate", sdf.format(new Date()));
        reportData.put("todayNewMember", todayNewMember);
        reportData.put("totalMember", totalMember);
        reportData.put("thisWeekNewMember", thisWeekNewMember);
        reportData.put("thisMonthNewMember", thisMonthNewMember);
        reportData.put("todayOrderNumber", todayOrderNumber);
        reportData.put("todayVisitsNumber", todayVisitsNumber);
        reportData.put("thisWeekOrderNumber", thisWeekOrderNumber);
        reportData.put("thisWeekVisitsNumber", thisWeekVisitsNumber);
        reportData.put("thisMonthOrderNumber", thisMonthOrderNumber);
        reportData.put("thisMonthVisitsNumber", thisMonthVisitsNumber);

        // 热门套餐统计：一次 SQL GROUP BY 聚合 + 套餐信息回填（按预约数量降序，取前5名）
        Map<Integer, Long> countBySetmeal = orderCountGroupBySetmeal();
        long totalOrderCount = orderService.count();
        List<Map<String, Object>> hotSetmeal = new ArrayList<>();
        if (!countBySetmeal.isEmpty()) {
            List<Setmeal> orderedSetmeals = setmealService.listByIds(new ArrayList<>(countBySetmeal.keySet()));
            for (Setmeal s : orderedSetmeals) {
                long count = countBySetmeal.getOrDefault(s.getId(), 0L);
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", s.getName());
                item.put("setmeal_count", count);
                item.put("proportion", totalOrderCount > 0 ?
                    Math.round(count * 1000.0 / totalOrderCount) / 10.0 + "%" : "0%");
                hotSetmeal.add(item);
            }
            hotSetmeal.sort((a, b) -> Long.compare((long) b.get("setmeal_count"), (long) a.get("setmeal_count")));
            if (hotSetmeal.size() > 5) hotSetmeal = new ArrayList<>(hotSetmeal.subList(0, 5));
        }
        reportData.put("hotSetmeal", hotSetmeal);

        return new Result(true, "获取运营数据成功", reportData);
    }

    /**
     * 会员统计（折线图数据）
     */
    @GetMapping("/getMemberReport")
    public Result getMemberReport() {
        LocalDate today = LocalDate.now();
        List<String> months = new ArrayList<>();
        List<Long> memberCounts = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = today.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1);
            String monthLabel = new SimpleDateFormat("yyyy-MM").format(java.sql.Date.valueOf(monthStart));
            months.add(monthLabel);

            long count = memberService.count(new QueryWrapper<Member>()
                .ge("regTime", monthStart)
                .lt("regTime", monthEnd));
            memberCounts.add(count);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("months", months);
        data.put("memberCounts", memberCounts);
        return new Result(true, "获取会员统计数据成功", data);
    }

    /**
     * 套餐预约占比（饼图数据）
     */
    @GetMapping("/getSetmealReport")
    public Result getSetmealReport() {
        // SQL GROUP BY 聚合：仅对被预约过的套餐统计，避免全表内存过滤
        Map<Integer, Long> countBySetmeal = orderCountGroupBySetmeal();
        List<Map<String, Object>> result = new ArrayList<>();
        if (!countBySetmeal.isEmpty()) {
            List<Setmeal> setmealList = setmealService.listByIds(new ArrayList<>(countBySetmeal.keySet()));
            for (Setmeal setmeal : setmealList) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", setmeal.getName());
                item.put("value", countBySetmeal.getOrDefault(setmeal.getId(), 0L));
                result.add(item);
            }
        }
        return new Result(true, "获取套餐统计数据成功", result);
    }

    /**
     * 导出运营数据报表为Excel
     */
    @GetMapping("/exportBusinessReport")
    public void exportBusinessReport(javax.servlet.http.HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = "运营数据报表_" + LocalDate.now() + ".xlsx";
            response.setHeader("Content-Disposition", "attachment;filename=" + 
                java.net.URLEncoder.encode(fileName, "UTF-8"));

            // 获取统计数据
            LocalDate today = LocalDate.now();
            long totalMember = memberService.count();
            long todayNewMember = memberService.count(new QueryWrapper<Member>().ge("regTime", today));
            long thisWeekNewMember = memberService.count(new QueryWrapper<Member>().ge("regTime", today.minusDays(7)));
            long thisMonthNewMember = memberService.count(new QueryWrapper<Member>().ge("regTime", today.withDayOfMonth(1)));

            // 订单明细仍全量拉取（用于 Sheet3 预约明细），统计指标走 SQL COUNT
            List<Order> allOrders = orderService.list();
            long todayOrders = orderService.count(new QueryWrapper<Order>().eq("orderDate", today));
            long thisWeekOrders = orderService.count(new QueryWrapper<Order>().ge("orderDate", today.minusDays(7)));
            long thisMonthOrders = orderService.count(new QueryWrapper<Order>().ge("orderDate", today.withDayOfMonth(1)));
            long todayVisits = orderService.count(new QueryWrapper<Order>()
                .eq("orderDate", today).eq("orderStatus", "已到诊"));
            long thisWeekVisits = orderService.count(new QueryWrapper<Order>()
                .ge("orderDate", today.minusDays(7)).eq("orderStatus", "已到诊"));
            long thisMonthVisits = orderService.count(new QueryWrapper<Order>()
                .ge("orderDate", today.withDayOfMonth(1)).eq("orderStatus", "已到诊"));

            // 创建Excel工作簿
            org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();

            // ========== 通用样式定义 ==========

            // 标题样式（大标题）
            org.apache.poi.xssf.usermodel.XSSFCellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 18);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

            // 日期行样式
            org.apache.poi.xssf.usermodel.XSSFCellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);

            // 分类标题样式（如"会员数据统计"、"预约到诊数据统计"）
            org.apache.poi.xssf.usermodel.XSSFCellStyle sectionStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont sectionFont = workbook.createFont();
            sectionFont.setBold(true);
            sectionFont.setFontHeightInPoints((short) 12);
            sectionStyle.setFont(sectionFont);
            sectionStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_GREEN.getIndex());
            sectionStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            sectionStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            sectionStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

            // 表头样式
            org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // 数据样式（居中）
            org.apache.poi.xssf.usermodel.XSSFCellStyle dataCenterStyle = workbook.createCellStyle();
            dataCenterStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            dataCenterStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            dataCenterStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataCenterStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataCenterStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataCenterStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // 数据样式（左对齐，用于文本列）
            org.apache.poi.xssf.usermodel.XSSFCellStyle dataLeftStyle = workbook.createCellStyle();
            dataLeftStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
            dataLeftStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            dataLeftStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataLeftStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataLeftStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataLeftStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // 数字格式样式（右对齐，用于数值列）
            org.apache.poi.xssf.usermodel.XSSFCellStyle dataNumberStyle = workbook.createCellStyle();
            dataNumberStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
            dataNumberStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            dataNumberStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataNumberStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataNumberStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataNumberStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // 百分比格式
            org.apache.poi.xssf.usermodel.XSSFCellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
            percentStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            percentStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            percentStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            percentStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            percentStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            org.apache.poi.xssf.usermodel.XSSFDataFormat dataFormat = workbook.createDataFormat();
            percentStyle.setDataFormat(dataFormat.getFormat("0.0%"));

            // ========== Sheet1: 运营数据概览 ==========
            org.apache.poi.xssf.usermodel.XSSFSheet sheet1 = workbook.createSheet("运营数据概览");

            // 设置固定列宽（单位：1/256个字符宽度）
            sheet1.setColumnWidth(0, 6000);  // 列A: 指标名称，约24字符宽
            sheet1.setColumnWidth(1, 4500);  // 列B: 今日
            sheet1.setColumnWidth(2, 4500);  // 列C: 本周
            sheet1.setColumnWidth(3, 4500);  // 列D: 本月

            // 行0: 大标题
            org.apache.poi.xssf.usermodel.XSSFRow titleRow = sheet1.createRow(0);
            titleRow.setHeightInPoints(40);
            org.apache.poi.xssf.usermodel.XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("医疗管家 - 运营数据报表");
            titleCell.setCellStyle(titleStyle);
            // 合并A1:D1并确保合并区域样式正确
            sheet1.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));
            // 为合并区域的其他单元格也设置边框样式
            for (int c = 1; c <= 3; c++) {
                titleRow.createCell(c).setCellStyle(titleStyle);
            }

            // 行1: 报表日期
            org.apache.poi.xssf.usermodel.XSSFRow dateRow = sheet1.createRow(1);
            dateRow.setHeightInPoints(22);
            org.apache.poi.xssf.usermodel.XSSFCell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("报表日期：" + today.toString());
            dateCell.setCellStyle(dateStyle);
            sheet1.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 3));

            // 行2: 空行
            sheet1.createRow(2);

            // 行3: 会员数据统计 - 分类标题
            org.apache.poi.xssf.usermodel.XSSFRow memberSectionRow = sheet1.createRow(3);
            memberSectionRow.setHeightInPoints(28);
            org.apache.poi.xssf.usermodel.XSSFCell memberSectionCell = memberSectionRow.createCell(0);
            memberSectionCell.setCellValue("会员数据统计");
            memberSectionCell.setCellStyle(sectionStyle);
            sheet1.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(3, 3, 0, 3));
            for (int c = 1; c <= 3; c++) {
                memberSectionRow.createCell(c).setCellStyle(sectionStyle);
            }

            // 行4: 表头 - 指标 | 数量 | 指标 | 数量
            org.apache.poi.xssf.usermodel.XSSFRow memberHeader = sheet1.createRow(4);
            memberHeader.setHeightInPoints(24);
            String[] memberHeaders = {"指标", "数量", "指标", "数量"};
            for (int i = 0; i < memberHeaders.length; i++) {
                org.apache.poi.xssf.usermodel.XSSFCell cell = memberHeader.createCell(i);
                cell.setCellValue(memberHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            // 行5: 新增会员数 | 8 | 总会员数 | 21
            org.apache.poi.xssf.usermodel.XSSFRow memberData1 = sheet1.createRow(5);
            memberData1.setHeightInPoints(22);
            { org.apache.poi.xssf.usermodel.XSSFCell c = memberData1.createCell(0); c.setCellValue("新增会员数"); c.setCellStyle(dataLeftStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = memberData1.createCell(1); c.setCellValue(todayNewMember); c.setCellStyle(dataNumberStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = memberData1.createCell(2); c.setCellValue("总会员数"); c.setCellStyle(dataLeftStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = memberData1.createCell(3); c.setCellValue(totalMember); c.setCellStyle(dataNumberStyle); }

            // 行6: 本周新增会员数 | 10 | 本月新增会员数 | 10
            org.apache.poi.xssf.usermodel.XSSFRow memberData2 = sheet1.createRow(6);
            memberData2.setHeightInPoints(22);
            { org.apache.poi.xssf.usermodel.XSSFCell c = memberData2.createCell(0); c.setCellValue("本周新增会员数"); c.setCellStyle(dataLeftStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = memberData2.createCell(1); c.setCellValue(thisWeekNewMember); c.setCellStyle(dataNumberStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = memberData2.createCell(2); c.setCellValue("本月新增会员数"); c.setCellStyle(dataLeftStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = memberData2.createCell(3); c.setCellValue(thisMonthNewMember); c.setCellStyle(dataNumberStyle); }

            // 行7: 空行
            sheet1.createRow(7);

            // 行8: 预约到诊数据统计 - 分类标题
            org.apache.poi.xssf.usermodel.XSSFRow orderSectionRow = sheet1.createRow(8);
            orderSectionRow.setHeightInPoints(28);
            org.apache.poi.xssf.usermodel.XSSFCell orderSectionCell = orderSectionRow.createCell(0);
            orderSectionCell.setCellValue("预约到诊数据统计");
            orderSectionCell.setCellStyle(sectionStyle);
            sheet1.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(8, 8, 0, 3));
            for (int c = 1; c <= 3; c++) {
                orderSectionRow.createCell(c).setCellStyle(sectionStyle);
            }

            // 表头: 指标 | 今日 | 本周 | 本月
            org.apache.poi.xssf.usermodel.XSSFRow orderHeader = sheet1.createRow(9);
            orderHeader.setHeightInPoints(24);
            String[] orderHeaders = {"指标", "今日", "本周", "本月"};
            for (int i = 0; i < orderHeaders.length; i++) {
                org.apache.poi.xssf.usermodel.XSSFCell cell = orderHeader.createCell(i);
                cell.setCellValue(orderHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            // 行10: 预约数
            org.apache.poi.xssf.usermodel.XSSFRow orderData1 = sheet1.createRow(10);
            orderData1.setHeightInPoints(22);
            { org.apache.poi.xssf.usermodel.XSSFCell c = orderData1.createCell(0); c.setCellValue("预约数"); c.setCellStyle(dataLeftStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = orderData1.createCell(1); c.setCellValue(todayOrders); c.setCellStyle(dataNumberStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = orderData1.createCell(2); c.setCellValue(thisWeekOrders); c.setCellStyle(dataNumberStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = orderData1.createCell(3); c.setCellValue(thisMonthOrders); c.setCellStyle(dataNumberStyle); }

            // 行11: 到诊数
            org.apache.poi.xssf.usermodel.XSSFRow orderData2 = sheet1.createRow(11);
            orderData2.setHeightInPoints(22);
            { org.apache.poi.xssf.usermodel.XSSFCell c = orderData2.createCell(0); c.setCellValue("到诊数"); c.setCellStyle(dataLeftStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = orderData2.createCell(1); c.setCellValue(todayVisits); c.setCellStyle(dataNumberStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = orderData2.createCell(2); c.setCellValue(thisWeekVisits); c.setCellStyle(dataNumberStyle); }
            { org.apache.poi.xssf.usermodel.XSSFCell c = orderData2.createCell(3); c.setCellValue(thisMonthVisits); c.setCellStyle(dataNumberStyle); }

            // ========== Sheet2: 套餐预约明细 ==========
            org.apache.poi.xssf.usermodel.XSSFSheet sheet2 = workbook.createSheet("套餐预约明细");

            // 设置列宽
            sheet2.setColumnWidth(0, 14000); // 套餐名称
            sheet2.setColumnWidth(1, 4000);  // 套餐价格
            sheet2.setColumnWidth(2, 4000);  // 预约数量
            sheet2.setColumnWidth(3, 4000);  // 占比

            // 标题行
            org.apache.poi.xssf.usermodel.XSSFRow sheet2Title = sheet2.createRow(0);
            sheet2Title.setHeightInPoints(36);
            org.apache.poi.xssf.usermodel.XSSFCell sheet2TitleCell = sheet2Title.createCell(0);
            sheet2TitleCell.setCellValue("套餐预约明细");
            sheet2TitleCell.setCellStyle(titleStyle);
            sheet2.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));
            for (int c = 1; c <= 3; c++) {
                sheet2Title.createCell(c).setCellStyle(titleStyle);
            }

            // 表头
            org.apache.poi.xssf.usermodel.XSSFRow setmealHeader = sheet2.createRow(1);
            setmealHeader.setHeightInPoints(24);
            String[] setmealHeaders = {"套餐名称", "套餐价格（元）", "预约数量", "占比"};
            for (int i = 0; i < setmealHeaders.length; i++) {
                org.apache.poi.xssf.usermodel.XSSFCell cell = setmealHeader.createCell(i);
                cell.setCellValue(setmealHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            Map<Integer, Long> exportCountBySetmeal = orderCountGroupBySetmeal();
            List<Setmeal> setmealList = setmealService.listByIds(new ArrayList<>(exportCountBySetmeal.keySet()));
            long totalOrders = exportCountBySetmeal.values().stream().mapToLong(Long::longValue).sum();
            int rowNum = 2;
            for (Setmeal setmeal : setmealList) {
                long count = exportCountBySetmeal.getOrDefault(setmeal.getId(), 0L);
                if (count > 0) {
                    org.apache.poi.xssf.usermodel.XSSFRow row = sheet2.createRow(rowNum++);
                    row.setHeightInPoints(22);
                    { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(0); c.setCellValue(setmeal.getName()); c.setCellStyle(dataLeftStyle); }
                    { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(1); c.setCellValue(setmeal.getPrice() != null ? setmeal.getPrice().doubleValue() : 0); c.setCellStyle(dataNumberStyle); }
                    { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(2); c.setCellValue(count); c.setCellStyle(dataNumberStyle); }
                    { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(3); c.setCellValue(totalOrders > 0 ? (double) count / totalOrders : 0); c.setCellStyle(percentStyle); }
                }
            }

            // ========== Sheet3: 预约记录明细 ==========
            org.apache.poi.xssf.usermodel.XSSFSheet sheet3 = workbook.createSheet("预约记录明细");

            // 设置列宽
            sheet3.setColumnWidth(0, 3000);  // 订单ID
            sheet3.setColumnWidth(1, 4500);  // 会员姓名
            sheet3.setColumnWidth(2, 5500);  // 手机号
            sheet3.setColumnWidth(3, 14000); // 体检套餐
            sheet3.setColumnWidth(4, 5500);  // 预约日期
            sheet3.setColumnWidth(5, 5500);  // 预约类型
            sheet3.setColumnWidth(6, 4000);  // 预约状态

            // 标题行
            org.apache.poi.xssf.usermodel.XSSFRow sheet3Title = sheet3.createRow(0);
            sheet3Title.setHeightInPoints(36);
            org.apache.poi.xssf.usermodel.XSSFCell sheet3TitleCell = sheet3Title.createCell(0);
            sheet3TitleCell.setCellValue("预约记录明细");
            sheet3TitleCell.setCellStyle(titleStyle);
            sheet3.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));
            for (int c = 1; c <= 6; c++) {
                sheet3Title.createCell(c).setCellStyle(titleStyle);
            }

            // 表头
            org.apache.poi.xssf.usermodel.XSSFRow detailHeader = sheet3.createRow(1);
            detailHeader.setHeightInPoints(24);
            String[] detailHeaders = {"订单ID", "会员姓名", "手机号", "体检套餐", "预约日期", "预约类型", "预约状态"};
            for (int i = 0; i < detailHeaders.length; i++) {
                org.apache.poi.xssf.usermodel.XSSFCell cell = detailHeader.createCell(i);
                cell.setCellValue(detailHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            // 构建会员ID到会员的映射
            Map<Integer, Member> memberMap = new HashMap<>();
            for (Member m : memberService.list()) {
                memberMap.put(m.getId(), m);
            }
            Map<Integer, Setmeal> setmealMap = new HashMap<>();
            for (Setmeal s : setmealService.list()) {
                setmealMap.put(s.getId(), s);
            }

            rowNum = 2;
            for (Order order : allOrders) {
                Member member = memberMap.get(order.getMemberId());
                Setmeal setmeal = setmealMap.get(order.getSetmealId());
                org.apache.poi.xssf.usermodel.XSSFRow row = sheet3.createRow(rowNum++);
                row.setHeightInPoints(22);
                { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(0); c.setCellValue(order.getId()); c.setCellStyle(dataCenterStyle); }
                { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(1); c.setCellValue(member != null ? member.getName() : "未知"); c.setCellStyle(dataCenterStyle); }
                { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(2); c.setCellValue(member != null ? member.getPhoneNumber() : ""); c.setCellStyle(dataCenterStyle); }
                { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(3); c.setCellValue(setmeal != null ? setmeal.getName() : "未知"); c.setCellStyle(dataLeftStyle); }
                { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(4); c.setCellValue(order.getOrderDate() != null ? order.getOrderDate().toString() : ""); c.setCellStyle(dataCenterStyle); }
                { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(5); c.setCellValue(order.getOrderType() != null ? order.getOrderType() : ""); c.setCellStyle(dataCenterStyle); }
                { org.apache.poi.xssf.usermodel.XSSFCell c = row.createCell(6); c.setCellValue(order.getOrderStatus() != null ? order.getOrderStatus() : ""); c.setCellStyle(dataCenterStyle); }
            }

            // 冻结首行（方便浏览数据量大的表格）
            sheet2.createFreezePane(0, 2);
            sheet3.createFreezePane(0, 2);

            // 输出到响应流
            java.io.OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<script>alert('导出失败：" + e.getMessage() + "');history.back();</script>");
            } catch (Exception ex) {}
        }
    }

    /**
     * 各套餐订单数统计：委托 SQL GROUP BY 聚合，返回 setmealId -> count 映射。
     */
    private Map<Integer, Long> orderCountGroupBySetmeal() {
        Map<Integer, Long> map = new HashMap<>();
        for (Map<String, Object> row : reportMapper.countOrderGroupBySetmeal()) {
            if (row.get("setmealId") != null && row.get("cnt") != null) {
                map.put(((Number) row.get("setmealId")).intValue(),
                        ((Number) row.get("cnt")).longValue());
            }
        }
        return map;
    }
}
