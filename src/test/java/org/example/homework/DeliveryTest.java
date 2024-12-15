package org.example.homework;


import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DeliveryTest extends AbstractTest{

    @Test
    void getDeliveryCount() throws SQLException {

        //given
        String sql = "SELECT * FROM delivery";
        Statement statement = getConnection().createStatement();
        int count = 0;

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            count++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(DeliveryEntity.class);

        //then
        Assertions.assertEquals(15, count);
    }

    @ParameterizedTest
    @CsvSource({"Yes, Card", "No, NULL"})
    void getHavePaymentMethodIfTaken(String taken, String payment_method) throws SQLException {

        //given
        String sql = "SELECT * FROM delivery WHERE taken='" + taken + "'";
        Statement statement  = getConnection().createStatement();
        String deliveryResult = "";

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            deliveryResult = rs.getString(6);
        }

        //then
        Assertions.assertEquals(payment_method, deliveryResult);
    }
}