package com.virul.medisure.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.virul.medisure.model.Policy;
import com.virul.medisure.model.PolicyHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final String policyDocsDir = "uploads/policy-documents/";

    public String generatePolicyDocument(PolicyHolder policyHolder) {
        try {
            // Create directory if it doesn't exist
            File directory = new File(policyDocsDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate filename
            String filename = "policy_" + policyHolder.getId() + "_" + UUID.randomUUID() + ".pdf";
            String filePath = policyDocsDir + filename;

            // Create PDF
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add content
            document.add(new Paragraph("MEDISUR HEALTH INSURANCE")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("POLICY CERTIFICATE")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Policy Details Table
            Table table = new Table(2);
            table.setWidth(500);

            addTableRow(table, "Policy Holder Name:", policyHolder.getUser().getFullName());
            addTableRow(table, "Email:", policyHolder.getUser().getEmail());
            addTableRow(table, "Phone:", policyHolder.getUser().getPhone());
            addTableRow(table, "Policy Name:", policyHolder.getPolicy().getName());
            addTableRow(table, "Policy Type:", policyHolder.getPolicy().getType().toString());
            addTableRow(table, "Coverage Amount:", "Rs. " + policyHolder.getPolicy().getCoverageAmount());
            addTableRow(table, "Premium Amount:", "Rs. " + policyHolder.getPolicy().getPremiumAmount());
            addTableRow(table, "Policy Start Date:", policyHolder.getStartDate().format(DateTimeFormatter.ISO_DATE));
            addTableRow(table, "Policy End Date:", policyHolder.getEndDate().format(DateTimeFormatter.ISO_DATE));
            addTableRow(table, "Status:", policyHolder.getStatus().toString());

            document.add(table);

            document.add(new Paragraph("\n\nPolicy Description:")
                    .setBold()
                    .setMarginTop(20));
            document.add(new Paragraph(policyHolder.getPolicy().getDescription() != null ? 
                    policyHolder.getPolicy().getDescription() : "Standard health insurance policy"));

            document.add(new Paragraph("\n\nThis is a computer-generated document. No signature is required.")
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(40));

            document.close();

            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate policy document", e);
        }
    }

    private void addTableRow(Table table, String label, String value) {
        table.addCell(new Paragraph(label).setBold());
        table.addCell(value != null ? value : "N/A");
    }
}
