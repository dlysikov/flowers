package lu.luxtrust.flowers.entity.enrollment;

import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.validation.ValidationGroups;
import lu.luxtrust.flowers.validation.annotation.Email;
import lu.luxtrust.flowers.validation.annotation.GTCAccepted;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.List;

import static lu.luxtrust.flowers.validation.ValidationGroups.*;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "eseal_order", indexes = {
        @Index(name = "org_unit_index", columnList = "organisational_unit"),
})
@GTCAccepted(groups = OrderSentToLRS.class)
public class ESealOrder extends Order {

    @Length(max = 10)
    @Column(name = "organisational_unit")
    private String organisationalUnit;

    @NotEmpty(groups = OrderSentToLRS.class)
    @ManyToMany
    @JoinTable(name = "ref_eseal_order_user", joinColumns = {
            @JoinColumn(name = "order_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    private List<User> eSealManagers;

    @NotEmpty(groups = OrderSentToLRS.class)
    @Email
    @Length(max = 100)
    @Column(name = "eseal_administrator_email")
    private String eSealAdministratorEmail;

    public String getOrganisationalUnit() {
        return organisationalUnit;
    }

    public void setOrganisationalUnit(String organisationalUnit) {
        this.organisationalUnit = organisationalUnit;
    }

    public List<User> geteSealManagers() {
        return eSealManagers;
    }

    public void seteSealManagers(List<User> eSealManagers) {
        this.eSealManagers = eSealManagers;
    }

    public String geteSealAdministratorEmail() {
        return eSealAdministratorEmail;
    }

    public void seteSealAdministratorEmail(String eSealAdministratorEmail) {
        this.eSealAdministratorEmail = eSealAdministratorEmail;
    }
}
