package id.atv.pointline.web;

import id.atv.pointline.jpa.Demo;

/**
 * Created by pancara on 11/2/16.
 */
public class DemoApp {
    public static void main(String[] args) {
        Demo demo = new Demo("Badu");
        System.out.println(demo.getName());
    }
}
