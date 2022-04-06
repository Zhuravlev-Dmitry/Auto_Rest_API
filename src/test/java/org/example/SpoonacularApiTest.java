package org.example;


import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class SpoonacularApiTest {
    private final String apiKey = "f0bef4f38c6b400ba2ccf4012c1c5c61";
    private final String hash="985916c236586c57ac863c7f0a0f5d2a6bcb979d";

    @Test
    void getWithQueryPositiveTest() {
        given()
                .queryParam("apiKey", apiKey)
                .queryParam("query", "bread")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("results[0].title")
                .toString()
                .contains("Bread");

    }
    @Test
    void getWithoutApiKeyNegativeTest() {
        given()
                .queryParam("query", "bread")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
                .then()
                .statusCode(401)
                .extract()
                .jsonPath()
                .get("message")
                .toString()
                .contains("You are not authorized");
    }

    @Test
    void getRecipeCheckBodyPositiveTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("query", "bread")
                .queryParam("addRecipeNutrition", "true")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
                .body()
                .jsonPath();
        assertThat(response.get("results[0].vegan"), is(false));
        assertThat(response.get("results[0].sourceName"), equalTo("Foodista"));
        assertThat(response.get("results[0].title"), containsString("Bread"));
        assertThat(response.get("results[0].nutrition.nutrients[0].name"), equalTo("Calories"));
    }
    @Test
    void getCheckSearchWithTypeTest() {
        given()
                .queryParam("apiKey", apiKey)
                .queryParam("type", "soup")
                .queryParam("number", 3)
                .expect()
                .body("results[0].title", containsString("Soup"))
                .body("offset", equalTo(0))
                .body("number", equalTo(3))
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch");
    }
    @Test
    void getCheckMinSugarRequestTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("query", "cake")
                .queryParam("minSugar", 40)
                .queryParam("number", 1)
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
                .body()
                .jsonPath();
        assertThat(response.get("results[0].title"), containsString("Cake"));
        assertThat(response.get("results[0].nutrition.nutrients[0].name"), equalTo("Sugar"));
        assertThat(response.get("results[0].nutrition.nutrients[0].amount"), equalTo(50.5043F));
    }

    @Test
    void postExampleTest() {
        given()
                .queryParam("apiKey", apiKey)
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("confidence")
                .equals(0.0);
    }

    @Test
    void postWithoutApiKeyNegativeTest() {
        given()
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
                .then()
                .statusCode(401)
                .extract()
                .jsonPath()
                .get("message")
                .toString()
                .contains("You are not authorized");
    }
    @Test
    void postWithTitleTest() {
        given()
                .queryParam("apiKey", apiKey)
                .body("{\n"
                        + " \"1\": t,\n"
                        + " \"2\": i,\n"
                        + " \"3\": t,\n"
                        + " \"4\": l,\n"
                        + " \"5\": e,\n"
                        + " \"6\": =,\n"
                        + " \"7\": C,\n"
                        + " \"8\": o,\n"
                        + " \"9\": r,\n"
                        + " \"10\": n,\n"
                        + " \"11\": ,\n"
                        + " \"12\": a,\n"
                        + " \"13\": v,\n"
                        + " \"14\": o,\n"
                        + " \"15\": c,\n"
                        + " \"16\": a,\n"
                        + " \"16\": d,\n"
                        + " \"16\": o,\n"
                        + " \"16\": ,\n"
                        + " \"16\": s,\n"
                        + " \"16\": a,\n"
                        + " \"16\": l,\n"
                        + " \"16\": s,\n"
                        + " \"16\": a,\n"
                        + "}")
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("cuisine")
                .equals("Mexican");
    }
    @Test
    void postWithIngredientListTest() {
        JsonPath response =  given()
                .queryParam("apiKey", apiKey)
                .body("{\n"
                        + " \"1\": i,\n"
                        + " \"2\": n,\n"
                        + " \"3\": g,\n"
                        + " \"4\": r,\n"
                        + " \"5\": e,\n"
                        + " \"6\": d,\n"
                        + " \"7\": i,\n"
                        + " \"8\": e,\n"
                        + " \"9\": n,\n"
                        + " \"10\": t,\n"
                        + " \"11\": L\n"
                        + " \"12\": i,\n"
                        + " \"13\": s,\n"
                        + " \"14\": t,\n"
                        + " \"15\": =,\n"
                        + " \"16\": 3,\n"
                        + " \"16\": ,\n"
                        + " \"16\": e,\n"
                        + " \"16\": g,\n"
                        + " \"16\": g,\n"
                        + " \"16\": s,\n"
                        + "}")
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
                .body()
                .jsonPath();
        assertThat(response.get("cuisine"), containsString("Mediterranean"));
        assertThat(response.get("cuisines[1]"), equalTo("European"));
        assertThat(response.get("confidence"), equalTo(0.0F));
    }
    @Test
    void postWithRussianTitleTest() {
        given()
                .queryParam("apiKey", apiKey)
                .body("{\n"
                        + " \"1\": t,\n"
                        + " \"2\": i,\n"
                        + " \"3\": t,\n"
                        + " \"4\": l,\n"
                        + " \"5\": e,\n"
                        + " \"6\": =,\n"
                        + " \"7\": с,\n"
                        + " \"8\": у,\n"
                        + " \"9\": п,\n"
                        + "}")
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("cuisine")
                .equals("Mediterranean");
    }

//    @Test
//    void ConnectUser(){
//        String hash = given()
//                .queryParam("apiKey", apiKey)
//                .body("{\n" +
//                                "    \"username\": \"DmitryGB\",\n" +
//                                "    \"firstName\": \"Sunny\",\n" +
//                                "    \"lastName\": \"Horoshevich\",\n" +
//                                "    \"email\": \"dmitry.yand1.mail@yandex.ru\"\n" +
//                                "}")
//                .when()
//                .post("https://api.spoonacular.com/users/connect")
//                .then()
//                .statusCode(200)
//                .extract()
//                .jsonPath()
//                .prettyPeek()
//                .get("hash")
//                .toString();
//    }

    @Test
    void addMealPlanTest() {
        String id = given()
                .queryParam("hash", hash)
                .queryParam("apiKey", apiKey)
                .body("{\n"
                        + " \"date\": 06042022,\n"
                        + " \"slot\": 1,\n"
                        + " \"position\": 0,\n"
                        + " \"type\": \"INGREDIENTS\",\n"
                        + " \"value\": {\n"
                        + " \"ingredients\": [\n"
                        + " {\n"
                        + " \"name\": \"2 eggs\"\n"
                        + " }\n"
                        + " ]\n"
                        + " }\n"
                        + "}")
                .when()
                .post("https://api.spoonacular.com/mealplanner/dmitrygb2/items/")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .prettyPeek()
                .get("id")
                .toString();

        given()
                .queryParam("hash", hash)
                .queryParam("apiKey", apiKey)
                .delete("https://api.spoonacular.com/mealplanner/dmitrygb2/items/" + id)
                .then()
                .statusCode(200);
    }






}
