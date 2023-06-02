package BGU.Group13B.frontEnd;

import java.io.Serializable;
import java.util.Objects;


public class GradeId implements Serializable {
    private int id;
    private int grade;

    public GradeId() {
    }

    public GradeId(int id, int grade) {
        this.id = id;
        this.grade = grade;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    // Constructors, getters, and setters
    // Make sure to override equals() and hashCode() methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeId gradeId = (GradeId) o;
        return id == gradeId.id && grade == gradeId.grade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, grade);
    }
}
