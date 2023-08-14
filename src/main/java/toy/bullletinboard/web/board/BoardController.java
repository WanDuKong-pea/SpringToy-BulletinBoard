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
import toy.bullletinboard.domain.comment.Comment;
import toy.bullletinboard.domain.comment.CommentService;
import toy.bullletinboard.domain.member.Member;
import toy.bullletinboard.domain.member.SessionConst;
import toy.bullletinboard.file.FileStore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;
    private final FileStore fileStore;


    /**
     * 게시판 목록 페이지를 반환하는 메서드
     */
    @GetMapping
    public String boards(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                         @RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "10") int pageSize,
                         Model model) {

        // 게시물 목록을 페이징하여 화면에 표시하기 위한 정보 설정
        try {
            int totalCnt = boardService.getCount();
            PageHandler ph = new PageHandler(totalCnt,page,pageSize);
            Map map = new HashMap();
            map.put("offset",(page-1)*pageSize);
            map.put("pageSize",pageSize);

            List<Board> boards = boardService.getPage(map);
            model.addAttribute("boards", boards);
            model.addAttribute("ph", ph);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 로그인된 회원이 없다면 비로그인 상태에 맞는 뷰를 반환
        if(loginMember == null){
            return "views/boards";
        }

        // 로그인된 회원 정보를 뷰에 추가하여 로그인 상태에 맞는 뷰를 반환
        model.addAttribute("member",loginMember);

        return "views/login-boards";
    }


    /**
     * 게시물 검색 기능을 구현하는 메서드
     */
    @GetMapping("/search")
    public ResponseEntity<List<Board>> searchBoards(@RequestParam("search") String search) {
        // 검색어를 이용하여 게시물을 조회하고 List<Board> 형태로 결과를 반환
        //ResponseEntity는 제네릭을 사용하여 응답 본문의 데이터 타입을 지정할 수 있음
        log.info("[게시글 검색어] search={}",search);
        List<Board> searchResult = boardService.searchBoards(search);

        // HTTP 상태 코드 200 OK와 함께 응답을 반환
        return ResponseEntity.ok(searchResult);
    }

    /**
     * 게시물 상세 페이지를 반환하는 메서드
     */
    @GetMapping("/{boardId}")
    public String item(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                       @PathVariable Long boardId, Model model) {

        // 게시물 및 댓글 정보를 조회하여 뷰에 전달하고 게시물 조회 수를 증가
        Board board = boardService.searchBoardById(boardId);
        boardService.plusBoardViews(boardId);
        log.info("[조회된 게시물] board={}",board);

        List<Comment> comments = commentService.getCommentList(boardId);
        List<Comment> replies = commentService.getReplyList(boardId);

        model.addAttribute("board", board);
        model.addAttribute("comments",comments);
        model.addAttribute("replies",replies);
        model.addAttribute("loginId",loginMember.getLoginId());

        return "views/board";
    }

    /**
     * 게시물 추가 페이지를 반환하는 메서드
     */
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

    /**
     * 게시물을 추가하는 메서드
     */
    @PostMapping("/add")
    public String addBoard(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                           @Validated @ModelAttribute("board") BoardSaveForm boardForm, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) throws IOException {
        // @ModelAttribute("board")을 지정해주어야 뷰에서 board으로 받음 + 에러 코드 오브젝트도 board으로 생김

        // 게시물을 추가하고 결과에 따라 리다이렉트
        // 검증에 실패하면 다시 입력 폼으로 이동
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            //모델에 담지 않아도 됨
            //BindingResult는 자동으로 뷰에 같이 넘어감
            return "views/addBoard";
        }

        // 검증 성공 로직
        // 게시물 정보를 생성하고 저장
        Board board = new Board();

        // 이미지가 있는 경우 파일을 저장
        if (boardForm.getImgName() != null) {
            String imgName = fileStore.storeFile(boardForm.getImgName());
            board.setImgName(imgName);
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

    /**
     * 게시물 수정 화면을 반환하는 메서드
     */
    @GetMapping("/edit/{boardId}")
    public String editBoard(@PathVariable long boardId, Model model) {
        Board board = boardService.searchBoardById(boardId);
        log.info("boardImg={}", board.getImgName());
        model.addAttribute("board", board);
        return "views/editBoard";
    }

    /**
     * 게시물을 업데이트하고 게시물 상세화면을 반환하는 메서드
     */
    @PostMapping("/edit/{boardId}")
    public String editBoard(@Validated @ModelAttribute("board") BoardUpdateForm boardForm, BindingResult bindingResult,
                            @PathVariable long boardId, RedirectAttributes redirectAttributes) throws IOException {

        // 검증에 실패하면 다시 입력 폼을 반환
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "views/editBoard";
        }

        // 검증 성공 로직
        Board board = new Board();

        // 이미지 파일이 존재하면 저장
        if (boardForm.getImgName() != null) {
            String imgName = fileStore.storeFile(boardForm.getImgName());
            board.setImgName(imgName);
        }

        board.setTitle(boardForm.getTitle());
        board.setBody(boardForm.getBody());

        log.info("[게시물 업데이트 데이터]={}", board);

        // 게시판 DB업데이트
        Board updateBoard = boardService.updateBoard(boardId, board);
        redirectAttributes.addAttribute("boardId", updateBoard.getBoardId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/boards/{boardId}";
    }

    /**
     * 게시물을 삭제하고 게시물 목록으로 리다이렉트하는 메서드
     */
    @GetMapping("/delete/{boardId}")
    public String deleteBoard(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                              @PathVariable long boardId, RedirectAttributes redirectAttributes) {

        boardService.deleteBoard(boardId);
        redirectAttributes.addAttribute("deleteStatus", true);
        return "redirect:/boards";
    }
}