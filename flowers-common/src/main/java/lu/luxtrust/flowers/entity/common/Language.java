package lu.luxtrust.flowers.entity.common;

import javax.persistence.*;

@Entity
@Table(name = "language")
@SequenceGenerator(sequenceName = "language_id", name = "language_id")
public class Language implements ID<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "language_id")
    private Long id;

    @Column(name = "two_chars_code", nullable = false, unique = true)
    private String twoCharsCode;

    @Column(name = "use_by_default")
    private Boolean useByDefault;

    public Boolean getUseByDefault() {
        return useByDefault;
    }

    public void setUseByDefault(Boolean useByDefault) {
        this.useByDefault = useByDefault;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTwoCharsCode() {
        return twoCharsCode;
    }

    public void setTwoCharsCode(String twoCharsCode) {
        this.twoCharsCode = twoCharsCode;
    }
}
