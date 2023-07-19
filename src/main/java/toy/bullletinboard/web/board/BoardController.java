package toy.bullletinboard.web.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import toy.bullletinboard.domain.board.*;
import toy.bullletinboard.domain.member.Member;
import toy.bullletinboard.domain.member.SessionConst;
import toy.bullletinboard.file.FileStore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final FileStore fileStore;


    @GetMapping
    public String boards(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {

        List<Board> boards = boardService.findAllBoards();
        model.addAttribute("boards", boards);

        //세션 확인
        if(loginMember == null){
            return "views/boards";
        }

        model.addAttribute("member",loginMember);

        return "views/login-boards";
    }

    /**
     * 게시물 검색 기능 구현
     * 파라미터로 받아온 String search에 대응하는 게시물 리스트로 반환
     */
    @GetMapping("/search")
    public ResponseEntity<List<Board>> searchBoards(@RequestParam("search") String search) {
        // 검색어를 이용하여 게시물을 조회하고 List<Board> 형태로 결과를 반환
        //ResponseEntity는 제네릭을 사용하여 응답 본문의 데이터 타입을 지정할 수 있음
        log.info("[게시글 검색어] search={}",search);
        List<Board> searchResult = boardService.searchBoards(search);

        return ResponseEntity.ok(searchResult);
        //00 OK 상태 코드와 함께 응답을 생성
    }

    @GetMapping("/{boardId}")
    public String item(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                       @PathVariable long boardId, Model model) {

        Board board = boardService.searchBoardById(boardId);
        boardService.plusBoardViews(boardId);
        log.info("[조회된 게시물] board={}",board);
        model.addAttribute("board", board);

        if(board.getMemberId().equals(loginMember.getLoginId())){
            model.addAttribute("status","true");
        }

        return "views/board";
    }

    @GetMapping("/add")
    public String addBoard(Model model) {
        model.addAttribute("board", new Board());
        return "views/addBoard";
    }

    /**
     * 태그로 이미지를 조회할때 사용
     * UrlResource: 이미지 파일을 읽어서 @ResponseBody로 이미지 바이너리 변환
     * 서버에 저장된 파일명을 파라미터로 받음
     */
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        if (Objects.equals(filename, "")) {
            return new UrlResource("");
        }
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }


    @PostMapping("/add")
    public String addBoard(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                           @Validated @ModelAttribute("board") BoardSaveForm boardForm, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) throws IOException {
        //@ModelAttribute("board")을 지정해주어야 뷰에서 board으로 받음 + 에러 코드 오브젝트도 board으로 생김

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            //모델에 담지 않아도 됨
            //BindingResult는 자동으로 뷰에 같이 넘어감
            return "views/addBoard";
        }

        Board board = new Board();

        //성공 로직
        if (boardForm.getImgName() != null) {
            UploadFile attachFile = fileStore.storeFile(boardForm.getImgName());
            board.setImgName(attachFile);
        }

        board.setMemberId(loginMember.getLoginId());
        board.setTitle(boardForm.getTitle());
        board.setBody(boardForm.getBody());

        log.info("[새게시물 입력 데이터]={}", board);
        //게시판 DB저장
        Board savedBoard = boardService.saveBoard(board);
        redirectAttributes.addAttribute("boardId", savedBoard.getBoardId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/boards/{boardId}";
    }


    @GetMapping("/edit/{boardId}")
    public String editBoard(@PathVariable long boardId, Model model) {
        Board board = boardService.searchBoardById(boardId);
        log.info("boardImg={}", board.getImgName());
        model.addAttribute("board", board);
        return "views/editBoard";
    }

    @PostMapping("/edit/{boardId}")
    public String editBoard(@Validated @ModelAttribute("board") BoardUpdateForm boardForm, BindingResult bindingResult,
                            @PathVariable long boardId, RedirectAttributes redirectAttributes) throws IOException {

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "views/editBoard";
        }

        Board board = new Board();
        //파일 저장
        if (boardForm.getImgName() != null) {//파일이 존재하면
            UploadFile attachFile = fileStore.storeFile(boardForm.getImgName());
            board.setImgName(attachFile);
        }

        board.setTitle(boardForm.getTitle());
        board.setBody(boardForm.getBody());

        log.info("[게시물 업데이트 데이터]={}", board);
        //게시판 DB업데이트
        Board updateBoard = boardService.updateBoard(boardId, board);
        redirectAttributes.addAttribute("boardId", updateBoard.getBoardId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/boards/{boardId}";
    }

    @GetMapping("/delete/{boardId}")
    public String deleteBoard(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                              @PathVariable long boardId, RedirectAttributes redirectAttributes) {

        boardService.deleteBoard(boardId);
        redirectAttributes.addAttribute("deleteStatus", true);
        return "redirect:/boards";
    }
}