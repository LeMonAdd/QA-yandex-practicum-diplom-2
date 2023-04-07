package client;

import io.restassured.response.ValidatableResponse;
import model.Order;
import model.User;

import javax.print.attribute.standard.OrientationRequested;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserClient {

    private final String CREATE_USER_URI = "api/auth/register/";
    private final String DELETE_USER_URI = "api/auth/user/";
    private final String AUTHORIZATION_USER_URI = "api/auth/login/";
    private final String UPDATE_USER_URI = "api/auth/user/";
    private final String CREATE_ORDER_URI = "api/orders/";
    private final String GET_ORDER_USER_URI = "api/orders/";

    public ValidatableResponse createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(CREATE_USER_URI)
                .then();
    }

    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .header("Content-type", "Application/json")
                .and()
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER_URI)
                .then();
    }

    public ValidatableResponse authorization(User user, String token) {
        return given()
                .header("Content-type", "Application/json")
                .and()
                .header("Authorization", token)
                .and()
                .body(user)
                .when()
                .post(AUTHORIZATION_USER_URI)
                .then();
    }

    public ValidatableResponse update(UserCredentional userCred, String token) {
        return given()
                .header("Content-type", "Application/json")
                .and()
                .header("Authorization", token)
                .and()
                .body(userCred)
                .when()
                .patch(UPDATE_USER_URI)
                .then();
    }

    public ValidatableResponse update(UserCredentional userCred) {
        return given()
                .header("Content-type", "Application/json")
                .and()
                .body(userCred)
                .when()
                .patch(UPDATE_USER_URI)
                .then();
    }

    public ValidatableResponse createOrder(Map<String, List<String>> order, String token) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", token)
                .and()
                .body(order)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    public ValidatableResponse createOrder(Order order, String token) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", token)
                .and()
                .body(order)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    public ValidatableResponse createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    public ValidatableResponse createOrder() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    public ValidatableResponse getOrderUser(String token) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", token)
                .when()
                .get(GET_ORDER_USER_URI)
                .then();
    }

    public ValidatableResponse getOrderUser() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .get(GET_ORDER_USER_URI)
                .then();
    }

}
