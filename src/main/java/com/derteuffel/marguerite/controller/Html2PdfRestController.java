package com.derteuffel.marguerite.controller;



import com.derteuffel.marguerite.repository.ChambreRepository;
import com.derteuffel.marguerite.repository.CommandeRepository;
import com.derteuffel.marguerite.repository.FactureRepository;
import com.derteuffel.marguerite.repository.PlaceRepository;
import com.derteuffel.marguerite.services.Html2PdfService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Controller
@Log()
public class Html2PdfRestController {

    @Autowired
    private Html2PdfService documentGeneratorService;
    @Autowired
    private FactureRepository factureRepository;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private PlaceRepository placeRepository;

    @RequestMapping(value = "/html2pdf", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity html2pdf(@RequestBody Map<String, Object> data, @PathVariable Long id) {
        InputStreamResource resource = documentGeneratorService.html2PdfGenerator(data);
        if (resource != null) {
            return ResponseEntity
                    .ok()
                    .body(resource);
        } else {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
