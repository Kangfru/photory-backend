package com.ot.controller;

import com.ot.config.AppConfig;
import com.ot.model.CommonResponse;
import com.ot.model.ticket.PhotoTicketResponse;
import com.ot.model.ticket.SavePhotoTicketRequest;
import com.ot.model.ticket.SavePhotoTicketResponse;
import com.ot.repository.photo_tikcet.entity.PhotoTicket;
import com.ot.service.PhotoTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/photo-tickets")
public class PhotoTicketController {

    private final AppConfig appConfig;

    private final PhotoTicketService photoTicketService;

    @PostMapping
    public SavePhotoTicketResponse savePhotoTicket(@RequestHeader Map<String, String> header
            , @RequestBody @Validated SavePhotoTicketRequest savePhotoTicketRequest) throws Exception {
        return photoTicketService.savePhotoTicket(savePhotoTicketRequest);
    }

    @GetMapping("/{photoTicketId}")
    public PhotoTicket getPhotoTicketById(@RequestHeader Map<String, String> header, @PathVariable String photoTicketId) throws Exception {
        return photoTicketService.getPhotoTicketVoBySeq(photoTicketId);
    }

    @GetMapping("/mine")
    public List<PhotoTicketResponse> getMyPhotoTickets(@RequestHeader Map<String, String> header) throws Exception {
        return photoTicketService.getMyPhotoTickets();
    }

    @DeleteMapping("/{photoTicketId}")
    public CommonResponse deletePhotoTicketById(@RequestHeader Map<String, String> header, @PathVariable String photoTicketId) throws Exception {
        return photoTicketService.deletePhotoTicketVoBySeq(photoTicketId);
    }

    @PutMapping("/{photoTicketId}")
    public CommonResponse updatePhotoTicketById(@RequestHeader Map<String, String> header, @PathVariable String photoTicketId, @RequestBody @Validated SavePhotoTicketRequest savePhotoTicketRequest) throws Exception {
        return photoTicketService.updatePhotoTicketById(photoTicketId, savePhotoTicketRequest);
    }

    @GetMapping("/popular")
    public List<PhotoTicketResponse> getPhotoTicketsPopular(@RequestHeader Map<String, String> header) {
        // 인기순 작품 가져오기 (12개 많이 만들어진 순)
        return photoTicketService.getPhotoTicketsPopular();
    }

}
