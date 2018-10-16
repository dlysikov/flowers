package lu.luxtrust.flowers.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public class UploadedFiles {
    @NotNull
    @NotEmpty
    private MultipartFile[] file;

    public UploadedFiles() {
    }

    public MultipartFile[] getFile() {
        return file;
    }

    public void setFile(MultipartFile[] file) {
        this.file = file;
    }
}
