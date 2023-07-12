//package toy.bullletinboard.Controller.board;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import toy.bullletinboard.domain.board.Board;
//import toy.bullletinboard.domain.board.BoardRepository;
//
//import javax.annotation.PostConstruct;
//
//@Component
//@RequiredArgsConstructor
//public class TestDataInit {
//    private final BoardRepository itemRepository;
//
//    /**
//     * 테스트용 데이터 추가
//     */
//    @PostConstruct
//    public void init() {
//        itemRepository.save(new Board("cok854", "테스트용 데이터1", "본문입니다.","default-image.png",0));
//        itemRepository.save(new Board("cok894", "테스트용 데이터2", "본문입니다.","default-image.png",0));
//    }
//}
