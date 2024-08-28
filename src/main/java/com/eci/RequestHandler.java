package com.eci;

public class RequestHandler {

    public static String handleRequest(Request req) {
        if ("/hello".equals(req.getPath())) {
            String name = req.getValues("name", "");
            String age = req.getValues("age", "");
            return "hello " + name + " your age is " + age;
        } else {
            return "404 Not Found";
        }
    }

    public static void main(String[] args) {
        Request req1 = new Request("/hello?name=John&age=30");
        String response1 = handleRequest(req1);
        System.out.println(response1);  // Output: hello John your age is 30

        Request req2 = new Request("/hello?name=Jane");
        String response2 = handleRequest(req2);
        System.out.println(response2);  // Output: hello Jane your age is 

        Request req3 = new Request("/goodbye");
        String response3 = handleRequest(req3);
        System.out.println(response3);  // Output: 404 Not Found
    }
}
