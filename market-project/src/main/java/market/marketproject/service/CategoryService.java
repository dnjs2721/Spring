package market.marketproject.service;

import lombok.extern.slf4j.Slf4j;
import market.marketproject.dto.Category;
import market.marketproject.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryService {
    private CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryMapper categoryMapper){
        this.categoryMapper = categoryMapper;
    }

    public List<Category> loadCategory(Category category){
        List<Category> myCate = categoryMapper.loadCategory(category);
        for (int i = 0; i < myCate.size(); i++) {
            String n_key = myCate.get(i).getNKey();
            String c_nm = myCate.get(i).getCategoryNm();
            String c_Nm = myCate.get(i).getCategoryName();
            log.info(n_key + ", " + c_nm + ", " + c_Nm);
        }
        return myCate;
    }

    public void addCategory(Category category){
        categoryMapper.addCategory(category);
    }

    public void deleteCategory(Category category){
        categoryMapper.deleteCategory(category);
    }
}
