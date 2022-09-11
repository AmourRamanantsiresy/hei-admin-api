package school.hei.haapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "\"course\"")
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Ref is mandatory")
    private String ref;
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Name is mandatory")
    private String name;
    @Column(nullable = false)
    private Integer credits;
    @Column(nullable = false)
    private Integer total_hours;
}
