package com.shop.controller;

import com.shop.entity.Board;
import com.shop.entity.Member;
import com.shop.service.BoardService;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/write")
    public String boardWriteFrom() {
        return "/board/write";
    }

    @PostMapping("/writedone")
    public ResponseEntity<?> boardWritePro(@RequestBody Board board, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member user = (Member) auth.getPrincipal();  // OK!
        String email = user.getUsername();

        Member member = memberService.findByEmail(email);

        System.out.println("writepro메소드 들어옴");
        System.out.println("제목: " + board.getTitle());
        System.out.println("내용: " + board.getContent());
        System.out.println(member.getEmail());
        board.setWriterId(member);
        boardService.write(board);
        return ResponseEntity.ok("작성 완료");
    }

    @GetMapping("/list")
    @ResponseBody
    public Page<Board> boardList(Model model, @PageableDefault(page=0, size=10, sort="boardId", direction= Sort.Direction.DESC) Pageable pageable) {
        //서비스에서 생성한 리스트를 list라는 이름으로 반환하겠다.
        Page<Board> list = boardService.boardList(pageable);
        //페이지블럭 처리
        //1을 더해주는 이유는 pageable은 0부터라 1을 처리하려면 1을 더해서 시작해주어야 한다.
        int nowPage = list.getPageable().getPageNumber() + 1;
        //-1값이 들어가는 것을 막기 위해서 max값으로 두 개의 값을 넣고 더 큰 값을 넣어주게 된다.
        int startPage =  Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage+9, list.getTotalPages());


        model.addAttribute("list", list);
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("list", boardService.boardList(pageable));
        return boardService.boardList(pageable);
    }
}