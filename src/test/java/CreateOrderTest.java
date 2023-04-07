import client.UserClient;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.User;
import client.UserGenerator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.*;

public class CreateOrderTest {
    UserClient userClient;
    String accessToken;
    Faker faker;

    @Before
    public void setUp() {
        RestAssured.baseURI= "https://stellarburgers.nomoreparties.site/";
        userClient = new UserClient();
        faker = new Faker();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с ингредиентами")
    public void changingOrderAuthorizationAndIngredientsPositiveTest() {
        Map<String, List<String>> ingredients = new HashMap<>();
        List<String> name = new ArrayList<>();
        name.add("61c0c5a71d1f82001bdaaa6d");
        name.add("61c0c5a71d1f82001bdaaa6f");
        ingredients.put("ingredients", name);

        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse authorizationUser = userClient.authorization(user, accessToken);
        int statusCode2 = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode2);

        ValidatableResponse createOrder = userClient.createOrder(ingredients, accessToken);
        int statusCode3 = createOrder.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode3);

        boolean success = createOrder.extract().path("success");
        Assert.assertTrue(success);

        String nameBurger = createOrder.extract().path("name");
        Assert.assertEquals("Бессмертный флюоресцентный бургер", nameBurger);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентами")
    public void changingOrderAuthorizationAndNotIngredientsPositiveTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse authorizationUser = userClient.authorization(user, accessToken);
        int statusCode2 = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode2);

        ValidatableResponse createOrder = userClient.createOrder();
        int statusCode3 = createOrder.extract().statusCode();
        Assert.assertEquals(HTTP_BAD_REQUEST, statusCode3);

        boolean success = createOrder.extract().path("success");
        Assert.assertFalse(success);

        String message = createOrder.extract().path("message");
        Assert.assertEquals("Ingredient ids must be provided", message);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    public void changingOrderNotAuthorizationAndIngredientsPositiveTest() {
        List<String> name = new ArrayList<>();
        name.add("61c0c5a71d1f82001bdaaa6d");
        name.add("61c0c5a71d1f82001bdaaa6f");

        User user = UserGenerator.getRandom();
        Order order = new Order(name);

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse createOrder = userClient.createOrder(order);
        int statusCode2 = createOrder.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode2);

        boolean success = createOrder.extract().path("success");
        Assert.assertTrue(success);

        String nameBurger = createOrder.extract().path("name");
        Assert.assertEquals("Бессмертный флюоресцентный бургер", nameBurger);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void changingOrderNotAuthorizationAndNotIngredientsNegativeTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse createOrder = userClient.createOrder();
        int statusCode2 = createOrder.extract().statusCode();
        Assert.assertEquals(HTTP_BAD_REQUEST, statusCode2);

        boolean success = createOrder.extract().path("success");
        Assert.assertFalse(success);

        String message = createOrder.extract().path("message");
        Assert.assertEquals("Ingredient ids must be provided", message);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хешем ингредиентов")
    public void changingOrderNotAuthorizationAndWithTheWrongIngredientHashNegativeTest() {
        List<String> name = new ArrayList<>();
        name.add(faker.number().digits(4));
        name.add(faker.number().digits(4));

        User user = UserGenerator.getRandom();
        Order order = new Order(name);

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse createOrder = userClient.createOrder(order);
        int statusCode2 = createOrder.extract().statusCode();
        Assert.assertEquals(HTTP_INTERNAL_ERROR, statusCode2);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и неверным хешем ингредиентов")
    public void changingOrderAuthorizationAndWithTheWrongIngredientHashNegativeTest() {
        List<String> name = new ArrayList<>();
        name.add(faker.number().digits(4));
        name.add(faker.number().digits(4));

        User user = UserGenerator.getRandom();
        Order order = new Order(name);

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse authorizationUser = userClient.authorization(user, accessToken);
        int statusCode2 = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode2);

        ValidatableResponse createOrder = userClient.createOrder(order);
        int statusCode3 = createOrder.extract().statusCode();
        Assert.assertEquals(HTTP_INTERNAL_ERROR, statusCode3);
    }

    @After
    public void clear() {
        if(accessToken != null) {
            ValidatableResponse deleteUser = userClient.deleteUser(accessToken);
            int statusCode = deleteUser.extract().statusCode();
            Assert.assertEquals(HTTP_ACCEPTED, statusCode);

            boolean success = deleteUser.extract().path("success");
            Assert.assertTrue(success);
        }
    }

}
