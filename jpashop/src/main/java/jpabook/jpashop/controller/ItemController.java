package jpabook.jpashop.controller;

import jpabook.jpashop.controller.form.BookForm;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.dto.UpdateItemDto;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    private String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("items/new")
    private String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    @GetMapping("items")
    private String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item  = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getItemId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping ("items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {
//        컨트롤러에서 엔티티를 생성하지 말자;
//        트랜잭션이 있는 서비스 계층에 식별자(id) 와 변경할 데이터를 확실하게 전달하자, 파라미터 or DTO
//        Book book = new Book();
//        book.setItemId(form.getId());능
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName(form.getName());
        updateItemDto.setPrice(form.getPrice());
        updateItemDto.setStockQuantity(form.getStockQuantity());
        updateItemDto.setAuthor(form.getAuthor());
        updateItemDto.setIsbn(form.getIsbn());

        itemService.updateItem(itemId, updateItemDto);
        return "redirect:/items";
    }
}






