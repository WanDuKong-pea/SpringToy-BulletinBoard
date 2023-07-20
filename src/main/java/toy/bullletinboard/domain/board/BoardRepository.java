package toy.bullletinboard.domain.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import toy.bullletinboard.mapper.BoardMapper;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BoardRepository {
    private final BoardMapper boardMapper;

    public Board save(Board board) {
        board.setRegDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        if (board.getImgName() == null) {
            board.setImgName("default");
        }

        //새로운 보드 저장
        boardMapper.insertBoard(board);

        Board savedBoard = boardMapper.selectById(board.getBoardId());
        log.info("[새로운 게시물]={}", savedBoard);
        return savedBoard;
    }

    public Board update(Long boardId, Board updateParam) {
        try {
            Board findBoard = boardMapper.selectById(boardId);
            findBoard.setTitle(updateParam.getTitle());
            findBoard.setBody(updateParam.getBody());

            if (updateParam.getImgName() != null) {
                findBoard.setImgName(updateParam.getImgName());
            }

            //보드 업데이트
            boardMapper.updateBoard(findBoard);
            //업데이트된 보드 조회
            Board updatedBoard = boardMapper.selectById(findBoard.getBoardId());
            log.info("[업데이트된 게시물] board={}", updatedBoard);
            return updatedBoard;
        } catch (EmptyResultDataAccessException e) {
            // 해당 boardId로 검색된 행이 없는 경우 처리
            log.error("[UPDATE BOARD][boardId={}][해당 boardId에 해당하는 게시물이 존재하지 않음]",boardId, e);
            throw new IllegalArgumentException("해당 boardId에 해당하는 게시물이 존재하지 않습니다.");
        }
    }


    public Board findById(Long boardId) {
        return boardMapper.selectById(boardId);
    }

    public List<Board> findAll() {
        return boardMapper.selectAllBoard();
    }

    public void plusViews(Long boardId) {
        boolean isUpdateView = boardMapper.updateViews(boardId);
        log.info("[boardId:{} 조회+1:{}]", isUpdateView, boardId);
    }
    public List<Board> searchByTitle(String search) {
        return boardMapper.selectBoardLikeTitle(search);
    }

    public void delete(Long boardId) {
        boolean isDeleteBoard = boardMapper.deleteBoard(boardId);
        log.info("[boardId:{} 삭제:{}]", boardId, isDeleteBoard);
    }
}