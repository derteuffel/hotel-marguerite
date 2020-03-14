package com.derteuffel.marguerite.services;

import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import lombok.extern.java.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

@Service
@Log
@RequiredArgsConstructor
public class Html2PdfServiceImpl implements Html2PdfService {


    @Autowired
    private  TemplateEngine templateEngine;

    @Override
    public InputStreamResource html2PdfGenerator(Map<String, Object> data) {

        Context context = new Context();
        context.setVariables(data);
        final String html = templateEngine.process("facture", context);

        log.log(INFO, html);

        final String DEST = "target/FA-2020-17-03-0001.pdf";

        try {
            HtmlConverter.convertToPdf(html, new FileOutputStream(DEST));
            return new InputStreamResource(new FileInputStream(DEST));

        } catch (IOException e) {
            log.log(SEVERE, e.getMessage(), e);
            return null;
        }
    }


}
