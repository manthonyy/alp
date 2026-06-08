import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

public class Voucher {
        private static final int POINTS_PER_10K = 100;
        protected static final int RUPIAH_PER_POINT = 100;
        private static final String COUNTER_FILE     = "voucher_counter.txt";

        private static final DeviceRgb COLOR_PRIMARY   = new DeviceRgb(39, 78, 19);
        private static final DeviceRgb COLOR_SECONDARY = new DeviceRgb(56, 118, 29);
        private static final DeviceRgb COLOR_LIGHT     = new DeviceRgb(217, 234, 211);
        private static final DeviceRgb COLOR_GOLD      = new DeviceRgb(184, 134, 11);
        private static final DeviceRgb COLOR_WHITE     = new DeviceRgb(255, 255, 255);

        private String  voucherId;
        private String  username;
        private int     pointsRedeemed;
        private long    voucherValue;
        private String  createdAt;

        public Voucher(String voucherId, String username, int pointsRedeemed) {
                this.voucherId      = voucherId;
                this.username       = username;
                this.pointsRedeemed = pointsRedeemed;
                this.voucherValue   = convertPointsToRupiah(pointsRedeemed);
                this.createdAt      = LocalDateTime.now().format(DateTimeFormatter
                                        .ofPattern("dd-MM-yyyy HH:mm:ss"));
        }

        private long convertPointsToRupiah(int points) {
                return (long) points * RUPIAH_PER_POINT;
        }

        public static int getMinimumPoints() {
                return POINTS_PER_10K;
        }

        private static int loadCounter() {
                File file = new File(COUNTER_FILE);
                if (!file.exists()) return 0;
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) return Integer.parseInt(line.trim());
                } catch (IOException | NumberFormatException e) {
                }
                return 0;
        }

        private static void saveCounter(int count) {
                try (FileWriter writer = new FileWriter(COUNTER_FILE)) {
                writer.write(String.valueOf(count));
                } catch (IOException e) {
                System.out.println("Warning: could not save voucher counter.");
                }
        }

        public static Voucher redeem(User user, int pointsToRedeem) {

                if (pointsToRedeem < getMinimumPoints()) {
                System.out.println("Minimum redemption is " + getMinimumPoints() + " points.");
                return null;
                }

                if (pointsToRedeem % POINTS_PER_10K != 0) {
                System.out.println("Points must be a multiple of 100");
                return null;
                }

                if (user.getTotalPoints() < pointsToRedeem) {
                System.out.println("Insufficient points!");
                System.out.println("Your Points   : " + user.getTotalPoints());
                System.out.println("Points Needed : " + pointsToRedeem);
                return null;
                }

                user.reducePoints(pointsToRedeem);

                int counter = loadCounter() + 1;
                saveCounter(counter);

                String id = "VCH" + String.format("%03d", counter);
                return new Voucher(id, user.getUsername(), pointsToRedeem);
        }

        public void displayVoucher() {
                System.out.println("=== REWARD VOUCHER ===");
                System.out.printf("  Voucher ID   : %-21s%n", voucherId);
                System.out.printf("  Username     : %-21s%n", username);
                System.out.printf("  Points Used  : %-21s%n", pointsRedeemed + " pts");
                System.out.printf("  Value        : Rp %-18s%n", String.format("%,d", voucherValue));
                System.out.printf("  Created      : %-21s%n", createdAt);
        }

        public void exportToPdf() {
                String filename = "Voucher_" + voucherId + "_" + username + ".pdf";

                try {
                PdfWriter   pdfWriter = new PdfWriter(filename);
                PdfDocument pdfDoc    = new PdfDocument(pdfWriter);
                Document    document  = new Document(pdfDoc);

                PdfFont boldFont   = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                PdfFont thinFont   = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

                document.add(new Paragraph("SMART WASTE MANAGEMENT SYSTEM")
                        .setFont(boldFont).setFontSize(14).setFontColor(COLOR_PRIMARY)
                        .setTextAlignment(TextAlignment.CENTER).setMarginBottom(2));

                document.add(new Paragraph("Reward Voucher")
                        .setFont(thinFont).setFontSize(11).setFontColor(COLOR_SECONDARY)
                        .setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));

                SolidLine headerLine = new SolidLine(2f);
                headerLine.setColor(COLOR_PRIMARY);
                document.add(new LineSeparator(headerLine).setMarginBottom(16));

                Table card = new Table(UnitValue.createPercentArray(new float[]{100}))
                        .setWidth(UnitValue.createPercentValue(85))
                        .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

                Table inner = new Table(UnitValue.createPercentArray(new float[]{45, 55}))
                        .setWidth(UnitValue.createPercentValue(100));

                Table idRow = new Table(UnitValue.createPercentArray(new float[]{100}))
                        .setWidth(UnitValue.createPercentValue(100));
                idRow.addCell(new Cell()
                        .add(new Paragraph(voucherId).setFont(boldFont).setFontSize(22)
                                .setFontColor(COLOR_WHITE).setTextAlignment(TextAlignment.CENTER))
                        .setBackgroundColor(COLOR_PRIMARY).setBorder(Border.NO_BORDER).setPadding(10));

                inner.addCell(new Cell(1, 2).add(idRow)
                        .setBorder(Border.NO_BORDER).setPadding(0).setMarginBottom(8));

                inner.addCell(new Cell(1, 2)
                        .add(new Paragraph("Rp " + String.format("%,d", voucherValue))
                                .setFont(boldFont).setFontSize(28).setFontColor(COLOR_GOLD)
                                .setTextAlignment(TextAlignment.CENTER))
                        .setBackgroundColor(new DeviceRgb(254, 249, 231))
                        .setBorder(new SolidBorder(COLOR_GOLD, 1.5f))
                        .setPadding(12).setMarginBottom(8));

                addCardRow(inner, normalFont, boldFont, "Username",    username);
                addCardRow(inner, normalFont, boldFont, "Points Used", pointsRedeemed + " pts");
                addCardRow(inner, normalFont, boldFont, "Issued On",   createdAt);

                card.addCell(new Cell().add(inner).setBackgroundColor(COLOR_LIGHT).setBorder(new SolidBorder(COLOR_SECONDARY, 1.5f)).setPadding(16));

                document.add(card);
                document.add(new Paragraph("\n"));

                document.add(new Paragraph("Terms & Conditions").setFont(boldFont).setFontSize(10).setFontColor(COLOR_PRIMARY).setMarginBottom(4));

                String[] terms = {
                        "1. This voucher is valid for one-time use only.",
                        "2. Voucher cannot be exchanged for cash.",
                        "3. Lost or damaged vouchers will not be replaced.",
                        "4. This voucher is non-transferable."
                };
                for (String term : terms) {
                        document.add(new Paragraph(term).setFont(normalFont).setFontSize(9).setFontColor(ColorConstants.DARK_GRAY).setMarginBottom(2));
                }

                document.add(new Paragraph("\n"));
                SolidLine footerLine = new SolidLine(0.5f);
                footerLine.setColor(ColorConstants.GRAY);
                document.add(new LineSeparator(footerLine).setMarginBottom(4));
                document.add(new Paragraph("Smart Waste Management System  |  Generated: " + createdAt)
                        .setFont(normalFont).setFontSize(8).setFontColor(ColorConstants.GRAY)
                        .setTextAlignment(TextAlignment.CENTER));

                document.close();
                System.out.println("Voucher PDF exported: " + filename);

                } catch (IOException e) {
                System.out.println("Error exporting voucher PDF: " + e.getMessage());
                }
        }

        private void addCardRow(Table table, PdfFont labelFont, PdfFont valueFont,
                                String label, String value) throws IOException {
                table.addCell(new Cell()
                        .add(new Paragraph(label).setFont(labelFont).setFontSize(10)
                                .setFontColor(ColorConstants.DARK_GRAY))
                        .setBorder(Border.NO_BORDER).setPaddingBottom(6));
                table.addCell(new Cell()
                        .add(new Paragraph(value).setFont(valueFont).setFontSize(10)
                                .setFontColor(COLOR_PRIMARY))
                        .setBorder(Border.NO_BORDER).setPaddingBottom(6));
        }

        public String getVoucherId(){ 
                return voucherId; 
        }
        public String getUsername(){ 
                return username; 
        }
        public int getPointsRedeemed(){ 
                return pointsRedeemed; 
        }
        public long getVoucherValue(){ 
                return voucherValue; 
        }
        public String getCreatedAt(){ 
                return createdAt; 
        }
}