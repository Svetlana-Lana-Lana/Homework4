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
public class ProductTest extends AbstractTest{

    @Test
    @Order(1)
    void getProductsCount() throws SQLException {

        //given
        String sql = "SELECT * FROM products";
        Statement statement = getConnection().createStatement();
        int count = 0;

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            count++;
        }
        final Query query = (Query) getSession().createSQLQuery(sql).addEntity(ProductsEntity.class);
        Assertions.assertEquals(10, count);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"GOJIRA ROLL, 300.0", "ZONIE ROLL, 254.0", "MINERAL WATER, 50.0"})
    void getPriceByMenu(String menu_name, String price) throws SQLException {

        //given
        String sql = "SELECT * FROM products WHERE menu_name='" + menu_name + "'";
        Statement statement  = getConnection().createStatement();
        String ProductResult = "";

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            ProductResult = rs.getString(3);
        }

        //then
        Assertions.assertEquals(price, ProductResult);
    }

    @Test
    @Order(3)
    void addProduct() {

        //given
        ProductsEntity productsInfo = new ProductsEntity();
        productsInfo.setProductId((short) 11);
        productsInfo.setMenuName("COCA COLA");
        productsInfo.setPrice("115.0");

        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(productsInfo);
        session.getTransaction().commit();

        final Query query = (Query) getSession()
                .createSQLQuery("SELECT * FROM products WHERE product_id=" + 11)
                .addEntity(ProductsEntity.class);
        ProductsEntity productsEntity = (ProductsEntity) query.uniqueResult();

        //then
        Assertions.assertNotNull(productsEntity);
        Assertions.assertEquals("COCA COLA", productsEntity.getMenuName());
        Assertions.assertEquals("115.0", productsEntity.getPrice());
    }


    @Test
    @Order(4)
    void deleteProduct() {

        //given
        final Query query = (Query) getSession()
                .createSQLQuery("SELECT * FROM products WHERE product_id=" + 11)
                .addEntity(ProductsEntity.class);
        Optional<ProductsEntity> productsEntity = (Optional<ProductsEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(productsEntity.isPresent());

        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(productsEntity.get());
        session.getTransaction().commit();

        //then
        final Query queryAfterDelete = (Query) getSession()
                .createSQLQuery("SELECT * FROM products WHERE product_id=" + 11)
                .addEntity(ProductsEntity.class);
        Optional<ProductsEntity> productsEntityAfterDelete = (Optional<ProductsEntity>) queryAfterDelete
                .uniqueResultOptional();
        Assertions.assertFalse(productsEntityAfterDelete.isPresent());
    }
}
