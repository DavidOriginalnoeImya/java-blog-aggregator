package cz.jiripinkas.jba.studies.unit;

import cz.jiripinkas.jba.dto.CategoryDto;
import cz.jiripinkas.jba.entity.Category;
import cz.jiripinkas.jba.service.AllCategoriesService;
import cz.jiripinkas.jba.service.CategoryService;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryUnitTest {
    private static final Logger LOGGER = Logger.getLogger(CategoryUnitTest.class.getName());

    @Autowired
    AllCategoriesService allCategoriesService;

    @Autowired
    CategoryService categoryService;

    private static final String categoryName = "Reddit";

    private static final String categoryShortName = "reddit";

    @Test
    public void testGetOneCategoriesId() {
        Category category = new Category();

        categoryService.save(category);

        Integer[] categoriesId = allCategoriesService.getAllCategoryIds();

        assertThat(categoriesId, hasItemInArray(category.getId()));
    }

    @Test
    public void testGetEmptyCategoriesId() {
        Integer[] categoriesId = allCategoriesService.getAllCategoryIds();

        assertEquals(0, categoriesId.length);
    }

    @Test
    public void testFindOneCategory() {
        Category category = new Category();
        category.setName(categoryName);
        category.setShortName(categoryShortName);

        categoryService.save(category);

        List<Category> categories = categoryService.findAll();

        assertThat(categories, hasItem(allOf(
                hasProperty("name", equalTo(categoryName)),
                hasProperty("shortName", equalTo(categoryShortName))
        )));
    }

    @Test
    public void testFindEmptyCategory() {
        List<Category> categories = categoryService.findAll();

        assertEquals(0, categories.size());
    }

    @Test
    public void testDeleteCategory() {
        Category category = new Category();

        categoryService.save(category);
        categoryService.delete(category.getId());

        assertEquals(0, categoryService.findAll()
                .stream().filter(curCategory -> curCategory.getId() == category.getId())
                .toList().size()
        );
    }

    @Test
    public void testFindDto() {
        Category category = new Category();
        category.setName(categoryName);
        category.setShortName(categoryShortName);

        categoryService.save(category);

        CategoryDto categoryDto = categoryService.findOneDto(category.getId());

        assertEquals(category.getId(), categoryDto.getId());
        assertEquals(categoryName, categoryDto.getName());
        assertEquals(categoryShortName, categoryDto.getShortName());
    }
}
