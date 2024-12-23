package org.macales.cart.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;

@DgsComponent
public class HelloWorldFetcher {

    @DgsQuery
    public String hello() {
        return "Hello, world!";
    }
}