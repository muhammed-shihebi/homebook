package com.mabem.homebook.Model.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;

public class ItemTest {
    /**
     * Testing functions of Item class except the getters and setters. Function names are self explaining.
     */

    Item it, it2;

    @Before
    public void setUp() throws Exception {
        it = new Item("11","example1", 110.0);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void itemWithSameIdAndCodeDifferentAsGivenItem_returnsFalse() {
        it2 = new Item("11","example1", 91.0);
        assertThat(it.equals(it2)).isFalse();
    }

    @Test
    public void itemWithSameIdAndCodeAndPriceEqualToGivenItem_returnsTrue() {
        it2 = new Item("11","example1", 110.0);
        assertThat(it.equals(it2)).isTrue();
    }

    @Test
    public void itemDifferentAsGivenObjectOfDifferentClassWithSimilarAttributes_returnsFalse() {

        class NotItem {
            String id = "11";
            String name = "example1";
            double price = 110.0;
        }
        NotItem nit = new NotItem();
        assertThat(it.equals(nit)).isFalse();
    }

    @Test
    public void itemIdEqualToGivenId_returnsTrue() {
        assertThat(it.getId()).isEqualTo("11");
    }


}