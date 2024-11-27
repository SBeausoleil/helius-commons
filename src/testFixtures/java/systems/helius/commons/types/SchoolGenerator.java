package systems.helius.commons.types;

import com.sb.factorium.FakerGenerator;

import java.util.concurrent.ThreadLocalRandom;

public class SchoolGenerator extends FakerGenerator<School> {
    private static ComplexHumanGenerator adultGenerator = new ComplexHumanGenerator();
    private static HumanGenerator humanGenerator = new HumanGenerator();

    @Override
    protected School make() {
        var school = new School(faker.university().name(), faker.address().fullAddress());
        school.getTeachers().add(adultGenerator.generate());
        return school;
    }

    public void addTeachers(School school, int nTeachers) {
        for (ComplexHuman teacher : adultGenerator.generate(nTeachers)) {
            school.getTeachers().add(teacher);
        }
    }

    public void addStudents(School school, int nStudents) {
        for (ComplexHuman student : humanGenerator.generate(nStudents)) {
            StudentProfile profile = school.registerStudent(student);
            if (ThreadLocalRandom.current().nextFloat() > 0.25f) {
                profile.setAverage(ThreadLocalRandom.current().nextFloat());
            }
        }
    }
}
