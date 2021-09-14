package ru.job4j;

import org.junit.Assert;
import org.junit.Test;

public class TigerTest {

    @Test
    public void test() {
        Assert.assertEquals(1, new Tiger().someLogic());
    }

}