package com.datum.client.manager.mapper;

import com.datum.client.manager.domain.Customer;
import com.datum.client.manager.dto.CustomerResponse;
import com.datum.client.manager.dto.CreateCustomerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    CustomerResponse toResponse(Customer client);

    Customer toClient(CreateCustomerRequest request);

}
