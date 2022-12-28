package cz.jiripinkas.jba.studies.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jiripinkas.jba.entity.Category;
import cz.jiripinkas.jba.repository.CategoryRepository;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryIntegrationTest {
    private static final Logger LOGGER = Logger.getLogger(CategoryIntegrationTest.class.getName());

    @Autowired
    private WebApplicationContext context;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ObjectMapper objectMapper;

    private static final String categoryName = "Reddit";

    private static final String categoryShortName = "reddit";

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testGetOneCategoriesId() throws Exception {
        Category category = new Category();

        categoryRepository.save(category);

        ResultActions resultActions = mvc.perform(get("/all-categories"))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        Integer[] categoriesId = objectMapper.readValue(contentAsString, Integer[].class);

        assertThat(categoriesId, hasItemInArray(category.getId()));
    }

    @Test
    public void testGetEmptyCategoriesId() throws Exception {
        ResultActions resultActions = mvc.perform(get("/all-categories"))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        Integer[] categoriesId = objectMapper.readValue(contentAsString, Integer[].class);

        assertEquals(0, categoriesId.length);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testSaveCategory() throws Exception {
        Category category = new Category();
        category.setName(categoryName);
        category.setShortName(categoryShortName);

        mvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isFound());

        List<Category> allCategories = categoryRepository.findAll();

        assertEquals(1, allCategories.size());

        Category savedCategory = allCategories.get(0);

        assertEquals(categoryName, savedCategory.getName());
        assertEquals(categoryShortName, savedCategory.getShortName());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteCategory() throws Exception {
        Category category = new Category();

        categoryRepository.save(category);

        assertEquals(1, categoryRepository.findAll().size());

        mvc.perform(post("/admin/categories/delete/{id}", category.getId()))
                .andExpect(status().isOk());

        assertEquals(0, categoryRepository.findAll().size());
    }
}
