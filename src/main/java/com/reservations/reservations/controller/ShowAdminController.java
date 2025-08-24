package com.reservations.reservations.controller;

import com.reservations.reservations.dto.CollabForm;
import com.reservations.reservations.dto.RepForm;
import com.reservations.reservations.dto.ShowForm;
import com.reservations.reservations.model.Price;
import com.reservations.reservations.model.Representation;
import com.reservations.reservations.model.Show;
import com.reservations.reservations.model.Tag;
import com.reservations.reservations.service.*;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class ShowAdminController {

    // ======= Services métier existants =======
    @Autowired private ShowService showService;
    @Autowired private LocationService locationService;
    @Autowired private PriceService priceService;
    @Autowired private TagService tagService;
    @Autowired private ArtistService artistService;
    @Autowired private ArtistTypeService artistTypeService;

    // ======= DataSource pour export/import DB =======
    @Autowired private DataSource dataSource;

    // ========= VUES ADMIN =========

    private void populateFormLists(Model m) {
        m.addAttribute("locations", locationService.getAll());
        m.addAttribute("tags", tagService.findAll());
        m.addAttribute("artists", artistService.getAllArtists());
        m.addAttribute("types", artistTypeService.getAllTypes());
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("shows", showService.getAll());
        return "admin/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        ShowForm form = new ShowForm();
        form.getRepresentations().add(new RepForm());
        form.getCollaborators().add(new CollabForm());
        model.addAttribute("showForm", form);
        populateFormLists(model);
        return "admin/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("showForm") ShowForm form,
                         BindingResult br,
                         RedirectAttributes ra,
                         Model model) {
        if (br.hasErrors()) {
            populateFormLists(model);
            // FIX: vue correcte
            return "admin/form";
        }
        Show s = buildShowFromForm(form);
        showService.add(s);
        ra.addFlashAttribute("success", "Spectacle créé !");
        // FIX: redirection correcte
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Show s = showService.get(id.toString());
        ShowForm form = toForm(s);
        form.getRepresentations().forEach(r -> {});
        form.getCollaborators().forEach(c -> {});
        model.addAttribute("showForm", form);
        model.addAttribute("editingId", id);
        populateFormLists(model);
        // FIX: vue correcte
        return "admin/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("showForm") ShowForm form,
                         BindingResult br,
                         RedirectAttributes ra,
                         Model model) {
        if (br.hasErrors()) {
            populateFormLists(model);
            // FIX: vue correcte
            return "admin/form";
        }
        Show s = buildShowFromForm(form);
        s.setId(id);
        showService.update(id.toString(), s);
        ra.addFlashAttribute("success", "Spectacle mis à jour !");
        // FIX: redirection correcte
        return "redirect:/admin";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        showService.delete(id.toString());
        ra.addFlashAttribute("success", "Spectacle supprimé !");
        // FIX: redirection correcte
        return "redirect:/admin";
    }

    private Show buildShowFromForm(ShowForm form) {
        Show s = new Show();
        s.setTitle(form.getTitle());
        s.setDescription(form.getDescription());
        s.setPosterUrl(form.getPosterUrl());
        s.setBookable(form.isBookable());
        s.setLocation(locationService.get(form.getLocationId().toString()));

        // Prix plein
        Price plein = new Price();
        plein.setType("Plein");
        plein.setPrice(form.getFullPrice());
        plein.setStartDate(form.getFullPriceStartDate());
        plein.setEndDate(form.getFullPriceEndDate());
        priceService.addPrice(plein);
        s.getPrices().add(plein);

        // Prix réduit
        if (form.getReducedPrice() != null) {
            Price reduit = new Price();
            reduit.setType("Réduit");
            reduit.setPrice(form.getReducedPrice());
            reduit.setStartDate(form.getReducedPriceStartDate());
            reduit.setEndDate(form.getReducedPriceEndDate());
            priceService.addPrice(reduit);
            s.getPrices().add(reduit);
        }

        // Tags existants + nouveaux
        form.getTagIds().stream()
                .map(tagService::find)
                .forEach(opt -> opt.ifPresent(s.getTags()::add));
        if (form.getNewTags() != null) {
            form.getNewTags().stream()
                    .filter(t -> t != null && !t.isBlank())
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .forEach(label -> {
                        var tag = tagService.findByTag(label)
                                .orElseGet(() -> {
                                    var nt = new Tag();
                                    nt.setTag(label);
                                    tagService.save(nt);
                                    return nt;
                                });
                        s.getTags().add(tag);
                    });
        }

        // Représentations
        form.getRepresentations().forEach(rf -> {
            Representation r = new Representation();
            r.setScheduledAt(rf.getScheduledAt());
            r.setCapacity(rf.getCapacity());
            r.setLocation(locationService.get(rf.getLocationId().toString()));
            s.addRepresentation(r);
        });

        // Collaborateurs
        form.getCollaborators().forEach(cf -> {
            var artist = artistService.getArtist(cf.getArtistId());
            var at = artistTypeService.getOrCreate(artist, cf.getTypeId());
            s.addArtistType(at);
        });

        return s;
    }

    private ShowForm toForm(Show s) {
        ShowForm form = new ShowForm();
        form.setTitle(s.getTitle());
        form.setDescription(s.getDescription());
        form.setPosterUrl(s.getPosterUrl());
        form.setBookable(s.isBookable());
        form.setLocationId(s.getLocation().getId());

        s.getPrices().stream()
                .filter(p -> "Plein".equals(p.getType()))
                .findFirst()
                .ifPresent(p -> {
                    form.setFullPrice(p.getPrice());
                    form.setFullPriceStartDate(p.getStartDate());
                    form.setFullPriceEndDate(p.getEndDate());
                });

        s.getPrices().stream()
                .filter(p -> "Réduit".equals(p.getType()))
                .findFirst()
                .ifPresent(p -> {
                    form.setReducedPrice(p.getPrice());
                    form.setReducedPriceStartDate(p.getStartDate());
                    form.setReducedPriceEndDate(p.getEndDate());
                });

        s.getTags().forEach(t -> form.getTagIds().add(t.getId()));

        s.getRepresentations().forEach(r -> {
            RepForm rf = new RepForm();
            rf.setScheduledAt(r.getScheduledAt());
            rf.setCapacity(r.getCapacity());
            rf.setLocationId(r.getLocation().getId());
            form.getRepresentations().add(rf);
        });

        s.getArtistTypes().forEach(at -> {
            CollabForm cf = new CollabForm();
            cf.setArtistId(at.getArtist().getId());
            cf.setTypeId(at.getType().getId());
            form.getCollaborators().add(cf);
        });

        return form;
    }

    // ========= EXPORT DB -> XLSX (1 feuille / table) =========

    @GetMapping(value = "/export-db.xlsx",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<StreamingResponseBody> exportDbXlsx(
            @RequestParam(required = false) String exclude // ex: "flyway_schema_history,logs"
    ) {
        StreamingResponseBody body = out -> {
            try (Connection cn = dataSource.getConnection();
                 SXSSFWorkbook wb = new SXSSFWorkbook(500)) { // streaming
                wb.setCompressTempFiles(true);

                List<String> tables = new ArrayList<>(fetchAllTables(cn));
                if (exclude != null && !exclude.isBlank()) {
                    Set<String> toExclude = Arrays.stream(exclude.split(","))
                            .map(String::trim).filter(s -> !s.isBlank())
                            .collect(Collectors.toSet());
                    tables.removeIf(toExclude::contains);
                }

                for (String table : tables) {
                    List<String> cols = new ArrayList<>(fetchColumns(cn, table));
                    if (cols.isEmpty()) continue;

                    Sheet sh = wb.createSheet(safeSheetName(table));
                    int r = 0;

                    // header
                    Row header = sh.createRow(r++);
                    for (int c = 0; c < cols.size(); c++) header.createCell(c).setCellValue(cols.get(c));

                    String sql = "SELECT " + cols.stream().map(c -> "`" + c + "`").collect(Collectors.joining(", "))
                            + " FROM `" + table + "`";

                    try (PreparedStatement ps = cn.prepareStatement(sql,
                            ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                        ps.setFetchSize(Integer.MIN_VALUE); // MySQL streaming
                        try (ResultSet rs = ps.executeQuery()) {
                            final int n = cols.size();
                            while (rs.next()) {
                                Row row = sh.createRow(r++);
                                for (int c = 1; c <= n; c++) {
                                    Object v = rs.getObject(c);
                                    row.createCell(c - 1).setCellValue(valToString(v));
                                }
                            }
                        }
                    }
                }

                wb.write(out);
                wb.dispose();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        String filename = "db_export_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return ResponseEntity.ok().headers(headers).body(body);
    }

    // ========= IMPORT DB <- XLSX (FK ON : parents -> enfants ; mode upsert/remplacement) =========

    /**
     * Import générique depuis un .xlsx (1 feuille = 1 table, 1re ligne = noms de colonnes).
     * - mode=upsert (défaut) : INSERT ... ON DUPLICATE KEY UPDATE (pas de TRUNCATE)
     * - mode=replace_all      : TRUNCATE (enfants->parents) puis INSERT (parents->enfants), FK ON
     */
    @PostMapping(value = "/import-db.xlsx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<String> importDbXlsx(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "upsert") String mode // upsert | replace_all
    ) throws Exception {

        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().body("Fournis un fichier .xlsx valide (1 feuille = 1 table).");
        }
        boolean upsert = "upsert".equalsIgnoreCase(mode);

        try (Connection cn = dataSource.getConnection();
             Workbook wb = new XSSFWorkbook(file.getInputStream())) {

            cn.setAutoCommit(false);

            // Tables DB & feuilles correspondantes
            Set<String> dbTables = fetchAllTables(cn);
            Map<String, Sheet> sheets = new LinkedHashMap<>();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sh = wb.getSheetAt(i);
                if (sh != null && dbTables.contains(sh.getSheetName())) {
                    sheets.put(sh.getSheetName(), sh);
                }
            }
            if (sheets.isEmpty()) return ResponseEntity.badRequest().body("Aucune feuille ne correspond à une table de la base.");

            // Ordre FK parents -> enfants
            List<String> order = topoOrderByFK(cn);
            order.removeIf(t -> !sheets.containsKey(t)); // garder seulement celles présentes

            // Remplacement total : TRUNCATE en sens inverse (enfants -> parents), FK ON
            if (!upsert) {
                truncateInReverse(order, cn);
            }

            int tablesDone = 0, rowsTotal = 0;

            for (String table : order) {
                Sheet sheet = sheets.get(table);
                if (sheet == null) continue;

                Row header = sheet.getRow(0);
                if (header == null) continue;
                List<String> cols = readHeader(header);
                if (cols.isEmpty()) continue;

                // Valider colonnes vs DB
                Set<String> dbCols = fetchColumns(cn, table);
                for (String c : cols) if (!dbCols.contains(c))
                    throw new IllegalArgumentException("Colonne inconnue pour " + table + " : " + c);

                // Métadonnées colonnes
                Map<String, ColMeta> meta = fetchColumnMeta(cn, table);

                // SQL INSERT / UPSERT
                String columnsList = cols.stream().map(c -> "`" + c + "`").collect(Collectors.joining(", "));
                String placeholders = cols.stream().map(c -> "?").collect(Collectors.joining(", "));
                String insert = "INSERT INTO `" + table + "` (" + columnsList + ") VALUES (" + placeholders + ")";
                String sql = insert;
                if (upsert) {
                    Set<String> pkCols = fetchPkColumns(cn, table);
                    String updates = cols.stream()
                            .filter(c -> !pkCols.contains(c))
                            .map(c -> "`" + c + "`=VALUES(`" + c + "`)")
                            .collect(Collectors.joining(", "));
                    if (updates.isBlank()) {
                        updates = cols.stream()
                                .map(c -> "`" + c + "`=VALUES(`" + c + "`)")
                                .collect(Collectors.joining(", "));
                    }
                    sql = insert + " ON DUPLICATE KEY UPDATE " + updates;
                }

                int imported = 0;
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                        Row row = sheet.getRow(r);
                        if (row == null) continue;

                        boolean empty = true;
                        for (int i = 0; i < cols.size(); i++) {
                            String col = cols.get(i);
                            ColMeta cm = meta.get(col);

                            String raw = cellToString(row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
                            if (isBooleanColumn(cm)) raw = normalizeBooleanText(raw); // "true/false" -> "1/0"

                            if (raw != null && !raw.isBlank()) empty = false;

                            setParamByType(ps, i + 1, raw, cm); // typage MySQL 8
                        }
                        if (empty) continue;

                        ps.addBatch();
                        imported++;
                        if (imported % 1000 == 0) ps.executeBatch();
                    }
                    ps.executeBatch();
                }

                tablesDone++;
                rowsTotal += imported;
            }

            cn.commit();
            return ResponseEntity.ok((upsert ? "Fusion (FK ON) OK : " : "Remplacement (FK ON) OK : ")
                    + tablesDone + " table(s), " + rowsTotal + " ligne(s).");
        }
    }

    // ========= Helpers EXPORT/IMPORT =========

    private static String valToString(Object v) {
        if (v == null) return "";
        if (v instanceof byte[]) return Base64.getEncoder().encodeToString((byte[]) v);
        if (v instanceof Timestamp ts) return ts.toString();
        if (v instanceof java.sql.Date d) return d.toString();
        if (v instanceof Time t) return t.toString();
        return String.valueOf(v);
    }

    private static String safeSheetName(String name) {
        String s = name.replaceAll("[\\\\/?*\\[\\]:]", "_");
        return s.length() > 31 ? s.substring(0, 31) : s;
    }

    private static Set<String> fetchAllTables(Connection cn) throws SQLException {
        Set<String> ts = new LinkedHashSet<>();
        String sql = "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = database() AND table_type='BASE TABLE' ORDER BY table_name";
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ts.add(rs.getString(1));
        }
        return ts;
    }

    private static Set<String> fetchColumns(Connection cn, String table) throws SQLException {
        Set<String> cs = new LinkedHashSet<>();
        String sql = "SELECT column_name FROM information_schema.columns " +
                "WHERE table_schema = database() AND table_name = ? ORDER BY ordinal_position";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cs.add(rs.getString(1));
            }
        }
        return cs;
    }

    private static Set<String> fetchPkColumns(Connection cn, String table) throws SQLException {
        Set<String> pk = new LinkedHashSet<>();
        String sql = """
            SELECT kcu.column_name
            FROM information_schema.TABLE_CONSTRAINTS tc
            JOIN information_schema.KEY_COLUMN_USAGE kcu
              ON tc.constraint_name = kcu.constraint_name
             AND tc.table_schema = kcu.table_schema
             AND tc.table_name = kcu.table_name
            WHERE tc.table_schema = database()
              AND tc.table_name = ?
              AND tc.constraint_type = 'PRIMARY KEY'
            ORDER BY kcu.ordinal_position
            """;
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) pk.add(rs.getString(1));
            }
        }
        return pk;
    }

    private static List<String> topoOrderByFK(Connection cn) throws SQLException {
        Set<String> tables = fetchAllTables(cn);
        Map<String, Set<String>> parents = new HashMap<>();
        for (String t : tables) parents.put(t, new LinkedHashSet<>());

        String sql = """
            SELECT table_name, referenced_table_name
            FROM information_schema.KEY_COLUMN_USAGE
            WHERE table_schema = database() AND referenced_table_name IS NOT NULL
            """;
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String child = rs.getString(1);
                String parent = rs.getString(2);
                if (tables.contains(child) && tables.contains(parent) && !child.equals(parent)) {
                    parents.get(child).add(parent);
                }
            }
        }

        Deque<String> q = new ArrayDeque<>();
        Map<String, Integer> indeg = new HashMap<>();
        for (String t : tables) {
            int d = parents.get(t).size();
            indeg.put(t, d);
            if (d == 0) q.add(t);
        }

        List<String> order = new ArrayList<>();
        while (!q.isEmpty()) {
            String p = q.removeFirst();
            order.add(p);
            for (String child : tables) {
                if (parents.get(child).contains(p)) {
                    int d = indeg.merge(child, -1, Integer::sum);
                    if (d == 0) q.add(child);
                }
            }
        }

        // Reste (cycles/self-FK) à la fin
        for (String t : tables) if (!order.contains(t)) order.add(t);
        return order;
    }

    private static List<String> readHeader(Row header) {
        List<String> cols = new ArrayList<>();
        for (int c = 0; c < header.getLastCellNum(); c++) {
            Cell hc = header.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (hc != null && hc.getCellType() == CellType.STRING) {
                String name = hc.getStringCellValue();
                if (name != null && !name.isBlank()) cols.add(name.trim());
            }
        }
        return cols;
    }

    private static void truncateInReverse(List<String> order, Connection cn) throws SQLException {
        List<String> rev = new ArrayList<>(order);
        Collections.reverse(rev);
        try (Statement st = cn.createStatement()) {
            for (String t : rev) {
                st.execute("TRUNCATE TABLE `" + t + "`");
            }
        }
    }

    // ====== Métadonnées & typage pour MySQL 8 ======

    static final class ColMeta { String dataType; String columnType; } // ex: tinyint / tinyint(1)

    private static Map<String, ColMeta> fetchColumnMeta(Connection cn, String table) throws SQLException {
        Map<String, ColMeta> m = new LinkedHashMap<>();
        String sql = "SELECT column_name, data_type, column_type " +
                "FROM information_schema.columns " +
                "WHERE table_schema = database() AND table_name = ? ORDER BY ordinal_position";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ColMeta cm = new ColMeta();
                    cm.dataType = rs.getString("data_type");
                    cm.columnType = rs.getString("column_type");
                    m.put(rs.getString("column_name"), cm);
                }
            }
        }
        return m;
    }

    private static boolean isBooleanColumn(ColMeta cm) {
        if (cm == null) return false;
        String dt = cm.dataType == null ? "" : cm.dataType.toLowerCase();
        String ct = cm.columnType == null ? "" : cm.columnType.toLowerCase();
        return ("tinyint".equals(dt) && ct.startsWith("tinyint(1)")) || ("bit".equals(dt) && ct.startsWith("bit(1)"));
    }

    private static String normalizeBooleanText(String s) {
        if (s == null) return null;
        String v = s.trim().toLowerCase();
        return switch (v) {
            case "true","vrai","yes","oui","y","o","1" -> "1";
            case "false","faux","no","non","n","0"     -> "0";
            default -> s;
        };
    }

    private static final java.text.SimpleDateFormat DT_FMT = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final java.text.SimpleDateFormat D_FMT  = new java.text.SimpleDateFormat("yyyy-MM-dd");

    private static void setParamByType(PreparedStatement ps, int idx, String raw, ColMeta cm) throws SQLException {
        if (raw == null || raw.isBlank()) { ps.setNull(idx, Types.NULL); return; }
        String dt = (cm != null && cm.dataType != null) ? cm.dataType.toLowerCase() : "";

        try {
            switch (dt) {
                case "tinyint", "smallint", "mediumint", "int", "integer" -> {
                    ps.setLong(idx, Long.parseLong(raw)); return;
                }
                case "bigint" -> { ps.setLong(idx, Long.parseLong(raw)); return; }
                case "decimal", "numeric" -> { ps.setBigDecimal(idx, new java.math.BigDecimal(raw.replace(',', '.'))); return; }
                case "float" -> { ps.setFloat(idx, Float.parseFloat(raw)); return; }
                case "double" -> { ps.setDouble(idx, Double.parseDouble(raw)); return; }
                case "bit" -> { ps.setBoolean(idx, "1".equals(normalizeBooleanText(raw))); return; }
                case "date" -> { ps.setDate(idx, java.sql.Date.valueOf(raw)); return; }
                case "datetime", "timestamp" -> { ps.setTimestamp(idx, java.sql.Timestamp.valueOf(raw)); return; }
                case "time" -> { ps.setTime(idx, java.sql.Time.valueOf(raw)); return; }
                default -> { ps.setString(idx, raw); return; }
            }
        } catch (Exception parseEx) {
            // fallback : laisser MySQL tenter la conversion
            ps.setString(idx, raw);
        }
    }

    private static String cellToString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING -> {
                String s = cell.getStringCellValue();
                return (s == null || s.isBlank()) ? null : s.trim();
            }
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date d = cell.getDateCellValue();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);
                    boolean hasTime = cal.get(Calendar.HOUR_OF_DAY) != 0 ||
                            cal.get(Calendar.MINUTE) != 0 ||
                            cal.get(Calendar.SECOND) != 0;
                    return hasTime ? DT_FMT.format(d) : D_FMT.format(d);
                } else {
                    double v = cell.getNumericCellValue();
                    if (Math.floor(v) == v) return String.valueOf((long) v);
                    return new java.math.BigDecimal(String.valueOf(v)).toPlainString();
                }
            }
            case BOOLEAN -> { return cell.getBooleanCellValue() ? "true" : "false"; }
            case FORMULA -> {
                return switch (cell.getCachedFormulaResultType()) {
                    case STRING -> {
                        String s = cell.getStringCellValue();
                        yield (s == null || s.isBlank()) ? null : s.trim();
                    }
                    case NUMERIC -> {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date d = cell.getDateCellValue();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(d);
                            boolean hasTime = cal.get(Calendar.HOUR_OF_DAY) != 0 ||
                                    cal.get(Calendar.MINUTE) != 0 ||
                                    cal.get(Calendar.SECOND) != 0;
                            yield hasTime ? DT_FMT.format(d) : D_FMT.format(d);
                        } else {
                            double v = cell.getNumericCellValue();
                            yield (Math.floor(v) == v) ? String.valueOf((long) v)
                                    : new java.math.BigDecimal(String.valueOf(v)).toPlainString();
                        }
                    }
                    case BOOLEAN -> cell.getBooleanCellValue() ? "true" : "false";
                    default -> null;
                };
            }
            case BLANK, _NONE, ERROR -> { return null; }
            default -> { return null; }
        }
    }
}
