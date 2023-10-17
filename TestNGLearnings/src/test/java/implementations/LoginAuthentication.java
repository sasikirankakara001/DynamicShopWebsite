package implementations;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.*;

import java.io.File;

import io.restassured.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import org.hamcrest.*;
import org.testng.annotations.Test;

public class LoginAuthentication {
	public static void main(String[] args) {
		RequestSpecification req = new RequestSpecBuilder().setContentType(ContentType.JSON)
				.setBaseUri("https://rahulshettyacademy.com/").build();
		ResponseSpecification resp = new ResponseSpecBuilder().expectContentType(ContentType.JSON).expectStatusCode(200)
				.build();

		String extractDetails = given().log().all().spec(req)
				.body("    {\r\n" + "    \"userEmail\": \"sasikiran2213@gmail.com\",\r\n"
						+ "    \"userPassword\": \"Sasikiran@2213\"\r\n" + "}")
				.when().post("api/ecom/auth/login").then().spec(resp).log().all().extract().asString();

		JsonPath json = new JsonPath(extractDetails);
		String token = json.getString("token");
		String userId = json.getString("userId");
		System.out.println(token);
		
		RequestSpecification reqAddProductBaseReq= new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com/")
				.addHeader("Authorization", token).build();
		RequestSpecification reqAddProduct = given().log().all().spec(reqAddProductBaseReq)
				.param("productName","Laptop").param("productAddedBy",userId).param("productCategory","fashion")
				.param("productSubCategory","shirts").param("productPrice", 15000).param("productDescription","Lenova")
				.param("productFor","men").multiPart("productImage",new File("C:\\Users\\kakar\\Downloads\\snap5.png"));
		String addProductResponse = reqAddProduct.when().post("api/ecom/product/add-product").then().log().all().extract().asString();
		JsonPath js = new JsonPath(addProductResponse);
		String productId = js.getString("productId");
		System.out.println(productId);
		
		given().log().all().baseUri("https://rahulshettyacademy.com/").header("Authorization",token).contentType(ContentType.JSON).body("{\r\n"
				+ "    \"orders\": [\r\n"
				+ "        {\r\n"
				+ "            \"country\": \"India\",\r\n"
				+ "            \"productOrderedId\": \""+productId+"\"\r\n"
				+ "        }\r\n"
				+ "    ]\r\n"
				+ "}").when().post("api/ecom/order/create-order").then().log().all().extract().asString();
		
		given().spec(reqAddProductBaseReq).when().delete("api/ecom/product/delete-product/"+productId+"").then().log().all().extract().asString();
		
		
	}

}
