package edutech.project.service;

import edutech.project.model.AcademicPeriod;
import java.util.List;

public interface AcademicPeriodService {
    List<AcademicPeriod> getAllPeriods();
    AcademicPeriod getActivePeriod();
    AcademicPeriod getPeriodById(Long id);
    AcademicPeriod createPeriod(String year, String half, String status);
    void advancePeriod();
}
