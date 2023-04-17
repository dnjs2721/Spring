package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jpabook.jpashop.controller.dto.UpdateItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("B")
public class Book extends Item{
    private String author;
    private String isbn;

    /** 수정*/
//    public void updateBook(UpdateItemDto dto) {
//        this.author = dto.getAuthor();
//        this.isbn = dto.getIsbn();
//        this.update(dto.getName(), dto.getPrice(), dto.getStockQuantity());
//    }
    @Override
    public void changeItem(UpdateItemDto dto) {
        super.changeItem(dto);
        author = dto.getAuthor();
        isbn = dto.getIsbn();
    }

}
