package market.marketproject.controller;

import lombok.extern.slf4j.Slf4j;
import market.marketproject.dto.Category;
import market.marketproject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
public class CategoryController {
    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @PostMapping("/loadCategory")
    public List<Category> loadCategory(@RequestBody Category category){
        return categoryService.loadCategory(category);
    }

    @PostMapping("/addCategory")
    public void addCategory(@RequestBody Category category){
        categoryService.addCategory(category);
    }

    @PostMapping("/deleteCategory")
    public void deleteCategory(@RequestBody Category category){
        categoryService.deleteCategory(category);
    }
}
