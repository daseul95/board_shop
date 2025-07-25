package com.shop.controller;

import com.shop.dto.ItemFormDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ItemController {

    @GetMapping(value="/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    @GetMapping(value="/admin/item")
    public String itemList(Model model){

        model.addAttribute("boards", model);

        return "item/itemList";

    }
}
