package cz.jiripinkas.jba.studies.unit;

import cz.jiripinkas.jba.controller.CategoryController;
import cz.jiripinkas.jba.entity.Category;
import cz.jiripinkas.jba.service.AllCategoriesService;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebMvcTest(CategoryController.class)
public class CategoryUnitTest {

//    @Autowired
//    AllCategoriesService allCategoriesService;

//    @Autowired
//    MockMvc mvc;

    @LocalServerPort
    int port;

    private static final Category testCategory = new Category();


    @Before
    public void init() {
        RestAssured.port = port;
        initTestCategory();
    }

    @Test
    public void testGetCategoriesId() {
        given()
                .body(testCategory).contentType("application/json")
                .when().post("/admin/categories")
                .then().statusCode(204);

        when().get("/all-categories").then().statusCode(200);
    }

    private static void initTestCategory() {
        testCategory.setName("Test category");
        testCategory.setShortName("tc");
    }
}
