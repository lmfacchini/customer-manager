package com.client.manager.controller;

import com.client.manager.constants.CustomerStatus;
import com.client.manager.dto.*;
import com.client.manager.dto.*;
import com.client.manager.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;

import static com.client.manager.constants.Configuration.MAX_RESULT;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody CreateCustomerRequest request){
        Long id = service.create(request);
        return ResponseEntity.created(URI.create("client/"+id) ).build();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CustomerResponse> update(@PathVariable("id") Long id, @RequestBody UpdateCustomerRequest request){
        var response = service.update(id, request);
        return ResponseEntity.accepted().body(response);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable("id") Long id){
        service.delete(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CustomerResponse> retrieve(@PathVariable("id") Long id){
        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Pagenable<CustomerResponse>> filter(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "cpf", required = false) String document,
            @RequestParam(name = "status", required = false) CustomerStatus status,
            @RequestParam(name = "createdAtStart", required = false) Long createdAtStart,
            @RequestParam(name = "createdAtEnd", required = false) Long createdAtEnd,
            @RequestParam(name = "updatedAtStart", required = false) Long updatedAtStart,
            @RequestParam(name = "updatedAtEnd", required = false) Long updatedAtEnd,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "max", required = false) Integer max){
        int _max = Objects.requireNonNullElse(max, MAX_RESULT);
        _max = _max > MAX_RESULT || _max < 0 ?MAX_RESULT:_max;
        var filter = CustomerFilter.builder()
                .name(name)
                .email(email)
                .cpf(document)
                .status(status)
                .createdAtStart(createdAtStart)
                .createdAtEnd(createdAtEnd)
                .updatedAtStart(updatedAtStart)
                .updatedAtEnd(updatedAtEnd)
                .page(Objects.requireNonNullElse(page, 0))
                .max(_max)
                .build();
        var response = service.filter(filter);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}/score")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CustomerScoreResponse> score(@PathVariable("id") Long id){
        var response = service.score(id);
        return ResponseEntity.ok(response);
    }

}
