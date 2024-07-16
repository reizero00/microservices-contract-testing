package com.rahulshettyacademy;

import java.util.HashMap;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rahulshettyacademy.controller.LibraryController;
import com.rahulshettyacademy.controller.ProductsPrices;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "CoursesCatalogue")
public class PactConsumerTest {
    
    @Autowired
    private LibraryController libraryController;

    @Pact(consumer="BooksCatalogue")
    public RequestResponsePact PactAllCoursesDetailsConfig(PactDslWithProvider builder) {
        return builder.given("courses exist")
            .uponReceiving("getting all courses details")
            .path("/allCourseDetails")
            .willRespondWith()
                .status(200)
                .body(PactDslJsonArray.arrayMinLike(2)
                    .stringType("course_name")
                    .stringType("id")
                    .integerType("price", 13)
                    .stringType("category")
                    .closeObject()).toPact();
    }

    @Test
    @PactTestFor(pactMethod="PactAllCoursesDetailsConfig",port = "9999")
    public void testAllProductsSum() throws JsonMappingException, JsonProcessingException {
        libraryController.setBaseUrl("http://localhost:9999");
        ProductsPrices actualProductPricesResponse = libraryController.getProductPrices();
        
        Assertions.assertEquals(expectedProductPricesResponse(), actualProductPricesResponse);

    }

    public static HashMap<String, Integer> expectedProductPricesResponse() {

        HashMap<String, Integer> productPrices = new HashMap<String, Integer>();

        productPrices.put("booksPrice", 250);
        productPrices.put("coursesPrice", 102);

        return productPrices;
    }

}