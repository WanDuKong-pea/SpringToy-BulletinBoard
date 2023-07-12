package toy.bullletinboard.controller.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import toy.bullletinboard.domain.board.Board;
import toy.bullletinboard.domain.board.BoardRepository;
import toy.bullletinboard.domain.board.BoardSaveForm;
import toy.bullletinboard.domain.board.BoardUpdateForm;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping
    public String boards(Model model) {
        List<Board> boards = boardRepository.findAll();
        model.addAttribute("boards", boards);
        return "views/boards";
    }

    @GetMapping("/{boardId}")
    public String item(@PathVariable long boardId, Model model) {
        Board board = boardRepository.findById(boardId);
        model.addAttribute("board", board);
        return "views/board";
    }

    @GetMapping("/add")
    public String addBoard(Model model) {
        model.addAttribute("board", new Board());
        return "views/addBoard";
    }

    @PostMapping("/add")
    public String addBoard(@Validated @ModelAttribute("board") BoardSaveForm boardForm, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes, @RequestParam MultipartFile file) throws IOException {
        //@ModelAttribute("board")을 지정해주어야 뷰에서 board으로 받음 + 에러 코드 오브젝트도 item으로 생김

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            //모델에 담지 않아도 됨
            //BindingResult는 자동으로 뷰에 같이 넘어감
            return "views/addBoard";
        }

    //성공 로직

        //파일 저장
        if (!file.isEmpty()) {
            String fullPath = fileDir + file.getOriginalFilename();
            file.transferTo(new File(fullPath));
        }
        boardForm.setImgName(file.getOriginalFilename());

        Board board = new Board();
        board.setTitle(boardForm.getTitle());
        board.setBody(boardForm.getBody());
        board.setImgName(boardForm.getImgName());

        //게시판 DB저장
        Board savedBoard = boardRepository.save(board);
        redirectAttributes.addAttribute("boardId", savedBoard.getBoardId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/boards/{boardId}";
    }

    @GetMapping("/edit/{boardId}")
    public String editBoard(@PathVariable long boardId, Model model) {
        Board board = boardRepository.findById(boardId);
        model.addAttribute("board", board);
        return "views/editBoard";
    }

    @PostMapping("/edit/{boardId}")
    public String editBoard(@Validated @ModelAttribute("board") BoardUpdateForm boardForm, BindingResult bindingResult, @PathVariable long boardId,
                            RedirectAttributes redirectAttributes, @RequestParam MultipartFile file) throws IOException {

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "views/editBoard";
        }

        //성공 로직
        //파일 저장
        if (!file.isEmpty()) {
            String fullPath = fileDir + file.getOriginalFilename();
            file.transferTo(new File(fullPath));
            boardForm.setImgName(file.getOriginalFilename());
        }


        Board board = new Board();
        board.setTitle(boardForm.getTitle());
        board.setBody(boardForm.getBody());
        board.setImgName(boardForm.getImgName());


        //게시판 DB업데이트
        Board updateBoard = boardRepository.update(boardId, board);
        redirectAttributes.addAttribute("boardId", updateBoard.getBoardId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/boards/{boardId}";
    }

    @GetMapping("/delete/{boardId}")
    public String deleteBoard(@PathVariable long boardId, RedirectAttributes redirectAttributes){
        boardRepository.delete(boardId);
        redirectAttributes.addAttribute("deleteStatus", true);
        return "redirect:/boards";
    }
}