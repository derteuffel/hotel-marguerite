package com.derteuffel.marguerite.services;

import org.springframework.core.io.InputStreamResource;

import java.util.Map;

public interface Html2PdfService {

    /**
     * @param data {@link Map}
     * @return a stream {@link InputStreamResource}
     */
    InputStreamResource html2PdfGenerator(Map<String, Object> data);
}
