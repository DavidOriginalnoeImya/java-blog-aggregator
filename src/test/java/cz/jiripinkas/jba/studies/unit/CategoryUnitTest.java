package cz.jiripinkas.jba.studies.unit;

import cz.jiripinkas.jba.controller.CategoryController;
import cz.jiripinkas.jba.service.AllCategoriesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.RestAssured.when;

@RunWith(SpringRunner.class)
@SpringBootTest
//@WebMvcTest(CategoryController.class)
public class CategoryUnitTest {

//    @Autowired
//    AllCategoriesService allCategoriesService;

//    @Autowired
//    MockMvc mvc;

    @Test
    public void testGetCategoriesId() {
        when().get("/all-categories").then().statusCode(200);
    }
}
