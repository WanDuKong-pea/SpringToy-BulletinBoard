package toy.bullletinboard.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.bullletinboard.domain.member.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public List<Board> findAllBoards(){
        return boardRepository.findAll();
    }

    public List<Board> searchBoards(String searchData){
        return boardRepository.searchByTitle(searchData);
    }

    public Board searchBoardById(Long id){
        return boardRepository.findById(id);
    }

    public void plusBoardViews(Long id){
        boardRepository.plusViews(id);
    }

    public Board saveBoard(Board board){
        return boardRepository.save(board);
    }

    public Board updateBoard(Long id, Board board){
        return boardRepository.update(id, board);
    }

    public void deleteBoard(Long id){
        boardRepository.delete(id);
    }
}
