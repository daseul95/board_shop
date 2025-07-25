package com.shop.service;

import com.shop.entity.Board;
import com.shop.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // 글 작성 처리
    public void write(Board board) {
        boardRepository.save(board);
    }

    public Page<Board> boardList(Pageable pageable){
        return boardRepository.findAll(pageable);
    }

}
