import client.UserClient;
import client.UserGenerator;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.MatcherAssert.*;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import model.Order;
import model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;


import static java.net.HttpURLConnection.HTTP_ACCEPTED;
import static java.net.HttpURLConnection.HTTP_OK;

public class ReceivingOrdersFromSpecificUserTest {
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
    @DisplayName("Получение заказов конкретного пользователя авторизованный пользователь")
    public void changingOrderAuthorizationAndIngredientsPositiveTest() {
        List<String> name = new ArrayList<>();
        name.add("61c0c5a71d1f82001bdaaa6d");
        name.add("61c0c5a71d1f82001bdaaa6f");

        User user = UserGenerator.getRandom();
        Order order = new Order(name);

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse authorizationUser = userClient.authorization(user, accessToken);
        int statusCode2 = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode2);

        ValidatableResponse createOrder = userClient.createOrder(order, accessToken);
        int statusCode3 = createOrder.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode3);

        ValidatableResponse getOrder = userClient.getOrderUser(accessToken);

        getOrder.assertThat().statusCode(200)
                .and().body("success", equalTo(true))
                .and().body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя неавторизованный пользователь")
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
        int statusCode3 = createOrder.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode3);

        ValidatableResponse getOrder = userClient.getOrderUser();

        getOrder.assertThat().statusCode(401)
                .and().body("success", equalTo(false))
                .and().body("message", equalTo("You should be authorised"));
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
