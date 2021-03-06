package com.pure.service.service.impl;

import com.pure.service.domain.Contract;
import com.pure.service.domain.Customer;
import com.pure.service.domain.CustomerCard;
import com.pure.service.domain.CustomerCollectionLog;
import com.pure.service.domain.CustomerCommunicationLog;
import com.pure.service.domain.CustomerCommunicationLogType;
import com.pure.service.domain.CustomerCommunicationSchedule;
import com.pure.service.domain.CustomerCousultantAssignHistory;
import com.pure.service.domain.CustomerStatus;
import com.pure.service.domain.CustomerTrackTask;
import com.pure.service.domain.FreeClassRecord;
import com.pure.service.domain.Student;
import com.pure.service.domain.Task;
import com.pure.service.domain.TaskStatus;
import com.pure.service.domain.User;
import com.pure.service.region.RegionUtils;
import com.pure.service.repository.ContractRepository;
import com.pure.service.repository.CustomerCardRepository;
import com.pure.service.repository.CustomerCollectionLogRepository;
import com.pure.service.repository.CustomerCommunicationLogRepository;
import com.pure.service.repository.CustomerCommunicationLogTypeRepository;
import com.pure.service.repository.CustomerCommunicationScheduleRepository;
import com.pure.service.repository.CustomerCousultantAssignHistoryRepository;
import com.pure.service.repository.CustomerRepository;
import com.pure.service.repository.CustomerStatusRepository;
import com.pure.service.repository.CustomerTrackTaskRepository;
import com.pure.service.repository.NewOrderAssignHistoryRepository;
import com.pure.service.repository.StudentRepository;
import com.pure.service.repository.TaskStatusRepository;
import com.pure.service.repository.UserRepository;
import com.pure.service.security.SecurityUtils;
import com.pure.service.service.CustomerCommunicationLogQueryService;
import com.pure.service.service.CustomerQueryService;
import com.pure.service.service.CustomerService;
import com.pure.service.service.FreeClassRecordService;
import com.pure.service.service.NewOrderAssignHistoryQueryService;
import com.pure.service.service.dto.CustomerCommunicationLogCriteria;
import com.pure.service.service.dto.CustomerCriteria;
import com.pure.service.service.dto.TaskStatusEnum;
import com.pure.service.service.dto.dto.ChannelReportElement;
import com.pure.service.service.dto.dto.ChartElement;
import com.pure.service.service.dto.dto.CombinedReport;
import com.pure.service.service.dto.dto.LocationStatusReportEntity;
import com.pure.service.service.dto.dto.Overview;
import com.pure.service.service.dto.dto.ReportEntity;
import com.pure.service.service.dto.request.CustomerStatusRequest;
import com.pure.service.service.dto.request.ReportElement;
import com.pure.service.service.dto.request.StatusReportElement;
import com.pure.service.service.util.DateUtil;
import com.pure.service.task.AutoReassignCustomerTask;
import io.github.jhipster.service.filter.LongFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service Implementation for managing Customer.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    @Autowired
    private FreeClassRecordService freeClassRecordService;

    @Autowired
    private CustomerTrackTaskRepository customerTrackTaskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private CustomerStatusRepository customerStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerCommunicationScheduleRepository scheduleRepository;

    @Autowired
    private CustomerCommunicationLogRepository logRepository;

    @Autowired
    private CustomerCardRepository cardRepository;

    @Autowired
    private CustomerCollectionLogRepository collectionLogRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AutoReassignCustomerTask autoReassignCustomerTask;

    @Autowired
    private NewOrderAssignHistoryRepository newOrderAssignHistoryRepository;

    @Autowired
    private CustomerCousultantAssignHistoryRepository assignHistoryRepository;

    @Autowired
    private NewOrderAssignHistoryQueryService newOrderAssignHistoryQueryService;

    private final CustomerQueryService customerQueryService;
    private final CustomerCommunicationLogTypeRepository customerCommunicationLogTypeRepository;
    private final CustomerCommunicationLogRepository customerCommunicationLogRepository;
    private final CustomerCommunicationLogQueryService customerCommunicationLogQueryService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               CustomerQueryService customerQueryService,
                               CustomerCommunicationLogTypeRepository customerCommunicationLogTypeRepository,
                               CustomerCommunicationLogRepository customerCommunicationLogRepository,
                               CustomerCommunicationLogQueryService customerCommunicationLogQueryService) {
        this.customerRepository = customerRepository;
        this.customerQueryService = customerQueryService;
        this.customerCommunicationLogTypeRepository = customerCommunicationLogTypeRepository;
        this.customerCommunicationLogRepository = customerCommunicationLogRepository;
        this.customerCommunicationLogQueryService = customerCommunicationLogQueryService;
    }

    @Override
    public List<Customer> batchAssignCustomer(List<Customer> customers, Long userId) {

        List<CustomerCousultantAssignHistory> assignHistories = new ArrayList<>();

        List<Customer> customerList = new ArrayList<>();

        for (Customer customer : customers) {

            Customer oldCustomer = customerRepository.findOne(customer.getId());
            if (oldCustomer.getCourseConsultant() == customer.getCourseConsultant()) {
                continue;
            }

            customer.setCourseConsultantAssignDate(Instant.now());
            User newAssignee = userRepository.findOne(userId);
            customer.setCourseConsultant(newAssignee);

            customerList.add(customer);

            String olderFollowerLogin = oldCustomer.getCourseConsultant() == null ? "" : oldCustomer.getCourseConsultant().getLogin();
            String olderFollowerName = oldCustomer.getCourseConsultant() == null ? "" : oldCustomer.getCourseConsultant().getFirstName();
            String newFollowerLogin = customer.getCourseConsultant() == null ? "" : customer.getCourseConsultant().getLogin();
            String newFollowerName = customer.getCourseConsultant() == null ? "" : customer.getCourseConsultant().getFirstName();

            CustomerCousultantAssignHistory assignHistory = new CustomerCousultantAssignHistory();
            assignHistory = assignHistory.customer(customer).olderFollowerLogin(olderFollowerLogin).olderFollowerName(olderFollowerName).newFollowerLogin(newFollowerLogin).newFollowerName(newFollowerName);

            RegionUtils.setRegionAbstractAuditingRegionEntity(assignHistory);
            assignHistories.add(assignHistory);

        }

        if (!CollectionUtils.isEmpty(customerList)) {
            customerRepository.save(customerList);
        }

        if (!CollectionUtils.isEmpty(assignHistories)) {
            assignHistoryRepository.save(assignHistories);

        }

        return customerList;
    }


    /**
     * Save a customer.
     *
     * @param customer the entity to save
     * @return the persisted entity
     */
    @Override
    public Customer save(Customer customer) {
        log.debug("Request to save Customer : {}", customer);

        if (customer.getStatus() == null && customer.getId() == null) {

            CustomerStatus newOrderStatus = customerStatusRepository.findByCode("new_created");
            customer.setStatus(newOrderStatus);
        }

        //TODO Synchronize more attribute
        FreeClassRecord freeClassRecord = customer.getNewOrder();
        if (freeClassRecord != null && customer.getStatus() != null) {

            String newOrderStatus = freeClassRecord.getStatus();

            if (!newOrderStatus.equals(customer.getStatus().getName())) {
                freeClassRecord.setStatus(customer.getStatus().getName());

                freeClassRecordService.save(freeClassRecord);

                log.debug("Update new order status while the customer status is changing");
            }

        }

        return customerRepository.save(customer);
    }

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findAll(Pageable pageable) {
        log.debug("Request to get all Customers");
        return customerRepository.findAll(pageable);
    }

    /**
     * Get one customer by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Customer findOne(Long id) {
        log.debug("Request to get Customer : {}", id);
        return customerRepository.findOne(id);
    }

    /**
     * Delete the  customer by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Customer : {}", id);
        customerRepository.delete(id);
    }

    @Override
    public Customer importCustomerFromNewOrder(Long newOrderId) {

        CustomerCriteria customerCriteria = new CustomerCriteria();
        LongFilter longFilter = new LongFilter();
        longFilter.setEquals(newOrderId);
        customerCriteria.setNewOrderId(longFilter);

        List<Customer> existCustomer = customerQueryService.findByCriteria(customerCriteria);

        if (CollectionUtils.isEmpty(existCustomer)) {

            FreeClassRecord newOrder = freeClassRecordService.findOne(newOrderId);

            Customer customer = new Customer();
            customer.setName(newOrder.getPersonName());
            customer.setContactPhoneNumber(newOrder.getContactPhoneNumber());
            customer.setSalesFollower(newOrder.getSalesFollower());
            customer.setOuterReferer(newOrder.getOuterReferer());
            if (customer.getSalesFollower() != null ) {
                customer.setAssignDate(Instant.now());

                freeClassRecordService.saveFreeClassAssignHistory(newOrder, null);

            }

            customer.setChannel(newOrder.getMarketChannelCategory());
            customer.setNewOrder(newOrder);
            customer.setBirthday(newOrder.getBirthday());
            customer.setNewOrderResourceLocation(newOrder.getNewOrderResourceLocation());
            customer.setAge(newOrder.getAge());
            customer.setClassLevel(newOrder.getClassLevel());
            customer.setSchool(newOrder.getSchool());
            customer.setRegionId(newOrder.getRegionId());
            customer.setOuterReferer(newOrder.getOuterReferer());

            Customer savedCustomer = save(customer);

            CustomerCommunicationLogType newCreateOrderType = customerCommunicationLogTypeRepository.findByCode(CustomerCommunicationLogTypeEnum.new_customer_created.name());
            CustomerCommunicationLog customerCommunicationLog = new CustomerCommunicationLog();
            customerCommunicationLog.comments("客户数据录入");

            customerCommunicationLog.setLogType(newCreateOrderType);
            customerCommunicationLog.customer(savedCustomer);
            customerCommunicationLog.setRegionId(customer.getRegionId());

            CustomerCommunicationLog savedLog = customerCommunicationLogRepository.save(customerCommunicationLog);

            log.debug("Saved customer log for the new order created {}", savedLog);

            CustomerCommunicationLogCriteria customerCommunicationLogCriteria = new CustomerCommunicationLogCriteria();
            customerCommunicationLogCriteria.setFreeClassRecordId(longFilter);
            List<CustomerCommunicationLog> logs = customerCommunicationLogQueryService.findByCriteria(customerCommunicationLogCriteria);

            if (!CollectionUtils.isEmpty(logs)) {

                logs.forEach(log -> log.setCustomer(savedCustomer));
                customerCommunicationLogRepository.save(logs);
            }

            return savedCustomer;

        }

        return existCustomer.get(0);
    }

    @Override
    public void updateTrackTaskStatus(Customer customer, String lastComments) {

        customer.setLastTrackDate(Instant.now());
        customer.setLastTrackComments(lastComments);

        Integer trackedCount = customer.getTrackCount();
        if (trackedCount == null) {
            trackedCount = 1;
        } else {
            trackedCount += 1;
        }
        customer.setTrackCount(trackedCount);

        List<CustomerTrackTask> trackTasks = customerTrackTaskRepository.findByCustomer_Id(customer.getId());

        boolean stillHaveUnfinishedTask = false;
        for (CustomerTrackTask trackTask : trackTasks) {
            Task task = trackTask.getTask();

            if (task.getTaskStatus().getCode().equals(TaskStatusEnum.ongoing.name())) {

                TaskStatus ongoing = taskStatusRepository.findByCode(TaskStatusEnum.ongoing.name());
                customer.setTrackStatus(ongoing.getName());

                customerRepository.save(customer);

                stillHaveUnfinishedTask = true;
                break;

            }
        }

        if (!stillHaveUnfinishedTask) {

            TaskStatus ongoing = taskStatusRepository.findByCode(TaskStatusEnum.finished.name());
            customer.setTrackStatus(ongoing.getName());

            customerRepository.save(customer);
        }
    }

    @Override
    public List<Customer> batchSave(List<Customer> customers) {
        return customerRepository.save(customers);
    }

    @Override
    public CombinedReport getStatusReport(CustomerStatusRequest customerStatusRequest) {

        CombinedReport report = new CombinedReport();
        List<ReportEntity> reportEntities = customerRepository.searchCustomerStatusReport(customerStatusRequest.getStartDate(), customerStatusRequest.getEndDate(), RegionUtils.getRegionIdForCurrentUser());

        Map<Long, ReportElement> userBasedStatusCountMap = new HashMap<>();
        Map<String, ChartElement> codeBasedStatusCountMap = new HashMap<>();

        for (ReportEntity reportEntity : reportEntities) {
            ReportElement reportElement = userBasedStatusCountMap.get(reportEntity.getUserId());
            if (reportElement == null) {
                reportElement = new ReportElement();

                reportElement.setUserId(reportEntity.getUserId());
                reportElement.setUserName(reportEntity.getUserName());

                userBasedStatusCountMap.put(reportEntity.getUserId(), reportElement);
            }

            ChartElement chartElement = codeBasedStatusCountMap.get(reportEntity.getStatusCode());
            if (chartElement == null) {

                chartElement = new ChartElement();

                chartElement.setChartName(reportEntity.getStatusName());
                chartElement.setChartCode(reportEntity.getStatusCode());

                codeBasedStatusCountMap.put(reportEntity.getStatusCode(), chartElement);
            }

            switch (reportEntity.getStatusCode()) {

                case "new_created":
                    reportElement.setNewCreatedCount(reportEntity.getCount());

                    chartElement.getxValues().add(reportEntity.getUserName());
                    chartElement.getyValues().add(reportEntity.getCount());
                    break;
                case "Too_Young":
                    reportElement.setAgeTooSmallCount(reportEntity.getCount());

                    chartElement.getxValues().add(reportEntity.getUserName());
                    chartElement.getyValues().add(reportEntity.getCount());
                    break;
                case "Bad_Information":
                    reportElement.setErrorInformation(reportEntity.getCount());

                    chartElement.getxValues().add(reportEntity.getUserName());
                    chartElement.getyValues().add(reportEntity.getCount());
                    break;
                case "No_Willing":
                    reportElement.setNoWillingCount(reportEntity.getCount());

                    chartElement.getxValues().add(reportEntity.getUserName());
                    chartElement.getyValues().add(reportEntity.getCount());
                    break;
                case "Considering":
                    reportElement.setConsideringCount(reportEntity.getCount());

                    chartElement.getxValues().add(reportEntity.getUserName());
                    chartElement.getyValues().add(reportEntity.getCount());
                    break;
                case "meeting_schedule_made":
                    reportElement.setScheduledCount(reportEntity.getCount());

                    chartElement.getxValues().add(reportEntity.getUserName());
                    chartElement.getyValues().add(reportEntity.getCount());
                    break;
                case "deal":
                    reportElement.setDealedCount(reportEntity.getCount());

                    chartElement.getxValues().add(reportEntity.getUserName());
                    chartElement.getyValues().add(reportEntity.getCount());
                    break;
                case "visited":
                    reportElement.setVisitedCount(reportEntity.getCount());

                    chartElement.getxValues().add(reportEntity.getUserName());
                    chartElement.getyValues().add(reportEntity.getCount());
                    break;

                default:
                    break;

            }
        }

        List<ReportElement> elements = new ArrayList<>(userBasedStatusCountMap.values());
        elements.forEach(element -> {

            Integer totalCount =  element.getConsideringCount() + element.getDealedCount() + element.getErrorInformation() + element.getAgeTooSmallCount() + element.getNewCreatedCount()
                + element.getNoWillingCount() + element.getScheduledCount() +element.getVisitedCount();
            element.setTotalCount(totalCount);

            if (totalCount > 0) {
                Double finishRate = (new Double(totalCount) - element.getNewCreatedCount()) * 100 / totalCount;

                BigDecimal finishRateDecimal = new BigDecimal(finishRate);
                BigDecimal roundedDecimal = finishRateDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                element.setFinishRate(roundedDecimal.toString() + "%");
            }
        });

        report.setData(elements);

        List<ChartElement> charts = new ArrayList<>(codeBasedStatusCountMap.values());
        report.setChart(charts);

        return report;
    }

    @Override
    public List<StatusReportElement> getReferStatusReport(CustomerStatusRequest customerStatusRequest) {

        List<LocationStatusReportEntity> entities =
            customerRepository.searchReferCustomerStatusReport(customerStatusRequest.getStartDate(), customerStatusRequest.getEndDate(), RegionUtils.getRegionIdForCurrentUser());

        return generateStatusReport(entities);
    }

    @Override
    public List<StatusReportElement> getLocationStatusReport(CustomerStatusRequest customerStatusRequest) {

        List<LocationStatusReportEntity> entities =
            customerRepository.searchLocationCustomerStatusReport(customerStatusRequest.getStartDate(), customerStatusRequest.getEndDate(), RegionUtils.getRegionIdForCurrentUser());

        List<StatusReportElement> elements = generateStatusReport(entities);

        return elements;
    }

    private List<StatusReportElement> generateStatusReport(List<LocationStatusReportEntity> entities) {
        Map<String, StatusReportElement> locationBasedReportMap = new HashMap<>();
        for (LocationStatusReportEntity entity : entities) {

            StatusReportElement reportElement = locationBasedReportMap.get(entity.getLocation());
            if (reportElement == null) {
                reportElement = new StatusReportElement();

                reportElement.setLocation(entity.getLocation());
//                reportElement.setLocationId(entity.getLocationId());

                locationBasedReportMap.put(entity.getLocation(), reportElement);
            }

            switch (entity.getStatusCode()) {

                case "new_created":
                    reportElement.setNewCreatedCount(entity.getCount());
                    break;
                case "Too_Young":
                    reportElement.setAgeTooSmallCount(entity.getCount());
                    break;
                case "Bad_Information":
                    reportElement.setErrorInformation(entity.getCount());
                    break;
                case "No_Willing":
                    reportElement.setNoWillingCount(entity.getCount());
                    break;
                case "Considering":
                    reportElement.setConsideringCount(entity.getCount());
                    break;
                case "meeting_schedule_made":
                    reportElement.setScheduledCount(entity.getCount());
                    break;
                case "deal":
                    reportElement.setDealedCount(entity.getCount());
                    break;
                case "visited":
                    reportElement.setVisitedCount(entity.getCount());
                    break;
                case "No_Answer":
                    reportElement.setPhoneNoAnsweredCount(entity.getCount());
                    break;
                default:
                    break;

            }
        }

        List<StatusReportElement> elements = new ArrayList<>(locationBasedReportMap.values());
        elements.forEach(element -> {

            Integer totalCount =  element.getConsideringCount() + element.getDealedCount() + element.getErrorInformation() + element.getAgeTooSmallCount() + element.getNewCreatedCount()
                + element.getNoWillingCount() + element.getScheduledCount() + element.getVisitedCount();
            element.setTotalCount(totalCount);

            Integer validCount =  element.getConsideringCount() + element.getDealedCount()
                + element.getScheduledCount() + element.getVisitedCount();
            element.setTotalCount(totalCount);

            if (totalCount > 0) {
                Double finishRate = (new Double(totalCount) - element.getNewCreatedCount()) * 100 / totalCount;
                Double validRate = new Double(validCount) * 100 / totalCount;

                BigDecimal finishRateDecimal = new BigDecimal(finishRate);
                BigDecimal roundedDecimal = finishRateDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                element.setFinishRate(roundedDecimal.toString() + "%");

                BigDecimal validRateDecimal = new BigDecimal(validRate);
                BigDecimal roundedDecimalValid = validRateDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                element.setValidRate(roundedDecimalValid.toString() + "%");
            }
        });
        return elements;
    }

    @Override
    public Customer preloadMergedCustomer(Long oid, Long tid) {

        Customer leftCustomer = findOne(oid);
        Customer rightCustomer = findOne(tid);

        Customer mergedCustomer = new Customer();

        BeanUtils.copyProperties(leftCustomer, mergedCustomer);
        mergedCustomer.setId(null);

        String leftName = StringUtils.isEmpty(leftCustomer.getName())? "": leftCustomer.getName();
        String rightName = StringUtils.isEmpty(rightCustomer.getName())? "": rightCustomer.getName();

        mergedCustomer.setName(leftName + "/" + rightName);
        mergedCustomer.setContactPhoneNumber(leftCustomer.getContactPhoneNumber() + "/" + rightCustomer.getContactPhoneNumber());

        return mergedCustomer;
    }

    @Override
    public Customer mergeCustomer(Long originalId, Long targetId, Customer customer) {

        customer.setNewOrder(null);
        Customer finalCustomer = save(customer);

        Customer leftCustomer = findOne(originalId);
        Customer rightCustomer = findOne(targetId);

        finalCustomer.setStatus(leftCustomer.getStatus());

        mergeSchedules(leftCustomer, rightCustomer, finalCustomer);
        mergeLogs(leftCustomer, rightCustomer, finalCustomer);
        mergeTasks(leftCustomer, rightCustomer, finalCustomer);
        mergeCards(leftCustomer, rightCustomer, finalCustomer);
        mergeCollectionLogs(leftCustomer, rightCustomer, finalCustomer);
        mergeContracts(leftCustomer, rightCustomer, finalCustomer);
        mergeStudents(leftCustomer, rightCustomer, finalCustomer);

        //delete old customers
        delete(leftCustomer.getId());
        delete(rightCustomer.getId());

        saveMergeLog(finalCustomer);

        return finalCustomer;
    }

    @Override
    public void backupReport() {
        autoReassignCustomerTask.reassign();
    }

    @Override
    public List<ChannelReportElement> getVistedCustomerStatusReport(CustomerStatusRequest customerStatusRequest) {

        Long regionId = RegionUtils.getRegionIdForCurrentUser();

        List<ChannelReportElement> visitedTotalElements = customerRepository.searchChannelReport(customerStatusRequest.getStartDate(), customerStatusRequest.getEndDate(), RegionUtils.getRegionIdForCurrentUser());

        for (ChannelReportElement visitedTotalElement : visitedTotalElements) {

            Integer customerCardCount = customerRepository.getChannelCustomerCardCount(visitedTotalElement.getChannelId(),  regionId, customerStatusRequest.getStartDate(), customerStatusRequest.getEndDate());
            visitedTotalElement.setCardCount(customerCardCount);

            Integer customerContractCount = customerRepository.getChannelCustomerContractCount(visitedTotalElement.getChannelId(), regionId, customerStatusRequest.getStartDate(), customerStatusRequest.getEndDate());
            visitedTotalElement.setContractCount(customerContractCount);

            Double contractRate = new Double(customerContractCount) * 100 / visitedTotalElement.getVisitedCustomerCount();
            Double cardRate = new Double(visitedTotalElement.getCardCount()) * 100 / visitedTotalElement.getVisitedCustomerCount();

            BigDecimal finishRateDecimal = new BigDecimal(contractRate);
            BigDecimal roundedDecimal = finishRateDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            visitedTotalElement.setContractMadeRateText(roundedDecimal.toString() + "%");

            BigDecimal cardRateDecimal = new BigDecimal(cardRate);
            BigDecimal cardRoundedDecimal = cardRateDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            visitedTotalElement.setCardMadeRate(cardRoundedDecimal.toString() + "%");
        }
        return visitedTotalElements;
    }

    @Override
    public void fixupCreatedDateIssue() {

    }



    private void mergeStudents(Customer leftCustomer, Customer rightCustomer, Customer finalCustomer) {

        List<Student> leftStudents = studentRepository.findByCustomer_Id(leftCustomer.getId());
        List<Student> rightStudents = studentRepository.findByCustomer_Id(rightCustomer.getId());

        leftStudents.addAll(rightStudents);

        leftStudents.forEach(student -> student.setCustomer(finalCustomer));

        studentRepository.save(leftStudents);
    }

    private void saveMergeLog(Customer finalCustomer) {

        CustomerCommunicationLog log = new CustomerCommunicationLog();
        log.setCustomer(finalCustomer);
        log.setComments("客户信息合并");

        customerCommunicationLogRepository.save(log);
    }

    private void mergeContracts(Customer leftCustomer, Customer rightCustomer, Customer finalCustomer) {

        List<Contract> leftContracts = contractRepository.findByCustomer_Id(leftCustomer.getId());
        List<Contract> rightContracts = contractRepository.findByCustomer_Id(rightCustomer.getId());

        leftContracts.addAll(rightContracts);

        leftContracts.forEach(contract -> contract.setCustomer(finalCustomer));

        contractRepository.save(leftContracts);
    }

    private void mergeCollectionLogs(Customer leftCustomer, Customer rightCustomer, Customer finalCustomer) {

        List<CustomerCollectionLog> leftLogs = collectionLogRepository.findByCustomer_Id(leftCustomer.getId());
        List<CustomerCollectionLog> rightLogs = collectionLogRepository.findByCustomer_Id(rightCustomer.getId());

        leftLogs.addAll(rightLogs);

        leftLogs.forEach(customerCollectionLog -> customerCollectionLog.setCustomer(finalCustomer));

        collectionLogRepository.save(leftLogs);
    }


    private void mergeCards(Customer leftCustomer, Customer rightCustomer, Customer finalCustomer) {

        List<CustomerCard> leftCards = cardRepository.findByCustomer_Id(leftCustomer.getId());
        List<CustomerCard> rightCards = cardRepository.findByCustomer_Id(rightCustomer.getId());

        leftCards.addAll(rightCards);

        leftCards.forEach(customerCard -> customerCard.setCustomer(finalCustomer));
        cardRepository.save(leftCards);
    }

    private void mergeTasks(Customer leftCustomer, Customer rightCustomer, Customer finalCustomer) {
        List<CustomerTrackTask> leftTasks = customerTrackTaskRepository.findByCustomer_Id(leftCustomer.getId());
        List<CustomerTrackTask> rightTasks = customerTrackTaskRepository.findByCustomer_Id(rightCustomer.getId());

        leftTasks.addAll(rightTasks);

        leftTasks.forEach(customerTrackTask -> customerTrackTask.setCustomer(finalCustomer));

        customerTrackTaskRepository.save(leftTasks);

    }

    private void mergeLogs(Customer leftCustomer, Customer rightCustomer, Customer finalCustomer) {

        List<CustomerCommunicationLog> leftLogs = customerCommunicationLogRepository.findByCustomer_Id(leftCustomer.getId());
        List<CustomerCommunicationLog> rightLogs = customerCommunicationLogRepository.findByCustomer_Id(rightCustomer.getId());
        leftLogs.addAll(rightLogs);

        leftLogs.forEach(log -> log.setCustomer(finalCustomer));

        logRepository.save(leftLogs);
    }

    private void mergeSchedules(Customer leftCustomer, Customer rightCustomer, Customer finalCustomer) {

        List<CustomerCommunicationSchedule> leftSchdules = scheduleRepository.findByCustomer_Id(leftCustomer.getId());
        List<CustomerCommunicationSchedule> rightSchedules = scheduleRepository.findByCustomer_Id(rightCustomer.getId());

        leftSchdules.addAll(rightSchedules);
        leftSchdules.forEach(schedule -> schedule.setCustomer(finalCustomer));

        scheduleRepository.save(leftSchdules);
    }

    @Override
    public Overview getCurrentUserOverview() {

        Instant monthBeginning = DateUtil.getFirstSecondOfMonth();
        Instant monthEnding = DateUtil.getLastSecondOfMonth();

        String currentLogin = SecurityUtils.getCurrentUserLogin();
        User currentUser = userRepository.findOneByLogin(currentLogin).get();

        Overview overview = customerRepository.searchCurrentUserOverview(currentUser.getId(), monthEnding, monthBeginning, RegionUtils.getRegionIdForCurrentUser());

        return overview;
    }

    @Override
    public void updateCustomerStatusForNewSchedule( CustomerCommunicationSchedule schedule) {

        Customer customer = schedule.getCustomer();

        CustomerStatus scheduled = customerStatusRepository.findByCode("meeting_schedule_made");
        customer.setStatus(scheduled);
        customer.setNextScheduleComments(schedule.getComments());
        customer.setNextScheduleDate(schedule.getSceduleDate());

        save(customer);
    }

    @Override
    public void fixAssignDateIssue() {

        List<Customer> customers = customerRepository.findByAssignDateIsNullAndSalesFollowerIsNotNull();

        int count = 0;
        int goodcount = 0;
        for (Customer customer : customers) {

            customer.setAssignDate(customer.getCreatedDate());
//            Long newOrderId = customer.getNewOrder().getId();
//
//            NewOrderAssignHistoryCriteria historyCriteria = new NewOrderAssignHistoryCriteria();
//
//            LongFilter longFilter = new LongFilter();
//            longFilter.setEquals(newOrderId);
//
//            historyCriteria.setNewOrderId(longFilter);
//
//            List<NewOrderAssignHistory> histories = newOrderAssignHistoryQueryService.findByCriteria(historyCriteria);
//            if (CollectionUtils.isEmpty(histories)) {
//                count ++;
//                log.debug("没有分配记录+1");
//                continue;
//            }
//
//            NewOrderAssignHistory newOrderAssignHistory = histories.get(0);
//
//            System.out.println(newOrderAssignHistory.getCreatedDate().toString());
//
//            log.debug("Create date {} ", newOrderAssignHistory.getCreatedDate());
//            goodcount ++;
        }

        customerRepository.save(customers);

        log.debug("未找到分配历史记录数量 " + count + " 有历史记录的" + goodcount + "条");

    }



}
