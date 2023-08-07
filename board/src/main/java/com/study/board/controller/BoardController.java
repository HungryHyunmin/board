package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {
    @Autowired
    private BoardService boardService;
    @GetMapping("/board/write") //localhost:8090/board/write
    public String boardWriteForm(){
        return "boardwrite";
    }

    @PostMapping("/board/writepro")  //게시글 작성
    public String boardWritePro(Board board, Model model,MultipartFile file) throws Exception{

        boardService.write(board, file);

        model.addAttribute("message","글작성이 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");

        return "message";
    }
    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10 , sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword){ //받은 데이터 페이지로 전달 // 도메일 패키지 pageable
        Page<Board> list =null;

        if(searchKeyword == null){ // 검색 키워드가 없을때 그대로 실행
             list = boardService.boardList(pageable);
        }
        else{
             list = boardService.boardSearchList(searchKeyword,pageable);
        }




        int nowPage = list.getPageable().getPageNumber() + 1; //페이지에서 넘어온 현재 페이지 가져와 nowPage에 저장
        int startPage = Math.max(nowPage-4,1); //높은값 반환, 페이지가 1일 떄 1-4를 방지
        int endPage = Math.min(nowPage+5,list.getTotalPages()); //낮은값 반환 total페이지 수 랑 비교

        model.addAttribute("list", list); //반환된 리스트를 list이름으로 반환
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);

        return "boardlist";
    }

    @GetMapping("/board/view") //localhost:8090/board/view?id=1 입력하면 1이  (파라미터) (get방식)
    public String boardview(Model model, Integer id){ //여기로 들어가고
        model.addAttribute("board",boardService.boardView(id)); //1이 다시 안으로 들어가서 게시글 불러옴
        return "boardview"; //이부분은 넘겨 받을 html 페이지
    }
    @GetMapping("/board/delete")
    public String boardDelete(Integer id, Model model) {
        boardService.boardDelete(id);
        model.addAttribute("message", "글 삭제가 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id,Model model){
        //Pathvariable은 "/board/modify/{id}"의 id가 인식되서 Integer형태의 id로 들어옴
        model.addAttribute("board",boardService.boardView(id));
        return "boardmodify";
    }
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception {
        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());
        boardService.write(boardTemp, file);
        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }
}
