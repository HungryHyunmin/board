package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {
    @Autowired
   private BoardRepository boardRepository;

    public void write(Board board, MultipartFile file) throws Exception{ //글 작성 처리

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files"; //프로젝트 경로 담아주기
        UUID uuid = UUID.randomUUID(); //랜덤으로 만들어주기
        String fileName = uuid + "_" + file.getOriginalFilename(); //랜덤으로 uuid + 파일 아이디 생성
        File saveFile = new File(projectPath, fileName);
        file.transferTo(saveFile);
        board.setFilename(fileName); //DB에 반영
        board.setFilepath("/files/"+fileName); //DB에 반영


        boardRepository.save(board);
    }
    public Page<Board> boardList(Pageable pageable){// 게시글 리스트 처리

        return boardRepository.findAll(pageable);
    }
    //특정 게시글 불러오기
    public Board boardView(Integer id){ //id 에 들어간 값이

        return boardRepository.findById(id).get(); //여기로 들어서 반환
    }
    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable){
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }
    public void boardDelete (Integer id) { //특정 게시글 삭제

        boardRepository.deleteById(id);
    }
}
