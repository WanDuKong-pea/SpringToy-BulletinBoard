package toy.bullletinboard.domain.board;

import lombok.Data;

@Data
public class PageHandler {
    private int totalCnt; // 총 게시물 갯수
    private int pageSize; // 한 페이지의 크기(게시물 수)
    private int naviSize=5; // 페이지 네비게이션의 크기
    private int totalPage; // 전체 페이지의 수
    private int page; //현재 페이지
    private int beginPage; // 네비게이션의 첫번째 페이지
    private int endPage; // 네비게이션의 마지막 페이지
    private boolean showPrev; // 이전 페이지로 이동하는 링크를 보여줄 것인지의 여부
    private boolean showNext; // 다음 페이지로 이동하는 링크를 보여줄 것인지의 여부

    public PageHandler(int totalCnt, int page){
        this(totalCnt, page, 10);
    }

    public PageHandler(int totalCnt, int page, int pageSize) {
        this.totalCnt = totalCnt;
        this.page = page;
        this.pageSize = pageSize;

        //전체 페이지 갯수를 구할 때는 한 게시물이라도 넘어가는 경우 페이지가 만들어져야하기 때문에 올림을 사용
        totalPage = (int) Math.ceil(totalCnt / (double)pageSize);
        beginPage = (page-1)/ naviSize * naviSize + 1;
        endPage = Math.min(beginPage + naviSize - 1, totalPage);
        showPrev = beginPage != 1;
        showNext = endPage != totalPage;

    }

}