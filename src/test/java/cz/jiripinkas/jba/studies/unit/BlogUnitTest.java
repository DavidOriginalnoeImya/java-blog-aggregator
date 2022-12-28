package cz.jiripinkas.jba.studies.unit;

import cz.jiripinkas.jba.entity.Blog;
import cz.jiripinkas.jba.entity.User;
import cz.jiripinkas.jba.service.BlogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BlogUnitTest {

    @Autowired
    BlogService blogService;

    private static final String blogName = "Java blog";

    private static final String blogShortName = "java-blog";

    private static final String blogUrl = "https://javablogs.com?name=variables";

    private static final String blogHomepageUrl = "https://javablogs.com";

    private static final byte[] blogIcon = "icon".getBytes();

    @Test
    public void testFindOneBlog() {
        Blog blog = createBlog();

        blogService.save(blog, "");

        assertNotNull(blogService.findOne(blog.getId()));
    }

    @Test
    public void testFindNullBlog() {
        assertNull(blogService.findOne(1));
    }

    @Test
    public void testGetIcon() throws IOException {
        Blog blog = createBlog();

        blogService.save(blog, "");

        assertEquals(new String(blogIcon), new String(blogService.getIcon(blog.getId())));
    }

    @Test
    public void testDeleteBlog() {
        Blog blog = createBlog();

        blogService.save(blog, "");
        blogService.delete(blog);

        assertNull(blogService.findOne(blog.getId()));
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
