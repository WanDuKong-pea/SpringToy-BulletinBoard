package toy.bullletinboard.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 파일 저장과 관련된 업무 처리
 */
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    /**
     * 파일 경로 반환
     */
    public String getFullPath(String filename) {
        return fileDir + filename;
    }


    /**
     * 하나의 파일을 받아와서 저장하는 메서드
     * UploadFile 변환된 이미지명을 반환
     */
    public String storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        //서버에 저장할 파일명으로 파일 서버 저장
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return storeFileName;
    }

    /**
     * 서버에 저장할 파일명을 만드는 메서드
     */
    private String createStoreFileName(String originalFilename) {

        //"UUID+확장자"로 서버에 저장할 파일 네임을 생성
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    /**
     * 클라이언트가 보낸 파일명에서 확장자만 따로 추출
     */
    private String extractExt(String originalFilename) {
        //originalFilename 에서 "." 이 마지막으로 나타나는 위치 반환
        int pos = originalFilename.lastIndexOf(".");
        // 사용하는 부분에서 "."을 추가했음, .제외 확장자만 필요
        return originalFilename.substring(pos + 1);
    }
}
