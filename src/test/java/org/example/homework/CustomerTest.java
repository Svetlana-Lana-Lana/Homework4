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
public class CustomerTest extends AbstractTest{

    @Test
    @Order(1)
    void getCustomersCount() throws SQLException {

        //given
        String sql = "SELECT * FROM customers";
        Statement statement = getConnection().createStatement();
        int count = 0;

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            count++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(CustomersEntity.class);

        //then
        Assertions.assertEquals(15, count);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"1, Smith", "2, Brown", "9, Lorenson", "12, Rild", "15, Watson"})
    void getCustomerLastNameById(int customer_id, String last_name) throws SQLException {

        //given
        String sql = "SELECT * FROM customers WHERE customer_id=" + customer_id;
        Statement statement  = getConnection().createStatement();
        String nameResult = "";

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            nameResult = rs.getString(3);
        }

        //then
        Assertions.assertEquals(last_name, nameResult);
    }

    @Test
    @Order(3)
    void addCustomer() {

        //given
        CustomersEntity customerInfo = new CustomersEntity();
        customerInfo.setCustomerId((short) 16);
        customerInfo.setFirstName("Bridget");
        customerInfo.setLastName("Jones");
        customerInfo.setPhoneNumber("+7 333 333 3333");
        customerInfo.setDistrict("Southwark");
        customerInfo.setStreet("Bedale");
        customerInfo.setHouse("8");
        customerInfo.setApartment("11");

        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(customerInfo);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM customers WHERE customer_id=" + 16)
                .addEntity(CustomersEntity.class);
        CustomersEntity customersEntity = (CustomersEntity) query.uniqueResult();

        //then
        Assertions.assertNotNull(customersEntity);
        Assertions.assertEquals("Bridget", customersEntity.getFirstName());
        Assertions.assertEquals("Jones", customersEntity.getLastName());
        Assertions.assertEquals("+7 333 333 3333", customersEntity.getPhoneNumber());
        Assertions.assertEquals("Southwark", customersEntity.getDistrict());
        Assertions.assertEquals("Bedale", customersEntity.getStreet());
        Assertions.assertEquals("8", customersEntity.getHouse());
        Assertions.assertEquals("11", customersEntity.getApartment());
    }

    @Test
    @Order(4)
    void deleteCustomer() {

        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM customers WHERE customer_id=" + 16)
                .addEntity(CustomersEntity.class);
        Optional<CustomersEntity> customersEntity = (Optional<CustomersEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(customersEntity.isPresent());

        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(customersEntity.get());
        session.getTransaction().commit();

        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM customers WHERE customer_id=" + 16)
                .addEntity(CustomersEntity.class);
        Optional<CustomersEntity> customersEntityAfterDelete = (Optional<CustomersEntity>) queryAfterDelete
                .uniqueResultOptional();
        Assertions.assertFalse(customersEntityAfterDelete.isPresent());
    }
}
