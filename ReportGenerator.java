import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

public class ReportGenerator {

    private TransactionManager transactionManager;
    private ArrayList<User> userList;

    private static final DeviceRgb COLOR_HEADER_BG  = new DeviceRgb(39, 78, 19);//dark green
    private static final DeviceRgb COLOR_SUBHEADER   = new DeviceRgb(56, 118, 29);//medium green
    private static final DeviceRgb COLOR_ROW_ALT     = new DeviceRgb(217, 234, 211);//light green
    private static final DeviceRgb COLOR_WHITE        = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb COLOR_DARK_TEXT    = new DeviceRgb(30, 30, 30);

    public ReportGenerator(TransactionManager transactionManager, ArrayList<User> userList) {
        this.transactionManager = transactionManager;
        this.userList = userList;
    }

    public void generateTransactionReport() {
        System.out.println("=== TRANSACTION REPORT ===");
        System.out.println("Generated : " + getTimestamp());
        System.out.println("────────────────────────────────────────────────────────────");

        ArrayList<Transaction> transactions = transactionManager.transactionList;

        if (transactions.isEmpty()) {
            System.out.println("No transaction data available.");
            return;
        }

        System.out.printf("%-10s %-15s %-15s %-10s %-8s%n",
                "TRX ID", "Username", "Waste Type", "Weight", "Points");
        System.out.println("────────────────────────────────────────────────────────────");

        for (Transaction t : transactions) {
            System.out.printf("%-10s %-15s %-15s %-10.2f %-8d%n",
                    t.transactionId,
                    t.user.getUsername(),
                    t.wasteItem.wasteName,
                    t.weight,
                    t.pointsEarned);
        }

        System.out.println("────────────────────────────────────────────────────────────");
        System.out.println("Total Transactions : " + transactions.size());
        System.out.printf("Total Weight       : %.2f Kg%n", transactionManager.calculateTotalWaste());
        System.out.printf("Total Points Given : %d%n", transactionManager.calculateTotalPoints());
        System.out.println("══════════════════════════════════════════");
    }

    public void generateWasteReport() {
        System.out.println("=== WASTE REPORT ===");
        System.out.println("Generated : " + getTimestamp());
        System.out.println("────────────────────────────────────────────────────────────");

        ArrayList<Transaction> transactions = transactionManager.transactionList;

        if (transactions.isEmpty()) {
            System.out.println("No waste data available.");
            return;
        }

        Map<String, Double> wasteByType  = new HashMap<>();
        Map<String, Integer> countByType = new HashMap<>();

        for (Transaction t : transactions) {
            String type = t.wasteItem.wasteName;
            wasteByType.put(type,  wasteByType.getOrDefault(type, 0.0) + t.weight);
            countByType.put(type, countByType.getOrDefault(type, 0) + 1);
        }

        System.out.printf("%-20s %-15s %-12s%n", "Waste Type", "Total Weight", "# Deposits");
        System.out.println("────────────────────────────────────────────────────────────");

        for (Map.Entry<String, Double> entry : wasteByType.entrySet()) {
            System.out.printf("%-20s %-15.2f %-12d%n",
                    entry.getKey(),
                    entry.getValue(),
                    countByType.get(entry.getKey()));
        }

        System.out.println("──────────────────────────────────────────");
        System.out.printf("TOTAL WASTE COLLECTED : %.2f Kg%n", transactionManager.calculateTotalWaste());
        System.out.println("══════════════════════════════════════════");
    }

    public void generateUserReport() {
        System.out.println("=== USER REPORT ===");
        System.out.println("Generated : " + getTimestamp());
        System.out.println("──────────────────────────────────────────");

        if (userList.isEmpty()) {
            System.out.println("No user data available.");
            return;
        }

        System.out.printf("%-10s %-15s %-8s %-12s %-8s%n",
                "User ID", "Username", "Role", "Points", "Deposits");
        System.out.println("──────────────────────────────────────────");

        int totalUsers = 0, totalDeposits = 0;

        for (User u : userList) {
            System.out.printf("%-10s %-15s %-8s %-12d %-8d%n",
                    u.getUserId(),
                    u.getUsername(),
                    u.getRole(),
                    u.getTotalPoints(),
                    u.transactionHistory.size());
            totalUsers++;
            totalDeposits += u.transactionHistory.size();
        }

        System.out.println("──────────────────────────────────────────");
        System.out.println("Total Users    : " + totalUsers);
        System.out.println("Total Deposits : " + totalDeposits);
        System.out.println("══════════════════════════════════════════");
    }

    public void exportTransactionReportToPdf() {
        String filename = "Transaction_Report_" + getFileTimestamp() + ".pdf";
        try {
            PdfWriter  pdfWriter  = new PdfWriter(filename);
            PdfDocument pdfDoc   = new PdfDocument(pdfWriter);
            Document    document  = new Document(pdfDoc);

            PdfFont boldFont   = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            addReportHeader(document, boldFont, "TRANSACTION REPORT",
                    "Smart Waste Management System");

            ArrayList<Transaction> transactions = transactionManager.transactionList;

            addSectionTitle(document, boldFont, "Summary");
            Table summary = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .setWidth(UnitValue.createPercentValue(100));
            addSummaryRow(summary, normalFont, boldFont, "Total Transactions", String.valueOf(transactions.size()));
            addSummaryRow(summary, normalFont, boldFont, "Total Waste Collected", String.format("%.2f Kg", transactionManager.calculateTotalWaste()));
            addSummaryRow(summary, normalFont, boldFont, "Total Points Distributed", String.valueOf(transactionManager.calculateTotalPoints()));
            document.add(summary);
            document.add(new Paragraph("\n"));

            addSectionTitle(document, boldFont, "Transaction Detail");

            if (transactions.isEmpty()) {
                document.add(new Paragraph("No transaction data available.")
                        .setFont(normalFont).setFontSize(10));
            } else {
                String[] headers = {"TRX ID", "Username", "Waste Type", "Weight (Kg)", "Points"};
                float[]  cols    = {15, 20, 25, 20, 20};
                Table table = buildTableHeader(boldFont, headers, cols);

                for (int i = 0; i < transactions.size(); i++) {
                    Transaction t   = transactions.get(i);
                    DeviceRgb rowBg = (i % 2 == 0) ? COLOR_WHITE : COLOR_ROW_ALT;
                    addDataRow(table, normalFont, rowBg,
                            t.transactionId,
                            t.user.getUsername(),
                            t.wasteItem.wasteName,
                            String.format("%.2f", t.weight),
                            String.valueOf(t.pointsEarned));
                }
                document.add(table);
            }

            addFooter(document, normalFont);
            document.close();

            System.out.println("Transaction Report exported: " + filename);

        } catch (IOException e) {
            System.out.println("Error exporting Transaction Report PDF: " + e.getMessage());
        }
    }

    public void exportWasteReportToPdf() {
        String filename = "Waste_Report_" + getFileTimestamp() + ".pdf";
        try {
            PdfWriter  pdfWriter  = new PdfWriter(filename);
            PdfDocument pdfDoc   = new PdfDocument(pdfWriter);
            Document    document  = new Document(pdfDoc);

            PdfFont boldFont   = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            addReportHeader(document, boldFont, "WASTE REPORT",
                    "Smart Waste Management System");

            ArrayList<Transaction> transactions = transactionManager.transactionList;

            // Aggregate
            Map<String, Double>  wasteByType  = new HashMap<>();
            Map<String, Integer> countByType  = new HashMap<>();
            Map<String, Integer> pointsByType = new HashMap<>();

            for (Transaction t : transactions) {
                String type = t.wasteItem.wasteName;
                wasteByType.put(type,  wasteByType.getOrDefault(type,  0.0) + t.weight);
                countByType.put(type, countByType.getOrDefault(type, 0) + 1);
                pointsByType.put(type, pointsByType.getOrDefault(type, 0) + t.pointsEarned);
            }

            addSectionTitle(document, boldFont, "Summary");
            Table summary = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .setWidth(UnitValue.createPercentValue(100));
            addSummaryRow(summary, normalFont, boldFont, "Total Waste Types", String.valueOf(wasteByType.size()));
            addSummaryRow(summary, normalFont, boldFont, "Total Waste Collected",
                    String.format("%.2f Kg", transactionManager.calculateTotalWaste()));
            addSummaryRow(summary, normalFont, boldFont, "Total Deposits", String.valueOf(transactions.size()));
            document.add(summary);
            document.add(new Paragraph("\n"));

            addSectionTitle(document, boldFont, "Waste Breakdown by Type");

            if (wasteByType.isEmpty()) {
                document.add(new Paragraph("No waste data available.")
                        .setFont(normalFont).setFontSize(10));
            } else {
                String[] headers = {"Waste Type", "Total Weight (Kg)", "# Deposits", "Points Generated"};
                float[]  cols    = {30, 25, 20, 25};
                Table table = buildTableHeader(boldFont, headers, cols);

                int i = 0;
                for (Map.Entry<String, Double> entry : wasteByType.entrySet()) {
                    DeviceRgb rowBg = (i % 2 == 0) ? COLOR_WHITE : COLOR_ROW_ALT;
                    addDataRow(table, normalFont, rowBg,
                            entry.getKey(),
                            String.format("%.2f", entry.getValue()),
                            String.valueOf(countByType.get(entry.getKey())),
                            String.valueOf(pointsByType.get(entry.getKey())));
                    i++;
                }
                document.add(table);
            }

            addFooter(document, normalFont);
            document.close();

            System.out.println("Waste Report exported: " + filename);

        } catch (IOException e) {
            System.out.println("Error exporting Waste Report PDF: " + e.getMessage());
        }
    }

    public void exportUserReportToPdf() {
        String filename = "User_Report_" + getFileTimestamp() + ".pdf";
        try {
            PdfWriter  pdfWriter  = new PdfWriter(filename);
            PdfDocument pdfDoc   = new PdfDocument(pdfWriter);
            Document    document  = new Document(pdfDoc);

            PdfFont boldFont   = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            addReportHeader(document, boldFont, "USER ACTIVITY REPORT",
                    "Smart Waste Management System");

            int totalDeposits = userList.stream()
                    .mapToInt(u -> u.transactionHistory.size()).sum();

            addSectionTitle(document, boldFont, "Summary");
            Table summary = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .setWidth(UnitValue.createPercentValue(100));
            addSummaryRow(summary, normalFont, boldFont, "Total Registered Users", String.valueOf(userList.size()));
            addSummaryRow(summary, normalFont, boldFont, "Total Deposits (System)", String.valueOf(totalDeposits));
            addSummaryRow(summary, normalFont, boldFont, "Total Points (System)",
                    String.valueOf(transactionManager.calculateTotalPoints()));
            document.add(summary);
            document.add(new Paragraph("\n"));

            addSectionTitle(document, boldFont, "User Activity Detail");

            if (userList.isEmpty()) {
                document.add(new Paragraph("No user data available.")
                        .setFont(normalFont).setFontSize(10));
            } else {
                String[] headers = {"User ID", "Username", "Role", "Points", "# Deposits"};
                float[]  cols    = {18, 22, 15, 20, 25};
                Table table = buildTableHeader(boldFont, headers, cols);

                for (int i = 0; i < userList.size(); i++) {
                    User      u     = userList.get(i);
                    DeviceRgb rowBg = (i % 2 == 0) ? COLOR_WHITE : COLOR_ROW_ALT;
                    addDataRow(table, normalFont, rowBg,
                            u.getUserId(),
                            u.getUsername(),
                            u.getRole().toString(),
                            String.valueOf(u.getTotalPoints()),
                            String.valueOf(u.transactionHistory.size()));
                }
                document.add(table);
            }

            addFooter(document, normalFont);
            document.close();

            System.out.println("User Report exported: " + filename);

        } catch (IOException e) {
            System.out.println("Error exporting User Report PDF: " + e.getMessage());
        }
    }

    private void addReportHeader(Document doc, PdfFont boldFont,
                                String title, String subtitle) throws IOException {
        doc.add(new Paragraph(title)
                .setFont(boldFont)
                .setFontSize(20)
                .setFontColor(COLOR_HEADER_BG)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(4));

        doc.add(new Paragraph(subtitle)
                .setFont(boldFont)
                .setFontSize(11)
                .setFontColor(COLOR_SUBHEADER)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(4));

        doc.add(new Paragraph("Generated: " + getTimestamp())
                .setFontSize(9)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        SolidLine line = new SolidLine(1.5f);
        line.setColor(COLOR_HEADER_BG);
        doc.add(new LineSeparator(line).setMarginBottom(12));
    }

    private void addSectionTitle(Document doc, PdfFont boldFont, String text) throws IOException {
        doc.add(new Paragraph(text)
                .setFont(boldFont)
                .setFontSize(12)
                .setFontColor(COLOR_WHITE)
                .setBackgroundColor(COLOR_SUBHEADER)
                .setPadding(5)
                .setMarginBottom(0));
    }

    private void addSummaryRow(Table table, PdfFont normalFont, PdfFont boldFont,
                                String label, String value) throws IOException {
        table.addCell(new Cell()
                .add(new Paragraph(label).setFont(normalFont).setFontSize(10))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setPadding(5)
                .setBackgroundColor(COLOR_ROW_ALT));

        table.addCell(new Cell()
                .add(new Paragraph(value).setFont(boldFont).setFontSize(10))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setPadding(5));
    }

    private Table buildTableHeader(PdfFont boldFont, String[] headers, float[] colWidths) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(colWidths))
                .setWidth(UnitValue.createPercentValue(100));

        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header)
                            .setFont(boldFont)
                            .setFontSize(10)
                            .setFontColor(COLOR_WHITE))
                    .setBackgroundColor(COLOR_HEADER_BG)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(6));
        }
        return table;
    }

    private void addDataRow(Table table, PdfFont normalFont,
                            DeviceRgb rowBg, String... values) throws IOException {
        for (String val : values) {
            table.addCell(new Cell()
                    .add(new Paragraph(val).setFont(normalFont).setFontSize(9))
                    .setBackgroundColor(rowBg)
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(5));
        }
    }

    private void addFooter(Document doc, PdfFont normalFont) throws IOException {
        doc.add(new Paragraph("\n"));
        SolidLine line = new SolidLine(0.5f);
        line.setColor(ColorConstants.GRAY);
        doc.add(new LineSeparator(line));
        doc.add(new Paragraph("Smart Waste Management System  |  Confidential Report  |  " + getTimestamp())
                .setFont(normalFont)
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(4));
    }

    private String getTimestamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    private String getFileTimestamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
}
