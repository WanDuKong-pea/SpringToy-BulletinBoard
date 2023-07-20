package toy.bullletinboard.domain.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import toy.bullletinboard.mapper.BoardMapper;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BoardRepository {
    private static final Map<Long, Board> store = new HashMap<>(); //static
    private static long sequence = 0L; //static
    private final BoardMapper boardMapper;
    public Board save(Board board) {
        board.setBoardId(++sequence);
        board.setRegDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        if (board.getImgName() == null) {
            //board.setImgName(new UploadFile("default", "default"));
            board.setImgName("default");
        }

        store.put(board.getBoardId(), board);
        log.info("[새로운 게시물]={}", board);
        return board;
    }

    public Board findById(Long id) {
        return store.get(id);
    }

    public List<Board> findAll() {
        return new ArrayList<>(store.values());
    }

    public Board update(Long BoardId, Board updateParam) {
        Board findBoard = findById(BoardId);
        findBoard.setTitle(updateParam.getTitle());
        findBoard.setBody(updateParam.getBody());

        if (updateParam.getImgName() != null) {
            findBoard.setImgName(updateParam.getImgName());
        }

        log.info("[업데이트된 게시물] board={}", findBoard);
        return findBoard;
    }

    public void plusViews(Long BoardId){
        Board findBoard = findById(BoardId);
        findBoard.setViews(findBoard.getViews()+1);
    }

    public List<Board> searchByTitle(String search) {
        return findAll().stream()
                .filter(b -> b.getTitle().contains(search))
                .collect(Collectors.toList());
    }

    public void delete(Long BoardId) {
        log.info("[삭제된 게시물] board={}",findById(BoardId));
        store.remove(BoardId);
    }

    public void clearStore() {
        store.clear();
    }
}
