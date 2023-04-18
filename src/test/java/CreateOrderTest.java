import base.BaseTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.User;
import client.UserGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.*;

public class CreateOrderTest extends BaseTest {

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
        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse authorizationUser = userClient.authorization(user, accessToken);
        int statusCode2 = authorizationUser.extract().statusCode();

        ValidatableResponse createOrder = userClient.createOrder(ingredients, accessToken);
        int statusCode3 = createOrder.extract().statusCode();
        boolean success = createOrder.extract().path("success");
        String nameBurger = createOrder.extract().path("name");

        Assert.assertEquals(HTTP_OK, statusCode);
        Assert.assertEquals(HTTP_OK, statusCode2);
        Assert.assertEquals(HTTP_OK, statusCode3);
        Assert.assertTrue(success);
        Assert.assertEquals("Бессмертный флюоресцентный бургер", nameBurger);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентами")
    public void changingOrderAuthorizationAndNotIngredientsPositiveTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        accessToken = createUser.extract().path("accessToken");
        userClient.authorization(user, accessToken);

        ValidatableResponse createOrder = userClient.createOrder();
        int statusCode = createOrder.extract().statusCode();
        boolean success = createOrder.extract().path("success");
        String message = createOrder.extract().path("message");

        Assert.assertEquals(HTTP_BAD_REQUEST, statusCode);
        Assert.assertFalse(success);
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
        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse createOrder = userClient.createOrder(order);
        int statusCode = createOrder.extract().statusCode();
        boolean success = createOrder.extract().path("success");
        String nameBurger = createOrder.extract().path("name");

        Assert.assertEquals(HTTP_OK, statusCode);
        Assert.assertTrue(success);
        Assert.assertEquals("Бессмертный флюоресцентный бургер", nameBurger);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void changingOrderNotAuthorizationAndNotIngredientsNegativeTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse createOrder = userClient.createOrder();
        int statusCode = createOrder.extract().statusCode();
        boolean success = createOrder.extract().path("success");
        String message = createOrder.extract().path("message");

        Assert.assertEquals(HTTP_BAD_REQUEST, statusCode);
        Assert.assertFalse(success);
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
        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse createOrder = userClient.createOrder(order);
        int statusCode2 = createOrder.extract().statusCode();

        Assert.assertEquals(HTTP_OK, statusCode);
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
        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse authorizationUser = userClient.authorization(user, accessToken);
        int statusCode = authorizationUser.extract().statusCode();

        ValidatableResponse createOrder = userClient.createOrder(order);
        int statusCode2 = createOrder.extract().statusCode();

        Assert.assertEquals(HTTP_OK, statusCode);
        Assert.assertEquals(HTTP_INTERNAL_ERROR, statusCode2);
    }

}
