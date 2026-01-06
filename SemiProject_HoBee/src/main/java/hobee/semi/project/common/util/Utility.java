package hobee.semi.project.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

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
	
}
