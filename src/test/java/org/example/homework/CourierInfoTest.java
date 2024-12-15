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
public class CourierInfoTest extends AbstractTest{

    @Test
    @Order(1)
    void getCourierCount() throws SQLException {

        //given
        String sql = "SELECT * FROM courier_info";
        Statement statement = getConnection().createStatement();
        int count = 0;

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            count++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(CourierInfoEntity.class);

        //then
        Assertions.assertEquals(4, count);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"1, foot", "3, car"})
    void getDeliveryTypeByCourierId(int courier_id, String delivery_type) throws SQLException {

        //given
        String sql = "SELECT * FROM courier_info WHERE courier_id=" + courier_id;
        Statement statement  = getConnection().createStatement();
        String nameResult = "";

        //when
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            nameResult = rs.getString(5);
        }

        //then
        Assertions.assertEquals(delivery_type, nameResult);
    }

    @Test
    @Order(3)
    void addCourier() {

        //given
        CourierInfoEntity courierInfo = new CourierInfoEntity();
        courierInfo.setCourierId((short) 5);
        courierInfo.setFirstName("Letty");
        courierInfo.setLastName("Ortiz");
        courierInfo.setPhoneNumber("+ 7 777 777 7777");
        courierInfo.setDeliveryType("car");

        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(courierInfo);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5)
                .addEntity(CourierInfoEntity.class);
        CourierInfoEntity courierInfoEntity = (CourierInfoEntity) query.uniqueResult();

        //then
        Assertions.assertNotNull(courierInfoEntity);
        Assertions.assertEquals("Letty", courierInfoEntity.getFirstName());
        Assertions.assertEquals("Ortiz", courierInfoEntity.getLastName());
        Assertions.assertEquals("+ 7 777 777 7777", courierInfoEntity.getPhoneNumber());
        Assertions.assertEquals("car", courierInfoEntity.getDeliveryType());
    }

    @Test
    @Order(4)
    void deleteCourier() {

        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5)
                .addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierInfoEntity = (Optional<CourierInfoEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(courierInfoEntity.isPresent());

        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(courierInfoEntity.get());
        session.getTransaction().commit();

        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5)
                .addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierInfoEntityAfterDelete = (Optional<CourierInfoEntity>) queryAfterDelete
                .uniqueResultOptional();

        Assertions.assertFalse(courierInfoEntityAfterDelete.isPresent());
    }
}
