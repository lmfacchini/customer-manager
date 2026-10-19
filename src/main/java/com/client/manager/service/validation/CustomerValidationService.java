package com.client.manager.service.validation;

import com.client.manager.dto.CustomerWriteRequest;

public interface CustomerValidationService {


    void validate(CustomerWriteRequest request);


}
