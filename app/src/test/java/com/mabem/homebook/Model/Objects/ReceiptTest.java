package com.mabem.homebook.Model.Objects;

import com.mabem.homebook.Model.Item;
import com.mabem.homebook.Model.Receipt;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

public class ReceiptTest {
    private String id = "";
    private String name = "";
    private Date date;
    private Double total = 0.0;
    private ArrayList<Item> items;
    private String memberName;
    private String memberId;
    private Receipt r, r2;
    private Instant inst;

    @Before
    public void setUp() throws Exception {
        id = "testid";
        name = "testname";
        total = 10.0;
        inst = Instant.now();
        date = Date.from(inst);
        memberName = "testmembername";
        memberId = "testmemberid";
        r = new Receipt(id, name, date, total, memberName, memberId);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void receiptWithSameIdAndNameAndDateEqualToGivenReceipt_returnsTrue() {
        Double newTotal = total + 12;
        r2 = new Receipt(id,name, date, newTotal, "not" + memberName, "not" + memberId);

        assertThat(r.equals(r2)).isTrue();
    }
    @Test
    public void receiptDifferentAsGivenObjectOfDifferentClassWithSimilarAttributes_returnsFalse() {

        class NotReceipt {
            String id = "";
            String name = "";
            Date date;
            Double total = 0.0;
            ArrayList<Item> items;
            String memberName;
            String memberId;
        }
        NotReceipt nr = new NotReceipt();
        assertThat(r.equals(nr)).isFalse();

    }


}
