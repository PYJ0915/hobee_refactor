package hobee.semi.project.common.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class Utility {
	
	private static final Map<String, Integer> HOBBY_CODE_MAP = Map.of(
		    "sports",       1,
		    "art",          2,
		    "selfDevelop",  3,
		    "social",       4,
		    "shopping",     5
		);

	public static String fileRename(String originalName) {
	
		// 현재 시간
	    String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

	    // 확장자
	    String ext = originalName.substring(originalName.lastIndexOf("."));

	    // UUID 8자리 사용
	    String uuid = UUID.randomUUID().toString().replace("-", "") .substring(0, 8);

	    return date + "_" + uuid + ext;
	}
	
	public static int getHobbyCode(String hobby) {
			return HOBBY_CODE_MAP.getOrDefault(hobby, 0);
		}
	
	/**
     * 리사이징 가능한 이미지 타입인지 확인
     * GIF는 애니메이션 손상 방지를 위해 제외
     */
    public static boolean isResizableImage(String originalFilename) {
        if (originalFilename == null) return false;
        String lower = originalFilename.toLowerCase();
        return lower.endsWith(".jpg")
            || lower.endsWith(".jpeg")
            || lower.endsWith(".png")
            || lower.endsWith(".webp");
    }

    /**
     * 프로필 이미지 리사이징 - 300x300 정사각형 크롭
     */
    public static void resizeProfile(MultipartFile inputFile, String savePath) throws Exception {
        Thumbnails.of(inputFile.getInputStream())
                  .size(300, 300)
                  .crop(Positions.CENTER)
                  .toFile(new File(savePath));
    }

    /**
     * 게시글 이미지 리사이징 - 최대 너비 1200px, 비율 유지
     * 원본이 1200px 이하면 리사이징 없이 그대로 저장
     */
    public static void resizeBoard(MultipartFile inputFile, String savePath) throws Exception {
        BufferedImage original = ImageIO.read(inputFile.getInputStream());

        if (original == null) {
            // 이미지 파싱 실패 시 그대로 저장
            inputFile.transferTo(new File(savePath));
            return;
        }

        if (original.getWidth() > 1200) {
            Thumbnails.of(inputFile.getInputStream())
                      .width(1200)
                      .keepAspectRatio(true)
                      .toFile(new File(savePath));
        } else {
            inputFile.transferTo(new File(savePath));
        }
    }
	
}
