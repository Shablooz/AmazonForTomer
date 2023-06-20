package BGU.Group13B.frontEnd;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Entity
public class Preson {

    @Id
    private int id;
    private String name;




    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(name = "list_grade",
            joinColumns = {@JoinColumn(name = "preson_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "grade_id", referencedColumnName = "id"),
                    @JoinColumn(name = "grade_grade", referencedColumnName = "grade")})
    private List<Grade> grades;



    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(name = "Person_momo",
            joinColumns = {@JoinColumn(name = "preson_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "grade_id", referencedColumnName = "id"),
                    @JoinColumn(name = "grade_grade", referencedColumnName = "grade")})
    //@MapKey(name = "id")
    @MapKeyColumn(name = "my_column")
    private Map<Integer, Grade> reviews;

    public Preson(int id, String name) {
        this.id = id;
        this.name = name;
        this.grades = new ArrayList<>();
        this.reviews=new ConcurrentHashMap<>();
    }
    public List<Grade> getGrades() {
        return grades;
    }

    public Map<Integer, Grade> getReviews() {
        return reviews;
    }

    public Preson() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //add grade
    public void addGrade(Grade grade){
        grades.add(grade);

    }
    //remove grade
    public void removeGrade(Grade grade){
        grades.remove(grade);
    }
    //add review
    public void addReview(Grade grade,int userId){
        reviews.put(userId,grade);
    }
}
