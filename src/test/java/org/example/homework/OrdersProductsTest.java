package org.example.homework;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdersProductsTest extends AbstractTest{

    @Test
    @Order(1)
    void getOrdersProductsCount() throws SQLException {

        //given
        String sql = "SELECT * FROM orders_products";
        Statement statement = getConnection().createStatement();
        int count = 0;

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            count++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(OrdersProductsEntity.class);

        //then
        Assertions.assertEquals(23, count);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"1, 1", "1, 4", "1, 10", "7, 6", "15, 8", "15, 7"})
    void getProductsById(short order_id, String product_id) throws SQLException {

        //given
        String sql = "SELECT * FROM orders_products WHERE order_id=" + order_id;
        Statement statement  = getConnection().createStatement();
        String ordersProductsResult = "";

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            ordersProductsResult = rs.getString(2);
        }

        //then
        Assertions.assertEquals(product_id, ordersProductsResult);
    }

    @Test
    @Order(3)
    void addOrdersProducts() {

        //given
        OrdersProductsEntity OrdersProductsInfo = new OrdersProductsEntity();
        OrdersProductsInfo.setOrderId((short) 16);
        OrdersProductsInfo.setProductId((short) 9);
        OrdersProductsInfo.setQuantity((short) 2);

        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(OrdersProductsInfo);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM orders_products WHERE order_id=" + 16)
                .addEntity(OrdersProductsEntity.class);
        OrdersProductsEntity ordersProductsEntity = (OrdersProductsEntity) query.uniqueResult();

        //then
        Assertions.assertNotNull(ordersProductsEntity);
        Assertions.assertEquals(16, ordersProductsEntity.getOrderId());
        Assertions.assertEquals(9, ordersProductsEntity.getProductId());
        Assertions.assertEquals(2, ordersProductsEntity.getQuantity());
    }

    @Test
    @Order(4)
    void deleteOrdersProducts() {

        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM orders_products WHERE order_id=" + 16)
                .addEntity(OrdersProductsEntity.class);
        Optional<OrdersProductsEntity> products = (Optional<OrdersProductsEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(products.isPresent());

        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(products.get());
        session.getTransaction().commit();

        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM orders_products WHERE order_id=" + 16)
                .addEntity(OrdersProductsEntity.class);
        Optional<OrdersProductsEntity> productsAfterDelete = (Optional<OrdersProductsEntity>) queryAfterDelete
                .uniqueResultOptional();
        Assertions.assertFalse(productsAfterDelete.isPresent());
    }
}
