package hobee.semi.project.board.model.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.board.model.dto.BoardImg;
import hobee.semi.project.board.model.mapper.EditBoardMapper;
import hobee.semi.project.common.util.Utility;
import hobee.semi.project.follow.model.mapper.FollowMapper;
import hobee.semi.project.notification.model.dto.Notification;
import hobee.semi.project.notification.model.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@PropertySource("classpath:/config.properties")
@Slf4j
public class EditBoardServiceImpl implements EditBoardService {

    private final EditBoardMapper mapper;
    private final FollowMapper followMapper;
    private final NotificationMapper notificationMapper;

    @Value("${my.board.web-path}")
    private String webPath; // /images/board/

    @Value("${my.board.folder-path}")
    private String folderPath; // C:/uploadFiles/boardImg

    /** [서머노트 전용] 이미지 업로드 서비스 
     * 
     */
    @Override
    public String imageUpload(MultipartFile file) throws Exception {
        
        if (file.isEmpty()) return null;

        // 1. 파일명 변경
        String originalName = file.getOriginalFilename();
        String rename = Utility.fileRename(originalName);

        // 2. DB에 이미지 정보 미리 삽입
        BoardImg img = BoardImg.builder()
                .BoardImgOriginalName(originalName)
                .BoardImgRename(rename)
                .BoardImgPath(webPath)
                .build();

        int result = mapper.insertImage(img);

        if (result == 0) throw new RuntimeException("이미지 DB 삽입 실패");

        // 3. 서버에 파일 저장 (기존 transferTo 로직 활용)
        File folder = new File(folderPath);
        if(!folder.exists()) folder.mkdirs();
        
        file.transferTo(new File(folderPath + rename));

        // 4. 에디터에 뿌려줄 웹 접근 경로 반환
        return webPath + rename;
    }

    /** 게시글 작성 서비스 (서머노트 버전) */
    @Override
    public int boardInsert(Board inputBoard) {

        // 1. 게시글 부분 INSERT
        int result = mapper.boardInsert(inputBoard);
        if (result == 0) return 0;

        int boardNo = inputBoard.getBoardNo();

        // 2. 본문(boardContent)에서 이미지 이름들 추출 (정규표현식)
        List<String> fileNames = getImgNamesFromContent(inputBoard.getBoardContent());

        // 3. 추출된 이미지가 있다면 DB에서 주인(boardNo) 찾아주기
        if (!fileNames.isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            map.put("boardNo", boardNo);
            map.put("fileNames", fileNames);
            int updateCount = mapper.updateImageBoardNo(map);
            if (updateCount == 0) {
                throw new RuntimeException("이미지 정보 연결 실패"); // 롤백
            }
        }
        
        // 팔로워들에게 게시글 등록 알림
        List<Integer> followerNoList = followMapper.getFollowerNoList(inputBoard.getMemberNo());

        for (int followerNo : followerNoList) {
            Notification noti = Notification.builder()
                .receiverNo(followerNo)
                .senderNo(inputBoard.getMemberNo())
                .notiType("BOARD")
                .notiTargetNo(boardNo)
                .notiMessage("님이 새 게시글을 작성했습니다.")
                .build();
            notificationMapper.insertNotification(noti);
        }

        return boardNo;
    }

    // SummerNote 에서는 필수로 필요한 메서드임
    /** 본문에서 파일명 추출하는 도우미 메서드 */
    private List<String> getImgNamesFromContent(String content) {
        List<String> list = new ArrayList<>();
        // webPath(예: /images/board/) 뒤의 파일명을 찾는 패턴
        Pattern pattern = Pattern.compile(webPath + "([^\"']+)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

	@Override
	public int boardDelete(Map<String, Object> map) {
		return mapper.boardDelete(map);
		
	}

	/** 게시글 수정 
	 *
	 */
	@Override
	public int boardUpdate(Board inputBoard) {
		return mapper.boardUpdate(inputBoard);
	}

	@Override
	public List<String> selectDbImgList() {
		return mapper.selectDbImageList();
	}
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
}
