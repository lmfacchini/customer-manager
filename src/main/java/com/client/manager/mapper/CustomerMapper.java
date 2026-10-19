package com.client.manager.mapper;

import com.client.manager.domain.Customer;
import com.client.manager.dto.CustomerResponse;
import com.client.manager.dto.CreateCustomerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    CustomerResponse toResponse(Customer client);

    Customer toClient(CreateCustomerRequest request);

}
