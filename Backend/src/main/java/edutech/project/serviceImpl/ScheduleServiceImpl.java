package edutech.project.serviceImpl;

import edutech.project.dto.request.ScheduleRequestDTO;
import edutech.project.dto.response.ScheduleResponseDTO;
import edutech.project.exception.DuplicateResourceException;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.Course;
import edutech.project.model.Schedule;
import edutech.project.repository.CourseRepo;
import edutech.project.repository.ScheduleRepo;
import edutech.project.service.CourseService;
import edutech.project.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Override
    public ScheduleResponseDTO createSchedule(ScheduleRequestDTO request) {
        Course course = courseRepo.findById(request.getCourseId()).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        String roomNumber = request.getRoomNumber().trim().toUpperCase();

        //COURSE SCHEDULE CONFLICT

        List<Schedule> courseSchedules = scheduleRepo.findByCourse(course);
        for (Schedule schedule : courseSchedules) {
            if (schedule.getDay() == request.getDay()) {
                boolean overlap = request.getStartTime().isBefore(schedule.getEndTime()) && request.getEndTime().isAfter(schedule.getStartTime());
                if (overlap) {
                    throw new DuplicateResourceException("This course already has another class during this time.");
                }
            }
        }

        // ROOM CONFLICT

        List<Schedule> roomSchedules = scheduleRepo.findByDayAndRoomNumber(request.getDay(), roomNumber);
        for (Schedule schedule : roomSchedules) {
            boolean overlap = request.getStartTime().isBefore(schedule.getEndTime()) && request.getEndTime().isAfter(schedule.getStartTime());
            if (overlap) {
                throw new DuplicateResourceException("Room is already occupied during this time.");
            }
        }

        // PROFESSOR CONFLICT

        List<Schedule> professorSchedules = scheduleRepo.findByDayAndCourse_Professor(request.getDay(),course.getProfessor());
        for (Schedule schedule : professorSchedules) {
            boolean overlap = request.getStartTime().isBefore(schedule.getEndTime()) && request.getEndTime().isAfter(schedule.getStartTime());
            if (overlap) {
                throw new DuplicateResourceException("Professor already has another class during this time.");
            }
        }
        Schedule schedule = new Schedule();
        schedule.setCourse(course);
        schedule.setDay(request.getDay());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setRoomNumber(roomNumber);
        Schedule saved = scheduleRepo.save(schedule);
        return mapToResponse(saved);
    }
    @Override
    public ScheduleResponseDTO getScheduleById(Long scheduleId) {
        Schedule schedule = scheduleRepo.findById(scheduleId).orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        return mapToResponse(schedule);
    }
    @Override
    public List<ScheduleResponseDTO> getScheduleByCourse(Long courseId) {
        Course course = courseRepo.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        List<Schedule> schedules = scheduleRepo.findByCourse(course);
        List<ScheduleResponseDTO> response = new ArrayList<>();
        for (Schedule schedule : schedules) {
            response.add(mapToResponse(schedule));
        }
        return response;
    }
    @Override
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepo.findById(scheduleId).orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        scheduleRepo.delete(schedule);
    }
    private ScheduleResponseDTO mapToResponse(Schedule schedule) {
        return ScheduleResponseDTO.builder()
                .scheduleId(schedule.getScheduleId())
                .courseId(schedule.getCourse().getCourseId())
                .courseName(schedule.getCourse().getCourseName())
                .day(schedule.getDay())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .roomNumber(schedule.getRoomNumber())
                .build();
    }

    @Override
    public List<ScheduleResponseDTO> getScheduleByStudent(Long studentId, Long academicPeriodId) {
        List<Schedule> schedules = scheduleRepo.findScheduleByStudentId(studentId);
        List<ScheduleResponseDTO> response = new java.util.ArrayList<>();
        for (Schedule schedule : schedules) {
            Course course = schedule.getCourse();
            if (academicPeriodId == null || (course.getAcademicPeriod() != null && course.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                response.add(mapToResponse(schedule));
            }
        }
        return response;
    }
}
