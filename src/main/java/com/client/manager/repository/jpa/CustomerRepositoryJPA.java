package com.client.manager.repository.jpa;

import com.client.manager.domain.Customer;
import com.client.manager.dto.CustomerFilter;
import com.client.manager.dto.Pagenable;
import com.client.manager.repository.CustomerRepository;
import com.client.manager.repository.jpa.specification.CustomerSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CustomerRepositoryJPA extends CustomerRepository, JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {


    @Override
    default Pagenable<Customer> filter(CustomerFilter filter){
        Specification<Customer> specification =
                CustomerSpecifications.byFilter(filter);

        int pageSize = filter.max();
        Pageable pageable = PageRequest.of(
                filter.page(),
                pageSize+1
        );
        Page<Customer> result = findAll(specification, pageable);
        List<Customer> clients = result.toList();

        int total = clients.size();

        clients = clients.isEmpty()
                ? List.of()
                : total> pageSize?(clients.subList(0, clients.size() - 1)):clients;

        return Pagenable.<Customer>builder().result(clients).hasNext(total>pageSize).build();
    }
}
