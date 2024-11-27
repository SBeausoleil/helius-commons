package systems.helius.commons.types;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

@Data
public class StudentProfile {
    @Getter(lombok.AccessLevel.NONE)
    protected final int STUDENT_ID;
    protected ComplexHuman student;
    protected School school;
    @Nullable
    private Float average;

    protected StudentProfile(int studentId, ComplexHuman student, School school) {
        this.STUDENT_ID = studentId;
        this.student = student;
        this.school = school;
    }

    public int getStudentId() {
        return STUDENT_ID;
    }
}
