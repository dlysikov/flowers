package lu.luxtrust.flowers.properties;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Map;

@ConfigurationProperties(prefix = "notification")
@Validated
public class NotificationProperties {
    @NotNull
    private URL appBaseLink;

    @NotNull
    private NotificationConfig endUser;

    @NotNull
    private NotificationConfig issuerF2F;

    @NotNull
    private NotificationConfig issuerRemoteId;

    @NotNull
    private NotificationConfig issuerCsdRequired;

    @NotNull
    private NotificationConfig issuerDiaRequired;

    @NotNull
    private NotificationConfig endUserInviteToActivate;

    @NotNull
    private NotificationConfig adminNoDiasForUnit;

    @NotNull
    private NotificationConfig adminNoCsds;

    @NotNull
    private NotificationConfig esealAdministratorAssigned;

    @NotNull
    private NotificationConfig esealAdministratorRemoved;

    @NotNull
    private NotificationConfig esealAdministratorChanged;

    @NotNull
    private NotificationConfig esealManagerEsealStatusChanged;

    @NotNull
    private NotificationConfig esealAdministratorEsealStatusChanged;

    @NotNull
    private NotificationConfig esealHasToBeActivated;

    @NotNull
    private NotificationConfig esealManagerRemoved;

    @NotNull
    private NotificationConfig esealManagerAssigned;

    @NotNull
    private NotificationConfig esealAdministratorManagersChanged;

    public NotificationConfig getIssuerRemoteId() {
        return issuerRemoteId;
    }

    public NotificationConfig getEndUserInviteToActivate() {
        return endUserInviteToActivate;
    }

    public void setEndUserInviteToActivate(NotificationConfig endUserInviteToActivate) {
        this.endUserInviteToActivate = endUserInviteToActivate;
    }

    public void setIssuerRemoteId(NotificationConfig issuerRemoteId) {
        this.issuerRemoteId = issuerRemoteId;
    }

    public NotificationConfig getIssuerF2F() {
        return issuerF2F;
    }

    public void setIssuerF2F(NotificationConfig issuerF2F) {
        this.issuerF2F = issuerF2F;
    }

    public NotificationConfig getEndUser() {
        return endUser;
    }

    public void setEndUser(NotificationConfig endUser) {
        this.endUser = endUser;
    }

    public NotificationConfig getIssuerCsdRequired() {
        return issuerCsdRequired;
    }

    public void setIssuerCsdRequired(NotificationConfig issuerCsdRequired) {
        this.issuerCsdRequired = issuerCsdRequired;
    }

    public NotificationConfig getIssuerDiaRequired() {
        return issuerDiaRequired;
    }

    public void setIssuerDiaRequired(NotificationConfig issuerDiaRequired) {
        this.issuerDiaRequired = issuerDiaRequired;
    }

    public NotificationConfig getAdminNoDiasForUnit() {
        return adminNoDiasForUnit;
    }

    public void setAdminNoDiasForUnit(NotificationConfig adminNoDiasForUnit) {
        this.adminNoDiasForUnit = adminNoDiasForUnit;
    }

    public NotificationConfig getAdminNoCsds() {
        return adminNoCsds;
    }

    public void setAdminNoCsds(NotificationConfig adminNoCsds) {
        this.adminNoCsds = adminNoCsds;
    }

    public NotificationConfig getEsealAdministratorAssigned() {
        return esealAdministratorAssigned;
    }

    public void setEsealAdministratorAssigned(NotificationConfig esealAdministratorAssigned) {
        this.esealAdministratorAssigned = esealAdministratorAssigned;
    }

    public NotificationConfig getEsealAdministratorRemoved() {
        return esealAdministratorRemoved;
    }

    public void setEsealAdministratorRemoved(NotificationConfig esealAdministratorRemoved) {
        this.esealAdministratorRemoved = esealAdministratorRemoved;
    }

    public NotificationConfig getEsealAdministratorChanged() {
        return esealAdministratorChanged;
    }

    public void setEsealAdministratorChanged(NotificationConfig esealAdministratorChanged) {
        this.esealAdministratorChanged = esealAdministratorChanged;
    }

    public URL getAppBaseLink() {
        return appBaseLink;
    }

    public void setAppBaseLink(URL appBaseLink) {
        this.appBaseLink = appBaseLink;
    }

    public NotificationConfig getEsealManagerEsealStatusChanged() {
        return esealManagerEsealStatusChanged;
    }

    public void setEsealManagerEsealStatusChanged(NotificationConfig esealManagerEsealStatusChanged) {
        this.esealManagerEsealStatusChanged = esealManagerEsealStatusChanged;
    }

    public NotificationConfig getEsealAdministratorEsealStatusChanged() {
        return esealAdministratorEsealStatusChanged;
    }

    public void setEsealAdministratorEsealStatusChanged(NotificationConfig esealAdministratorEsealStatusChanged) {
        this.esealAdministratorEsealStatusChanged = esealAdministratorEsealStatusChanged;
    }

    public NotificationConfig getEsealHasToBeActivated() {
        return esealHasToBeActivated;
    }

    public void setEsealHasToBeActivated(NotificationConfig esealHasToBeActivated) {
        this.esealHasToBeActivated = esealHasToBeActivated;
    }

    public NotificationConfig getEsealManagerRemoved() {
        return esealManagerRemoved;
    }

    public void setEsealManagerRemoved(NotificationConfig esealManagerRemoved) {
        this.esealManagerRemoved = esealManagerRemoved;
    }

    public NotificationConfig getEsealManagerAssigned() {
        return esealManagerAssigned;
    }

    public void setEsealManagerAssigned(NotificationConfig esealManagerAssigned) {
        this.esealManagerAssigned = esealManagerAssigned;
    }

    public NotificationConfig getEsealAdministratorManagersChanged() {
        return esealAdministratorManagersChanged;
    }

    public void setEsealAdministratorManagersChanged(NotificationConfig esealAdministratorManagersChanged) {
        this.esealAdministratorManagersChanged = esealAdministratorManagersChanged;
    }

    public static class NotificationConfig {
        @NotBlank
        private String bodyTemplateName;
        @NotEmpty
        private Map<String, String> subject;
        @NotBlank
        private String defaultSubject;

        public String getDefaultSubject() {
            return defaultSubject;
        }

        public void setDefaultSubject(String defaultSubject) {
            this.defaultSubject = defaultSubject;
        }

        public Map<String, String> getSubject() {
            return subject;
        }

        public void setSubject(Map<String, String> subject) {
            this.subject = subject;
        }

        public String getBodyTemplateName() {
            return bodyTemplateName;
        }

        public void setBodyTemplateName(String bodyTemplateName) {
            this.bodyTemplateName = bodyTemplateName;
        }
    }
}
