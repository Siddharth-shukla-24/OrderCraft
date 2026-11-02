package com.ordercraft.controller;

import com.ordercraft.dto.TrackingDetailDto;
import com.ordercraft.dto.TrackingSummaryDto;
import com.ordercraft.entity.OrderStatus;
import com.ordercraft.service.TrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping
    public ResponseEntity<List<TrackingSummaryDto>> getTrackingSummaries(
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(trackingService.getTrackingSummaries(orderStatus, paymentStatus, search));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<TrackingDetailDto> getTrackingDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(trackingService.getTrackingDetail(orderId));
    }
}
