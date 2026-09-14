package com.datum.client.manager.service.validation;

import com.datum.client.manager.dto.CustomerWriteRequest;

public interface CustomerValidationService {


    void validate(CustomerWriteRequest request);


}
