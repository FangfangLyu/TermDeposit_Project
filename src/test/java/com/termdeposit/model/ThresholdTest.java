package com.termdeposit.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ThresholdTest {
    @Test
    public void numCompare(){
        //True if provided is less than or equals to
        Threshold t = new Threshold((double)24, "Integer");

        assertTrue( !t.compare(30) ); //false
        assertTrue( t.compare(23) ); //true
        assertTrue( t.compare(24) ); //true

        //double
        assertTrue( !t.compare(30.0) ); //false
        assertTrue( t.compare(23.0) ); //true
        assertTrue( t.compare(24.0) ); //true
 
    }

    @Test
    public void categoricalCompare(){
        //True if provided is less than or equals to
        Threshold t = new Threshold("married", "String");

        assertTrue( t.compare("married") ); //true
        assertTrue( !t.compare("divorced") ); //false
        assertTrue( !t.compare("single") ); //false
 
    }

    @Test
    public void mismatchCompare(){
        Threshold t1 = new Threshold((double)24, "Integer");
        Threshold t2 = new Threshold("String","String");

        assertTrue( !t1.compare("married") ); //false
        assertTrue( !t1.compare(true) ); //false
        assertTrue( !t2.compare(19.5) ); //false
    }
    
    @Test
    public void compareNull(){
        Threshold t1 = new Threshold("married", "String");
        assertTrue( !t1.compare(null) ); //false
    }

}
