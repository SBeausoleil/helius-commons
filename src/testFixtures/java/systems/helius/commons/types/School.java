package systems.helius.commons.types;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class School {
    private final int ID;
    private String name;
    private String address;

    private Map<Integer, StudentProfile> students;
    private Set<ComplexHuman> teachers;

    public School(String name, String address) {
        this(ThreadLocalRandom.current().nextInt(), name, address);
    }

    public School(int id, String name, String address) {
        this.ID = id;
        this.name = name;
        this.address = address;

        this.students = new HashMap<>();
        this.teachers = new LinkedHashSet<>();
    }

    public StudentProfile registerStudent(ComplexHuman student) {
        var profile = new StudentProfile(ThreadLocalRandom.current().nextInt(), student, this);
        this.students.put(profile.getStudentId(), profile);
        return profile;
    }
}