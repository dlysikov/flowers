package lu.luxtrust.flowers.entity.enrollment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lu.luxtrust.flowers.entity.common.AbstractEntity;
import lu.luxtrust.flowers.entity.common.ID;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "document")
@SequenceGenerator(sequenceName = "document_id", name = "document_id")
public class Document extends AbstractEntity implements ID<Long> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_id")
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String name;

    @JsonIgnore
    @Lob
    @Basic(fetch=LAZY)
    @Column(name = "file_content", nullable = false)
    private byte[] file;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "holder_id", nullable = false, updatable = false)
    private Holder holder;

    public Holder getHolder() {
        return holder;
    }

    public void setHolder(Holder holder) {
        this.holder = holder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
