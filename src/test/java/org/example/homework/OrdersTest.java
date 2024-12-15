package org.example.homework;

import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrdersTest extends AbstractTest{

    @Test
    void getOrdersCount() throws SQLException {

        //given
        String sql = "SELECT * FROM orders";
        Statement statement = getConnection().createStatement();
        int count = 0;

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            count++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(OrdersEntity.class);

        //then
        Assertions.assertEquals(15, count);
    }

    @ParameterizedTest
    @CsvSource({"1, 2024-12-13", "5, 2024-12-13", "15, 2024-12-13"})
    void getDateGetByOrderId(int order_id, String data_get) throws SQLException {

        //given
        String sql = "SELECT * FROM orders WHERE order_id=" + order_id;
        Statement statement  = getConnection().createStatement();
        String ordersResult = "";

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            ordersResult = rs.getString(3);
        }

        //then
        Assertions.assertEquals(data_get, ordersResult);
    }
}
