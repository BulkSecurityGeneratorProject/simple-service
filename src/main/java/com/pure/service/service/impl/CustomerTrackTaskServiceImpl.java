package com.pure.service.service.impl;

import com.pure.service.domain.Customer;
import com.pure.service.domain.CustomerTrackTask;
import com.pure.service.domain.TaskStatus;
import com.pure.service.domain.User;
import com.pure.service.region.RegionUtils;
import com.pure.service.repository.CustomerRepository;
import com.pure.service.repository.CustomerTrackTaskRepository;
import com.pure.service.repository.TaskRepository;
import com.pure.service.repository.TaskStatusRepository;
import com.pure.service.service.CustomerTrackTaskService;
import com.pure.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing CustomerTrackTask.
 */
@Service
@Transactional
public class CustomerTrackTaskServiceImpl implements CustomerTrackTaskService{

    private final Logger log = LoggerFactory.getLogger(CustomerTrackTaskServiceImpl.class);

    private final CustomerTrackTaskRepository customerTrackTaskRepository;
    private final TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerRepository customerRepository;

    public CustomerTrackTaskServiceImpl(CustomerTrackTaskRepository customerTrackTaskRepository, TaskRepository taskRepository) {
        this.customerTrackTaskRepository = customerTrackTaskRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Save a customerTrackTask.
     *
     * @param customerTrackTask the entity to save
     * @return the persisted entity
     */
    @Override
    public CustomerTrackTask save(CustomerTrackTask customerTrackTask) {
        log.debug("Request to save CustomerTrackTask : {}", customerTrackTask);

        if (customerTrackTask.getId() == null &&
            customerTrackTask.getTask() != null &&
            customerTrackTask.getTask().getId() == null) {

            //omg
            RegionUtils.setRegionAbstractAuditingRegionEntity(customerTrackTask.getTask());
            TaskStatus ongoing = taskStatusRepository.findByCode("ongoing");
            customerTrackTask.getTask().setTaskStatus(ongoing);

            User currentUser = userService.getUserWithAuthorities();
            customerTrackTask.getTask().setAssignee(currentUser);

            Customer customer = customerTrackTask.getCustomer();
            customer.setTrackStatus(ongoing.getName());
            customer.setNextTrackDate(customerTrackTask.getTask().getEstimateExecuteDate());
            customer.setNextTrackComments(customerTrackTask.getTask().getDescription());

            customerRepository.save(customer);
        }

        return customerTrackTaskRepository.save(customerTrackTask);
    }

    /**
     *  Get all the customerTrackTasks.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CustomerTrackTask> findAll(Pageable pageable) {
        log.debug("Request to get all CustomerTrackTasks");
        return customerTrackTaskRepository.findAll(pageable);
    }

    /**
     *  Get one customerTrackTask by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public CustomerTrackTask findOne(Long id) {
        log.debug("Request to get CustomerTrackTask : {}", id);
        return customerTrackTaskRepository.findOne(id);
    }

    /**
     *  Delete the  customerTrackTask by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete CustomerTrackTask : {}", id);
        customerTrackTaskRepository.delete(id);
    }

    @Override
    public CustomerTrackTask closeTask(CustomerTrackTask customerTrackTask) {

        TaskStatus finished = taskStatusRepository.findByCode("finished");
        customerTrackTask.getTask().setTaskStatus(finished);

        return customerTrackTaskRepository.save(customerTrackTask);
    }
}
