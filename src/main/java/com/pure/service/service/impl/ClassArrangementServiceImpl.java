package com.pure.service.service.impl;

import com.pure.service.domain.ClassArrangement;
import com.pure.service.domain.ClassArrangementRule;
import com.pure.service.domain.ClassArrangementStatus;
import com.pure.service.domain.ClassRoom;
import com.pure.service.domain.StudentClass;
import com.pure.service.domain.StudentClassLog;
import com.pure.service.region.RegionIdStorage;
import com.pure.service.region.RegionUtils;
import com.pure.service.repository.ClassArrangementRepository;
import com.pure.service.repository.ClassArrangementRuleRepository;
import com.pure.service.repository.ClassArrangementStatusRepository;
import com.pure.service.repository.ClassRoomRepository;
import com.pure.service.repository.ProductRepository;
import com.pure.service.repository.StudentAbsenceLogRepository;
import com.pure.service.repository.StudentClassLogRepository;
import com.pure.service.repository.StudentClassRepository;
import com.pure.service.repository.StudentFrozenArrangementRepository;
import com.pure.service.repository.StudentLeaveRepository;
import com.pure.service.service.ClassArrangementQueryService;
import com.pure.service.service.ClassArrangementService;
import com.pure.service.service.StudentClassLogQueryService;
import com.pure.service.service.StudentClassQueryService;
import com.pure.service.service.dto.ClassArrangementCriteria;
import com.pure.service.service.dto.StudentClassCriteria;
import com.pure.service.service.dto.StudentClassLogCriteria;
import com.pure.service.service.dto.dto.ClassArrangementWeekElement;
import com.pure.service.service.dto.dto.ClassSchedule;
import com.pure.service.service.dto.dto.ClassroomDto;
import com.pure.service.service.dto.request.BatchReassignClassArrangement;
import com.pure.service.service.dto.request.CustomerStatusRequest;
import com.pure.service.service.dto.request.StudentClassArrangementsRequest;
import com.pure.service.service.util.DateUtil;
import io.github.jhipster.service.filter.InstantFilter;
import io.github.jhipster.service.filter.LongFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Service Implementation for managing ClassArrangement.
 */
@Service
@Transactional
public class ClassArrangementServiceImpl implements ClassArrangementService {

    private final Logger log = LoggerFactory.getLogger(ClassArrangementServiceImpl.class);

    private final ClassArrangementRepository classArrangementRepository;

    @Autowired
    private ClassArrangementRuleRepository ruleRepository;

    @Autowired
    private ClassArrangementStatusRepository statusRepository;

    @Autowired
    private ClassRoomRepository classRoomRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StudentClassLogQueryService studentClassLogQueryService;

    @Autowired
    private StudentClassRepository studentClassRepository;

    @Autowired
    private StudentClassQueryService studentClassQueryService;

    @Autowired
    private ClassArrangementQueryService classArrangementQueryService;

    @Autowired
    private StudentLeaveRepository studentLeaveRepository;

    @Autowired
    private StudentFrozenArrangementRepository studentFrozenArrangementRepository;

    @Autowired
    private StudentAbsenceLogRepository studentAbsenceLogRepository;

    @Autowired
    private StudentClassLogRepository studentClassLogRepository;

    public ClassArrangementServiceImpl(ClassArrangementRepository classArrangementRepository) {
        this.classArrangementRepository = classArrangementRepository;
    }

    @Override
    public void reassignClassArrangements(BatchReassignClassArrangement request) {

        List<ClassArrangement> arrangements = new ArrayList<>();
        List<ClassArrangement> arrangementList = request.getArrangements();

        for (ClassArrangement classArrangement : arrangementList) {

            StudentClassLogCriteria studentClassLogCriteria = new StudentClassLogCriteria();

            LongFilter arrangementId = new LongFilter();
            arrangementId.setEquals(classArrangement.getId());

            studentClassLogCriteria.setArrangementId(arrangementId);

            List<StudentClassLog> logs = studentClassLogQueryService.findByCriteria(studentClassLogCriteria);
            if (CollectionUtils.isEmpty(logs)) {

                classArrangement.setClazz(request.getNewClass());

                arrangements.add(classArrangement);
            }

            if (!CollectionUtils.isEmpty(arrangements)) {

                classArrangementRepository.save(arrangements);
            }

        }
    }

    @Override
    public void deleteClassArrangements(BatchReassignClassArrangement request) {
        List<ClassArrangement> arrangementList = request.getArrangements();
        List<ClassArrangement> arrangements = new ArrayList<>();

        for (ClassArrangement classArrangement : arrangementList) {

            Integer studentClassLogCount = studentClassLogRepository.getStudentClassLogCount(classArrangement.getId());
            if (studentClassLogCount > 0) {
                continue;
            }

            Integer studentLeaveCount = studentLeaveRepository.getStudentLeaveCountByArrangement(classArrangement.getId());
            if (studentLeaveCount > 0) {
                continue;
            }

            Integer studentFrozenCount = studentFrozenArrangementRepository.getStudentFrozenArrangementCount(classArrangement.getId());

            if (studentFrozenCount > 0) {
                continue;
            }

            Integer studentAbsenceLogCount = studentAbsenceLogRepository.getStudentAbsenceLogCount(classArrangement.getId());
            if (studentAbsenceLogCount > 0) {
                continue;
            }

            arrangements.add(classArrangement);
        }

        if (!CollectionUtils.isEmpty(arrangements)) {

            classArrangementRepository.delete(arrangements);
        }
    }

    @Override
    public List<ClassArrangement> findStudentArrangements(StudentClassArrangementsRequest request) {

        List<ClassArrangement> classArrangements = new ArrayList<>();

        StudentClassCriteria studentClassCriteria = new StudentClassCriteria();
        LongFilter studentIdFilter = new LongFilter();
        studentIdFilter.setEquals(request.getStudentId());

        studentClassCriteria.setStudentId(studentIdFilter);

        List<StudentClass> studentClasses = studentClassQueryService.findByCriteria(studentClassCriteria);
        if (CollectionUtils.isEmpty(studentClasses)) {
            return new ArrayList<>();
        }

        for (StudentClass studentClass : studentClasses) {
            Long classId = studentClass.getProduct().getId();

            ClassArrangementCriteria classArrangementCriteria = new ClassArrangementCriteria();

            LongFilter classIdFilter = new LongFilter();
            classIdFilter.setEquals(classId);
            classArrangementCriteria.setClazzId(classIdFilter);

            InstantFilter instantFilter = new InstantFilter();
            instantFilter.setLessOrEqualThan(request.getEndDate());
            instantFilter.setGreaterOrEqualThan(request.getStartDate());

            classArrangementCriteria.setStartDate(instantFilter);

            List<ClassArrangement> classArrangementList = classArrangementQueryService.findByCriteria(classArrangementCriteria);

            if (CollectionUtils.isEmpty(classArrangementList)) {
                continue;
            }

            classArrangements.addAll(classArrangementList);
        }

        return classArrangements;
    }

    /**
     * Save a classArrangement.
     *
     * @param classArrangement the entity to save
     * @return the persisted entity
     */
    @Override
    public ClassArrangement save(ClassArrangement classArrangement) {
        log.debug("Request to save ClassArrangement : {}", classArrangement);
        return classArrangementRepository.save(classArrangement);
    }

    /**
     * Get all the classArrangements.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ClassArrangement> findAll(Pageable pageable) {
        log.debug("Request to get all ClassArrangements");
        return classArrangementRepository.findAll(pageable);
    }

    /**
     * Get one classArrangement by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ClassArrangement findOne(Long id) {
        log.debug("Request to get ClassArrangement : {}", id);
        return classArrangementRepository.findOne(id);
    }

    /**
     * Delete the  classArrangement by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ClassArrangement : {}", id);
        classArrangementRepository.delete(id);
    }

    @Override
    public void createClassArrangementsByRule(Long id) {

        ClassArrangementRule rule = ruleRepository.findOne(id);

        switch (rule.getLoopWay().getCode()) {

            case "perWeek":
                generateArrangementWeekly(rule);
                break;
            default:
                break;
        }
    }

    @Override
    public List<ClassSchedule> searchSchedulesInRange(CustomerStatusRequest customerStatusRequest) {
        Long regionId = Long.valueOf(RegionIdStorage.getRegionIdContext());
        return classArrangementRepository.getAllSchedulesByRange(customerStatusRequest.getStartDate(), customerStatusRequest.getEndDate(), regionId);
    }

    private static Map<Integer, String> gapWeekdayNameMap = new HashMap<>();
    private static Map<TimePeriod, List<ClassSchedule>> timePeriodListMap = new HashMap<>();

    static {
        gapWeekdayNameMap.put(0, "星期一");
        gapWeekdayNameMap.put(1, "星期二");
        gapWeekdayNameMap.put(2, "星期三");
        gapWeekdayNameMap.put(3, "星期四");
        gapWeekdayNameMap.put(4, "星期五");
        gapWeekdayNameMap.put(5, "星期六");
        gapWeekdayNameMap.put(6, "星期日");
    }


    @Override
    public List<ClassArrangementWeekElement> getArrangementsInCurrentWeek() {

        List<ClassArrangementWeekElement> elements = new ArrayList<>();
        List<ClassRoom> classRooms = classRoomRepository.findByRegionId(RegionUtils.getRegionIdForCurrentUser());

        Instant mondayStart = DateUtil.getCurrentMondayStartSecond();
        int dayGap = 0;

        for (; dayGap < 7; dayGap++) {

            Instant starting = mondayStart.plus(dayGap, ChronoUnit.DAYS);
            Instant ending = mondayStart.plus(dayGap + 1, ChronoUnit.DAYS).minusSeconds(1);

            ClassArrangementWeekElement singledDay = new ClassArrangementWeekElement();
            String weekdayName = gapWeekdayNameMap.get(dayGap);
            singledDay.setWeekdayName(weekdayName);

            for (int i = 0; i < classRooms.size(); i ++) {
                //add rest marks, fixed array as class room
                ClassSchedule rest = new ClassSchedule();
                rest.setClassName("休");
                rest.setClickable(false);
                singledDay.getMoonRest().add(rest);
                singledDay.getAfternoonRest().add(rest);

                ClassroomDto classroomDto = new ClassroomDto();
                classroomDto.setId(classRooms.get(i).getId());
                classroomDto.setName(classRooms.get(i).getName());

                singledDay.getClassrooms().add(classroomDto);
            }

            timePeriodListMap.clear();

            timePeriodListMap.put(new TimePeriod(DateUtil.getInstantWithSpecialHourMinutes(starting, "9", "0"),
                DateUtil.getInstantWithSpecialHourMinutes(starting, "10", "30")), singledDay.getMorningFirstHalf());
            timePeriodListMap.put(new TimePeriod(DateUtil.getInstantWithSpecialHourMinutes(starting, "10", "45"),
                DateUtil.getInstantWithSpecialHourMinutes(starting, "12", "15")), singledDay.getMorningSecondHalf());
            timePeriodListMap.put(new TimePeriod(DateUtil.getInstantWithSpecialHourMinutes(starting, "13", "0"),
                DateUtil.getInstantWithSpecialHourMinutes(starting, "14", "30")), singledDay.getAfternoonFirstHalf());
            timePeriodListMap.put(new TimePeriod(DateUtil.getInstantWithSpecialHourMinutes(starting, "14", "45"),
                DateUtil.getInstantWithSpecialHourMinutes(starting, "16", "15")), singledDay.getAfternoonSecondHalf());
            timePeriodListMap.put(new TimePeriod(DateUtil.getInstantWithSpecialHourMinutes(starting, "16", "30"),
                DateUtil.getInstantWithSpecialHourMinutes(starting, "18", "00")), singledDay.getAfternoonThiredHalf());
            timePeriodListMap.put(new TimePeriod(DateUtil.getInstantWithSpecialHourMinutes(starting, "18", "30"),
                DateUtil.getInstantWithSpecialHourMinutes(starting, "20", "00")), singledDay.getEvenning());

            Long regionId = Long.valueOf(RegionIdStorage.getRegionIdContext());
            //find all the schedules in the day.
            List<ClassSchedule> schedules = classArrangementRepository.getAllSchedulesByRange(starting, ending, regionId);

            //find the schedules for the room
            for (ClassRoom classRoom : classRooms) {
                List<ClassSchedule> scheduleListInClassRoom = findClassInClassRoom(classRoom.getName(), schedules);

                boolean foundTeacher = false;
                for (Map.Entry<TimePeriod, List<ClassSchedule>> timePeriodListEntry : timePeriodListMap.entrySet()) {

                    TimePeriod timePeriod = timePeriodListEntry.getKey();
                    List<ClassSchedule> classNameElements = timePeriodListEntry.getValue();

                    ClassSchedule periodSchedule = findClassInRange(scheduleListInClassRoom, timePeriod.getStart(), timePeriod.getEnd());

                    if (periodSchedule == null) {
                        //TODO 寻找待开班
                        classNameElements.add(new ClassSchedule());
                    } else {

                        periodSchedule.setClickable(true);

                        Integer studentCount = studentClassRepository.getStudentCountInClass(periodSchedule.getClassId());
                        periodSchedule.setStudentCount(studentCount);

                        classNameElements.add(periodSchedule);

                        if (!foundTeacher) {

                            singledDay.getTeachers().add(periodSchedule.getTeacherName());
                            singledDay.getCourses().add(periodSchedule.getCourseName());
                        }
                        foundTeacher = true;

                    }
                }

                if (!foundTeacher) {
                    singledDay.getTeachers().add("无");
                    singledDay.getCourses().add("无");
                }
            }

            elements.add(singledDay);
        }

        return elements;
    }

    @Override
    public void createClassSchedule(ClassArrangementRule rule) {

        generateArrangementWeekly(rule);

    }

    @Override
    public void fixupClassArrangements() {

        List<ClassArrangement> arrangementList = classArrangementRepository.findAll();

        for (ClassArrangement classArrangement : arrangementList) {
            Instant start = classArrangement.getStartDate().plus(8, ChronoUnit.HOURS);
            Instant end = classArrangement.getEndDate().plus(8, ChronoUnit.HOURS);

            classArrangement.setStartDate(start);
            classArrangement.setEndDate(end);

            classArrangementRepository.save(classArrangement);
        }
    }



    private ClassSchedule findClassInRange(List<ClassSchedule> roomClasses, Instant classStart, Instant classEnd) {

        for (ClassSchedule roomClass : roomClasses) {

            Instant correctedTime = roomClass.getStart();
            if (correctedTime.isAfter(classStart.minusSeconds(1)) && correctedTime.isBefore(classEnd)) {
                return roomClass;
            }
        }

        return null;

    }

    private List<ClassSchedule> findClassInClassRoom(String classRoomName, List<ClassSchedule> schedules) {

        return schedules.stream().filter(schedule -> (schedule.getClassroomName().equals(classRoomName))).collect(Collectors.toList());
    }

    private void generateArrangementWeekly(ClassArrangementRule rule) {

        Instant startDate = rule.getEstimateStartDate();
        Instant endDate = rule.getEstimateEndDate();

        Objects.nonNull(startDate);
        Objects.nonNull(endDate);

        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("起始时间错误，开始时间应该比结束时间早");
        }

        Long regionId = Long.valueOf(RegionIdStorage.getRegionIdContext());
        List<Instant> countDays = DateUtil.getCountWeekdayInRange(startDate, endDate, rule.getCountNumber().getValue(), rule.getEstimateStartTime());

        log.debug("Generating arrangements {} ", countDays);

        List<ClassArrangement> classArrangements = new ArrayList<>();
        countDays.forEach(day -> {

            LocalDateTime endDateLocalDateTime = LocalDateTime.ofInstant(day, DateUtil.defaultShanghaiZoneId);
            String[] hourMinutes = rule.getEstimateEndTime().split(":");
            endDateLocalDateTime = endDateLocalDateTime.withHour(Integer.valueOf(hourMinutes[0])).withMinute(Integer.valueOf(hourMinutes[1]));

            ClassArrangement arrangement = new ClassArrangement();
            arrangement = arrangement.clazz(rule.getTargetClass())
                .startDate(day)
                .endDate(endDateLocalDateTime.toInstant(ZoneOffset.ofHours(8)))
                .planedTeacher(rule.getTargetClass().getTeacher());

            arrangement.setConsumeClassCount(rule.getConsumeClassCount());
            arrangement.setRegionId(regionId);

            ClassArrangementStatus initStatus = statusRepository.findByCode("notTaken");
            arrangement.setStatus(initStatus);

            if (!hasDuplicateArrangement(arrangement)) {
                classArrangements.add(arrangement);
            }
        });

        log.debug("Generate classes: {}", classArrangements);

        classArrangementRepository.save(classArrangements);

    }

    private boolean hasDuplicateArrangement(ClassArrangement classArrangement) {

        log.debug("Checking if the arrangement exists {} ", classArrangement);

        List<ClassArrangement> existedArrangements = classArrangementRepository.findByStartDateAndEndDateAndClazz_Id(classArrangement.getStartDate(), classArrangement.getEndDate(), classArrangement.getClazz().getId());

        return !CollectionUtils.isEmpty(existedArrangements);
    }

    private class TimePeriod {

        public TimePeriod(Instant start, Instant end) {
            this.start = start;
            this.end = end;
        }

        private Instant start;
        private Instant end;

        public Instant getStart() {
            return start;
        }

        public void setStart(Instant start) {
            this.start = start;
        }

        public Instant getEnd() {
            return end;
        }

        public void setEnd(Instant end) {
            this.end = end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TimePeriod)) return false;
            TimePeriod that = (TimePeriod) o;
            return Objects.equals(getStart(), that.getStart()) &&
                Objects.equals(getEnd(), that.getEnd());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getStart(), getEnd());
        }
    }

}
