package school.hei.haapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "\"event\"")
public class Event implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Ref is mandatory")
    private String ref;

    @Column(nullable = false)
    @NotBlank(message = "Name is mandatory")
    public String name;

    private Instant startingDate;
    private Instant endingDate;

    @OneToOne
    @JoinColumn(name = "responsible_id", foreignKey = @ForeignKey(name="fk_event_responsible"))
    private User responsible;

    @OneToOne
    @JoinColumn(name = "place_id", foreignKey = @ForeignKey(name="fk_event_place"))
    private Place place;
}
