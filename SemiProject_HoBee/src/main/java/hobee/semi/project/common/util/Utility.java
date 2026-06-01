package hobee.semi.project.common.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class Utility {

	public static int seqNum = 1;
	
	public static int hobbyCode = 0;
	
	public static String fileRename(String originalName) {
		
		// 시간을 원하는 형태의 문자열로 간단히 변경
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		// 현재 시간 저장
		String date = sdf.format(new Date());
		
		// 00001 포맷
		String number = String.format("%05d", seqNum);

		// 1증가
		seqNum++;

		// 99999 넘어갈 시 1로 초기화
		if(seqNum == 100000) seqNum = 1;
		
		// 확장자 구하기
		String ext = originalName.substring(originalName.lastIndexOf("."));
		
		return date + "_" + number + ext;
	}
	
	public static int getHobbyCode(String hobby) {
			
			switch (hobby) {
			case "sports":
				hobbyCode = 1;
				break;
			case "art":
				hobbyCode = 2;
				break;
			case "selfDevelop":
				hobbyCode = 3;
				break;
			case "social":
				hobbyCode = 4;
				break;
			case "shopping":
				hobbyCode = 5;
				break;
			}
			
			return hobbyCode;
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
