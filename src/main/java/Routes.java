import java.io.OutputStream;

public class Routes {

    public Routes() {}

    @WebRoute(path = "/test1")
    public String test1(){
        System.out.println("in test 1 route");

        return "returning test1";}

    @WebRoute(path = "/test2")
    public String test2(){
        System.out.println("in test 2 route");
        return  "returning test2";}
}
