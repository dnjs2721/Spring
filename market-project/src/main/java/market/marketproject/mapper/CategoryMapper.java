package market.marketproject.mapper;

import market.marketproject.dto.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> loadCategory(Category category);
    void addCategory(Category category);

    void deleteCategory(Category category);
}
