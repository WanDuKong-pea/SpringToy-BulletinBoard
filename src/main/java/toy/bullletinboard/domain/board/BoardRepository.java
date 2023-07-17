package toy.bullletinboard.domain.board;

import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class BoardRepository {
    private static final Map<Long, Board> store = new HashMap<>(); //static
    private static long sequence = 0L; //static

    public Board save(Board board) {
        board.setBoardId(++sequence);
        board.setRegDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        if(board.getImgName() == null){
            board.setImgName(new UploadFile("default","default"));
        }

        store.put(board.getBoardId(), board);
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

        if(updateParam.getImgName() != null){
            findBoard.setImgName(updateParam.getImgName());
        }

        return findBoard;
    }

    public void delete(Long BoardId) {
        store.remove(BoardId);
    }

    public void clearStore() {
        store.clear();
    }
}
