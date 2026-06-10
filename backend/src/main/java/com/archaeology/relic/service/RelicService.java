package com.archaeology.relic.service;

import com.archaeology.relic.entity.Coordinate3D;
import com.archaeology.relic.entity.Relic;
import com.archaeology.relic.entity.RestorationRecord;
import com.archaeology.relic.repository.ExcavationUnitRepository;
import com.archaeology.relic.repository.RelicRepository;
import com.archaeology.relic.repository.RestorationRecordRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelicService {

    @Autowired
    private RelicRepository relicRepository;

    @Autowired
    private RestorationRecordRepository restorationRecordRepository;

    @Autowired
    private ExcavationUnitRepository excavationUnitRepository;

    public List<Relic> findAll() {
        return relicRepository.findAll().stream()
                .peek(this::setExcavationUnitId)
                .collect(Collectors.toList());
    }

    public Optional<Relic> findById(Long id) {
        return relicRepository.findById(id).map(relic -> {
            setExcavationUnitId(relic);
            return relic;
        });
    }

    private void setExcavationUnitId(Relic relic) {
        if (relic.getExcavationUnit() != null) {
            relic.setExcavationUnitId(relic.getExcavationUnit().getId());
        }
    }

    public List<Relic> searchByName(String name) {
        return relicRepository.findByNameContainingIgnoreCase(name).stream()
                .peek(this::setExcavationUnitId)
                .collect(Collectors.toList());
    }

    public List<Relic> searchByRelicNo(String relicNo) {
        return relicRepository.findByRelicNoContainingIgnoreCase(relicNo).stream()
                .peek(this::setExcavationUnitId)
                .collect(Collectors.toList());
    }

    public List<Relic> searchByCategory(String category) {
        return relicRepository.findByCategoryContainingIgnoreCase(category).stream()
                .peek(this::setExcavationUnitId)
                .collect(Collectors.toList());
    }

    @Transactional
    public Relic save(Relic relic) {
        if (relic.getCoordinate() == null) {
            Coordinate3D coord = new Coordinate3D();
            coord.setX(0.0);
            coord.setY(0.0);
            coord.setZ(0.0);
            relic.setCoordinate(coord);
        }
        if (relic.getExcavationUnitId() != null) {
            excavationUnitRepository.findById(relic.getExcavationUnitId())
                    .ifPresent(relic::setExcavationUnit);
        }
        Relic saved = relicRepository.save(relic);
        setExcavationUnitId(saved);
        return saved;
    }

    @Transactional
    public Relic update(Long id, Relic relicDetails) {
        return relicRepository.findById(id).map(relic -> {
            relic.setRelicNo(relicDetails.getRelicNo());
            relic.setName(relicDetails.getName());
            relic.setCategory(relicDetails.getCategory());
            relic.setDescription(relicDetails.getDescription());
            relic.setMaterial(relicDetails.getMaterial());
            relic.setEra(relicDetails.getEra());
            relic.setExcavateDate(relicDetails.getExcavateDate());
            relic.setExcavator(relicDetails.getExcavator());
            relic.setExcavationSite(relicDetails.getExcavationSite());
            relic.setStratum(relicDetails.getStratum());
            relic.setPreservationStatus(relicDetails.getPreservationStatus());
            relic.setRemark(relicDetails.getRemark());

            if (relicDetails.getCoordinate() != null) {
                if (relic.getCoordinate() != null) {
                    relic.getCoordinate().setX(relicDetails.getCoordinate().getX());
                    relic.getCoordinate().setY(relicDetails.getCoordinate().getY());
                    relic.getCoordinate().setZ(relicDetails.getCoordinate().getZ());
                    relic.getCoordinate().setCoordinateSystem(relicDetails.getCoordinate().getCoordinateSystem());
                    relic.getCoordinate().setRemark(relicDetails.getCoordinate().getRemark());
                } else {
                    relic.setCoordinate(relicDetails.getCoordinate());
                }
            }

            if (relicDetails.getExcavationUnitId() != null) {
                excavationUnitRepository.findById(relicDetails.getExcavationUnitId())
                        .ifPresent(relic::setExcavationUnit);
            } else {
                relic.setExcavationUnit(null);
            }

            Relic updated = relicRepository.save(relic);
            setExcavationUnitId(updated);
            return updated;
        }).orElseThrow(() -> new RuntimeException("Relic not found with id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        relicRepository.deleteById(id);
    }

    @Transactional
    public RestorationRecord addRestorationRecord(Long relicId, RestorationRecord record) {
        return relicRepository.findById(relicId).map(relic -> {
            record.setRelic(relic);
            return restorationRecordRepository.save(record);
        }).orElseThrow(() -> new RuntimeException("Relic not found with id: " + relicId));
    }

    public List<RestorationRecord> getRestorationRecords(Long relicId) {
        return restorationRecordRepository.findByRelicIdOrderByRecordDateDesc(relicId);
    }

    @Transactional
    public void deleteRestorationRecord(Long recordId) {
        restorationRecordRepository.deleteById(recordId);
    }

    public List<Object[]> countByCategory() {
        return relicRepository.countByCategory();
    }

    public List<Object[]> countByEra() {
        return relicRepository.countByEra();
    }

    public List<Object[]> countByMaterial() {
        return relicRepository.countByMaterial();
    }

    public List<Object[]> countByPreservationStatus() {
        return relicRepository.countByPreservationStatus();
    }

    public List<Integer> getAvailableYears() {
        return relicRepository.findDistinctYears();
    }

    public List<Object[]> countByMonth(Integer year) {
        return relicRepository.countByMonth(year);
    }

    public List<Object[]> countByCategoryByYear(Integer year) {
        return relicRepository.countByCategoryByYear(year);
    }

    public List<Object[]> countByEraByYear(Integer year) {
        return relicRepository.countByEraByYear(year);
    }

    public List<Object[]> countByMaterialByYear(Integer year) {
        return relicRepository.countByMaterialByYear(year);
    }

    public List<Object[]> countByPreservationStatusByYear(Integer year) {
        return relicRepository.countByPreservationStatusByYear(year);
    }

    public long countByYear(Integer year) {
        return relicRepository.countByYear(year);
    }

    public List<Relic> searchByExcavationUnitId(Long excavationUnitId) {
        return relicRepository.findByExcavationUnit_Id(excavationUnitId).stream()
                .peek(this::setExcavationUnitId)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> batchImportFromExcel(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<Relic> importedRelics = new ArrayList<>();
        List<Map<String, String>> errors = new ArrayList<>();
        int totalRows = 0;
        int successCount = 0;

        Map<String, Long> unitNoToIdMap = new HashMap<>();
        excavationUnitRepository.findAll().forEach(u -> unitNoToIdMap.put(u.getUnitNo(), u.getId()));

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                throw new RuntimeException("Excel文件为空");
            }

            Row headerRow = rowIterator.next();
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String header = getCellStringValue(cell).trim();
                    String cleanHeader = header.replace("*", "").trim();
                    headerMap.put(cleanHeader, i);
                }
            }

            int rowNum = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNum++;
                totalRows++;

                try {
                    Relic relic = parseRelicFromRow(row, headerMap, unitNoToIdMap);
                    Relic saved = save(relic);
                    importedRelics.add(saved);
                    successCount++;
                } catch (Exception e) {
                    Map<String, String> error = new HashMap<>();
                    error.put("row", String.valueOf(rowNum));
                    error.put("message", e.getMessage());
                    errors.add(error);
                }
            }
        }

        result.put("total", totalRows);
        result.put("success", successCount);
        result.put("failed", errors.size());
        result.put("errors", errors);
        result.put("imported", importedRelics.stream()
                .peek(this::setExcavationUnitId)
                .collect(Collectors.toList()));
        return result;
    }

    private Relic parseRelicFromRow(Row row, Map<String, Integer> headerMap, Map<String, Long> unitNoToIdMap) {
        Relic relic = new Relic();

        String relicNo = getCellValue(row, headerMap, "编号");
        if (relicNo == null || relicNo.isEmpty()) {
            throw new RuntimeException("编号不能为空");
        }
        relic.setRelicNo(relicNo);

        String name = getCellValue(row, headerMap, "名称");
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("名称不能为空");
        }
        relic.setName(name);

        relic.setCategory(getCellValue(row, headerMap, "类别"));
        relic.setMaterial(getCellValue(row, headerMap, "材质"));
        relic.setEra(getCellValue(row, headerMap, "年代"));
        relic.setExcavationSite(getCellValue(row, headerMap, "出土地点"));
        relic.setExcavator(getCellValue(row, headerMap, "发掘人员"));
        relic.setStratum(getCellValue(row, headerMap, "地层"));
        relic.setPreservationStatus(getCellValue(row, headerMap, "保存状态"));
        relic.setDescription(getCellValue(row, headerMap, "描述"));
        relic.setRemark(getCellValue(row, headerMap, "备注"));

        String excavateDateStr = getCellValue(row, headerMap, "出土日期");
        if (excavateDateStr != null && !excavateDateStr.isEmpty()) {
            LocalDate date = parseDate(excavateDateStr, row, headerMap, "出土日期");
            relic.setExcavateDate(date);
        } else {
            relic.setExcavateDate(LocalDate.now());
        }

        if (relic.getExcavator() == null || relic.getExcavator().isEmpty()) {
            relic.setExcavator("批量导入");
        }

        String unitNo = getCellValue(row, headerMap, "所属探方");
        if (unitNo != null && !unitNo.isEmpty()) {
            Long unitId = unitNoToIdMap.get(unitNo);
            if (unitId != null) {
                relic.setExcavationUnitId(unitId);
            }
        }

        Coordinate3D coord = new Coordinate3D();
        String xStr = getCellValue(row, headerMap, "三维坐标_X");
        String yStr = getCellValue(row, headerMap, "三维坐标_Y");
        String zStr = getCellValue(row, headerMap, "三维坐标_Z");

        if (xStr != null && !xStr.isEmpty()) {
            coord.setX(parseDouble(xStr));
        } else {
            coord.setX(0.0);
        }
        if (yStr != null && !yStr.isEmpty()) {
            coord.setY(parseDouble(yStr));
        } else {
            coord.setY(0.0);
        }
        if (zStr != null && !zStr.isEmpty()) {
            coord.setZ(parseDouble(zStr));
        } else {
            coord.setZ(0.0);
        }

        coord.setCoordinateSystem(getCellValue(row, headerMap, "坐标系"));
        coord.setRemark(getCellValue(row, headerMap, "坐标备注"));

        relic.setCoordinate(coord);

        return relic;
    }

    private Double parseDouble(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("数字格式错误: " + str);
        }
    }

    private LocalDate parseDate(String str, Row row, Map<String, Integer> headerMap, String columnName) {
        try {
            Integer colIdx = headerMap.get(columnName);
            if (colIdx != null) {
                Cell cell = row.getCell(colIdx);
                if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                }
            }
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyyMMdd");
            String trimmed = str.trim();
            try {
                return LocalDate.parse(trimmed, formatter1);
            } catch (Exception e1) {
                try {
                    return LocalDate.parse(trimmed, formatter2);
                } catch (Exception e2) {
                    return LocalDate.parse(trimmed, formatter3);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("日期格式错误: " + str + "，请使用 yyyy-MM-dd 格式");
        }
    }

    private String getCellValue(Row row, Map<String, Integer> headerMap, String columnName) {
        Integer colIdx = headerMap.get(columnName);
        if (colIdx == null) {
            return null;
        }
        Cell cell = row.getCell(colIdx);
        if (cell == null) {
            return null;
        }
        return getCellStringValue(cell);
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double numVal = cell.getNumericCellValue();
                if (numVal == Math.floor(numVal) && !Double.isInfinite(numVal)) {
                    return String.valueOf((long) numVal);
                }
                return String.valueOf(numVal);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e2) {
                        return "";
                    }
                }
            default:
                return "";
        }
    }

    public Workbook generateTemplate() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("遗物导入模板");

        String[] headers = {
                "编号*", "名称*", "类别", "材质", "年代", "所属探方",
                "出土地点", "出土日期", "发掘人员", "地层", "保存状态",
                "三维坐标_X", "三维坐标_Y", "三维坐标_Z", "坐标系", "坐标备注",
                "描述", "备注"
        };

        String[] examples = {
                "RL2024001", "青铜鼎", "青铜器", "青铜", "商代", "TF001",
                "一号遗址区", "2024-01-15", "张三", "第3层", "完好",
                "123.45", "67.89", "2.34", "WGS84", "中心点坐标",
                "商代青铜鼎，三足两耳", "示例行，导入前请删除"
        };

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle exampleStyle = workbook.createCellStyle();
        Font exampleFont = workbook.createFont();
        exampleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        exampleFont.setItalic(true);
        exampleStyle.setFont(exampleFont);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 18 * 256);
        }

        Row exampleRow = sheet.createRow(1);
        for (int i = 0; i < examples.length; i++) {
            Cell cell = exampleRow.createCell(i);
            cell.setCellValue(examples[i]);
            cell.setCellStyle(exampleStyle);
        }

        return workbook;
    }
}
