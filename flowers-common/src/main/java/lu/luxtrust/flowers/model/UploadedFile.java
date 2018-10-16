package lu.luxtrust.flowers.model;

import org.springframework.web.multipart.MultipartFile;

public class UploadedFile {
    private MultipartFile file;

    public UploadedFile() {

    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
