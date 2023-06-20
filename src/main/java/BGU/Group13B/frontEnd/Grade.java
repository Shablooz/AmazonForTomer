package BGU.Group13B.frontEnd;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

@Entity
@IdClass(GradeId.class)
public class Grade {
    @Id
    int id;

    @Id
    int grade;

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

    public Grade() {
    }

    public Grade(int id, int grade) {
        this.id = id;
        this.grade = grade;
    }
}
