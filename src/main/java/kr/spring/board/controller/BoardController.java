package kr.spring.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardVO;
import kr.spring.util.PageUtil;


@Controller
public class BoardController {
	@Autowired
	private BoardService boardService;
	
	//로그 처리(로그 대상 지정)
	private static final Logger log = LoggerFactory.getLogger(BoardController.class);
	
	//자바빈 초기화
	@ModelAttribute
	public BoardVO initCommand() {
		return new BoardVO();
	}
	
	//글작성 폼 호출
	@GetMapping("/insert.do")
	public String form() {
		return "insertForm";
	}
	
	//글작성 실행 
	@PostMapping("/insert.do")
	public String submit(@Valid BoardVO boardVO, BindingResult result) {
		log.debug("<<BoardVO>> : " + boardVO);
		
		//유효성 체크 결과 오류가 있으면 폼을 호출
		if(result.hasErrors()) {
			return form();
		}
		
		//글 등록
		boardService.insertBoard(boardVO);
		
		return "redirect:/list.do";
	}
	
	//페이지 목록 (초기 대문 화면)
	@RequestMapping("/list.do")
	public ModelAndView getList(@RequestParam(value="pageNum", defaultValue="1") int currentPage) {
		
		//총 레코드 수
		int count = boardService.selectBoardCount();
		
		log.debug("<<count>> : " + count);
		log.debug("<<pageNum>> : " + currentPage);
		
		//페이지 처리
		PageUtil page = new PageUtil(currentPage, count, 10, 10, "list.do");
		
		//목록 호출
		List<BoardVO> list = null;
		if(count > 0) {
			Map<String,Integer>map = new HashMap<String,Integer>();
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			
			list = boardService.selectBoardList(map);
		}
		
		ModelAndView mav = new ModelAndView();
		//뷰 이름 지정
		mav.setViewName("selectList");
		//데이터 저장
		mav.addObject("count", count);
		mav.addObject("list", list);
		mav.addObject("page", page.getPage());
		
		return mav;  
	}
	
	//선택 페이지 호출 (상세보기)
	@RequestMapping("/detail.do")
	public ModelAndView detail(@RequestParam int num) {
		BoardVO board = boardService.selectBoard(num);
		
		return new ModelAndView("selectDetail", "board", board); //뷰이름, 속성명, 속성값
	}
	
	
}