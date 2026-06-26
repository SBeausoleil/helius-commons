package systems.helius.commons.types;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Course {
    private int courseId;
    private String title;
    private String description;
    private List<String> tags;
    private String[] prerequisites;
    private Map<String, Integer> gradingCriteria;

    public Course(int courseId, String title, String description) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.tags = new ArrayList<>();
        this.prerequisites = new String[0];
        this.gradingCriteria = new HashMap<>();
    }
}
