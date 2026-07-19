package edutech.project.util;

import org.springframework.stereotype.Component;
@Component
public class GradeUtil {
    public String calculateLetterGrade(Double percentage) {
        if (percentage == null) return "F";
        if (percentage >= 90)
            return "O";
        if (percentage >= 80)
            return "A+";
        if (percentage >= 70)
            return "A";
        if (percentage >= 60)
            return "B+";
        if (percentage >= 50)
            return "B";
        if (percentage >= 40)
            return "C";
        return "F";
    }
}

