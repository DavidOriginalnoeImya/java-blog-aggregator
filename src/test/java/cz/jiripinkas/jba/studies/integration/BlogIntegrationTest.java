package cz.jiripinkas.jba.studies.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jiripinkas.jba.entity.Blog;
import cz.jiripinkas.jba.entity.User;
import cz.jiripinkas.jba.exception.PageNotFoundException;
import cz.jiripinkas.jba.repository.BlogRepository;
import cz.jiripinkas.jba.repository.CategoryRepository;
import cz.jiripinkas.jba.service.BlogService;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BlogIntegrationTest {
    private static final Logger LOGGER = Logger.getLogger(BlogIntegrationTest.class.getName());

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BlogService blogService;

    private static final String blogName = "Java blog";

    private static final String blogShortName = "java-blog";

    private static final String blogUrl = "https://javablogs.com?name=variables";

    private static final String blogHomepageUrl = "https://javablogs.com";

    private static final byte[] blogIcon = "icon".getBytes();

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testExistBlogDetail() throws Exception {
        Blog blog = createBlog();

        blogService.save(blog, "");

        mvc.perform(get("/blog/{shortName}", blogShortName))
                .andExpect(status().isOk())
                .andExpect(model().attribute("title", "Blog: " + blogName))
                .andExpect(model().attribute("blogDetail", true))
                .andExpect(model().attribute("blogShortName", blogShortName))
                .andExpect(model().attribute("nextPage", 1))
                .andExpect(model().attributeExists("blog"));
    }

    @Test
    public void testNotExistBlogDetail() throws Exception {
        mvc.perform(get("/blog/{shortName}", blogShortName))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PageNotFoundException))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBlogShortNameAvailable() throws Exception {
        Blog blog = createBlog();

        blogService.save(blog, "");

        ResultActions resultActions = mvc.perform(get("/blog/shortname/available")
                        .queryParam("shortName", blogShortName))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        String isAvailable = objectMapper.readValue(contentAsString, String.class);

        assertEquals("true", isAvailable);
    }

    @Test
    public void testBlogShortNameNotAvailable() throws Exception {
        ResultActions resultActions = mvc.perform(get("/blog/shortname/available")
                        .queryParam("shortName", blogShortName))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        String isAvailable = objectMapper.readValue(contentAsString, String.class);

        assertEquals("false", isAvailable);
    }



    private static Blog createBlog() {
        Blog blog = new Blog();
        blog.setName(blogName);
        blog.setIcon(blogIcon);
        blog.setShortName(blogShortName);
        blog.setUrl(blogUrl);
        blog.setHomepageUrl(blogHomepageUrl);
        blog.setUser(new User());

        return blog;
    }
}
