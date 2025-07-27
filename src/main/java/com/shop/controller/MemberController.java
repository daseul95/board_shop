package com.shop.controller;

import com.shop.config.jwt.JwtUtils;
import com.shop.dto.MemberFormDto;
import com.shop.dto.payload.request.LoginRequest;
import com.shop.dto.payload.response.JwtResponse;
import com.shop.entity.Member;
import com.shop.service.JwtTokenProvider;
import com.shop.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

@RequestMapping("/member")
@Controller
public class MemberController{

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MemberService memberService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping(value="/new")
    public String memberForm(Model model)
    {
        model.addAttribute("memberFormDto",new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value="/new")
    public String newMember(@Valid MemberFormDto memberFormDto,
                            BindingResult bindingResult,Model model){

        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }
        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        }catch(IllegalStateException e){
            model.addAttribute("errorMessage",e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value="/login")
    public String loginMember(){
        return "/member/memberLoginForm";
    }

    @PostMapping(value="/login")
    @ResponseBody
    public  ResponseEntity<?> postLoginMember(@RequestBody LoginRequest request) {

        System.out.println(request.getEmail());
        System.out.println(request.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            System.out.println(authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Member member = (Member) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.generateJwtToken(authentication);
            String refreshToken = jwtTokenProvider.generateJwtToken(authentication);

            // 2. 리프레시 토큰 저장
            member.setRefreshToken(refreshToken);
            memberService.resaveMember(member);

            String token = jwtTokenProvider.generateJwtToken(authentication);
            Long id = memberService.findByEmail(request.getEmail()).getId();
            String userName = memberService.findByEmail(request.getEmail()).getName();

            return ResponseEntity.ok(new JwtResponse(accessToken, id, request.getEmail(), userName)); // ✅ JSON 형태로 JWT 보내야 함
        } catch (BadCredentialsException ex) {
            System.out.println("비밀번호 또는 아이디 불일치");
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }


    @GetMapping(value="/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }
}
