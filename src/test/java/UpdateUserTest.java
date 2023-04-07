import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;
import static java.net.HttpURLConnection.HTTP_OK;

public class UpdateUser {
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
    @DisplayName("Изменение данных пользователя с авторизацией (изменён е-мейл)")
    public void changingUserDataWithAuthorizationEmailPositiveTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse authorizationUser = userClient.authorization(user, accessToken);
        int statusCode2 = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode2);

        ValidatableResponse updateUser = userClient.update(UserCredentional.updateEmailCred(user, faker.name().firstName() + faker.number().digits(3) + "@yandex.ru"), accessToken);
        int statusCode3 = updateUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode3);

        boolean success = updateUser.extract().path("success");
        Assert.assertEquals(true, success);

        String newUserEmail = updateUser.extract().path("user.email");
        Assert.assertEquals(user.getEmail().toLowerCase(), newUserEmail);
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией (изменён пароль)")
    public void changingUserDataWithAuthorizationPasswordPositiveTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse authorizationUser = userClient.authorization(user, accessToken);
        int statusCode2 = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode2);

        ValidatableResponse updateUser = userClient.update(UserCredentional.updatePasswordCred(user,  faker.number().digits(10)), accessToken);
        int statusCode3 = updateUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode3);

        boolean success = updateUser.extract().path("success");
        Assert.assertEquals(true, success);

        String newUserEmail = updateUser.extract().path("user.email");
        Assert.assertEquals(user.getEmail().toLowerCase(), newUserEmail);

        Response response = updateUser.extract().response();
        System.out.println(response.getBody().asString());
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией (изменено имя)")
    public void changingUserDataWithAuthorizationNamePositiveTest() {
        User user = UserGenerator.getRandom();

        ValidatableResponse createUser = userClient.createUser(user);
        int statusCode = createUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode);

        accessToken = createUser.extract().path("accessToken");

        ValidatableResponse authorizationUser = userClient.authorization(user, accessToken);
        int statusCode2 = authorizationUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode2);

        ValidatableResponse updateUser = userClient.update(UserCredentional.updateNameCred(user,  faker.name().username()), accessToken);
        int statusCode3 = updateUser.extract().statusCode();
        Assert.assertEquals(HTTP_OK, statusCode3);

        boolean success = updateUser.extract().path("success");
        Assert.assertEquals(true, success);

        String newUserEmail = updateUser.extract().path("user.name");
        Assert.assertEquals(user.getName().toLowerCase(), newUserEmail);

        Response response = updateUser.extract().response();
        System.out.println(response.getBody().asString());
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
