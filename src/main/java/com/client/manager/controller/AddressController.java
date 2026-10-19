package com.client.manager.controller;

import com.client.manager.dto.AddressResponse;
import com.client.manager.dto.PrioritizedAddressRequest;
import com.client.manager.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/{cep}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AddressResponse> findByCep(@PathVariable String cep) {
        return ResponseEntity.ok(addressService.findByCep(cep));
    }

    @PutMapping("/prioritized")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AddressResponse> savePrioritized(
            @Valid @RequestBody PrioritizedAddressRequest request
    ) {
        return ResponseEntity.ok(addressService.savePrioritized(request));
    }

    @DeleteMapping("/prioritized/{cep}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePrioritized(@PathVariable String cep) {
        addressService.deletePrioritized(cep);
        return ResponseEntity.noContent().build();
    }
}
