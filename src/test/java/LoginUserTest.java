import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.*;

public class LoginUser {
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
    @DisplayName("Авторизация под существующим пользователем")
    public void loginAsExistingUserPositiveTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");
        String refreshToken = createUser.extract().path("refreshToken");

        ValidatableResponse authorizationUser = userClient.authorization(user, refreshToken);
        int statusCode2 = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode2);

        boolean success = authorizationUser.extract().path("success");
        Assert.assertEquals(true, success);

        String userEmail = authorizationUser.extract().path("user.email");
        Assert.assertEquals(user.getEmail().toLowerCase(), userEmail);
    }

    @Test
    @DisplayName("Авторизация под не существующим пользователем")
    public void loginAsNotExistingUserNegativeTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse authorizationUser = userClient.authorization(user, faker.starTrek().character());
        int statusCode = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_UNAUTHORIZED, statusCode);

        boolean success = authorizationUser.extract().path("success");
        Assert.assertEquals(false, success);

        String message = authorizationUser.extract().path("message");
        Assert.assertEquals("email or password are incorrect", message);

        if(statusCode == 200) {
            accessToken = authorizationUser.extract().path("accessToken");
        }
    }

    @Test
    @DisplayName("Авторизация без пароля")
    public void authorizationWthoutPasswordNegativeTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");
        String refreshToken = createUser.extract().path("refreshToken");

        user.setPassword("");

        ValidatableResponse authorizationUser = userClient.authorization(user, refreshToken);
        int statusCode2 = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_UNAUTHORIZED, statusCode2);

        boolean success = authorizationUser.extract().path("success");
        Assert.assertEquals(false, success);

        String message = authorizationUser.extract().path("message");
        Assert.assertEquals("email or password are incorrect", message);
    }

    @Test
    @DisplayName("Авторизация без логина")
    public void authorizationWthoutLoginNegativeTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");
        String refreshToken = createUser.extract().path("refreshToken");

        user.setEmail("");

        ValidatableResponse authorizationUser = userClient.authorization(user, refreshToken);
        int statusCode2 = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_UNAUTHORIZED, statusCode2);

        boolean success = authorizationUser.extract().path("success");
        Assert.assertEquals(false, success);

        String message = authorizationUser.extract().path("message");
        Assert.assertEquals("email or password are incorrect", message);
    }

    @After
    public void clear() {
        if(accessToken != null) {
            ValidatableResponse deleteUser = userClient.deleteUser(accessToken);
            int statusCode = deleteUser.extract().statusCode();
            Assert.assertEquals(HTTP_ACCEPTED, statusCode);

            boolean success = deleteUser.extract().path("success");
            Assert.assertEquals(true, success);
        }
    }
}
