package org.max.seminar;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class ClientTest extends AbstractTest {

    @Test
    void getClients_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM client";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(ClientEntity.class);
        //then
        Assertions.assertEquals(14, countTableSize);
        Assertions.assertEquals(14, query.list().size());
    }

    @ParameterizedTest
    @CsvSource({"1, Иванов", "2, Петров", "3, Сидоров"})
    void getClientById_whenValid_shouldReturn(int id, String lastName) throws SQLException {
        //given
        String sql = "SELECT * FROM client WHERE client_id=" + id;
        Statement stmt  = getConnection().createStatement();
        String name = "";
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            name = rs.getString(3);
        }
        //then
        Assertions.assertEquals(lastName, name);
    }

    @ParameterizedTest
    @CsvSource({"Иванов1", "Петров1", "Сидоров1"})
    void addClient (String lastName) throws SQLException {
        String sql = "SELECT MAX(client_id) FROM client";
        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        short maxid =(short) (rs.getShort(1) + 1);
        getConnection().close();

        ClientEntity client = new ClientEntity();
        client.setClientId(maxid);
        client.setFirstName("");
        client.setApartment("");
        client.setDistrict("");
        client.setHouse("");
        client.setLastName(lastName);
        client.setStreet("");
        client.setPhoneNumber("");

        Session session = getSession();
        session.beginTransaction();
        session.persist(client);
        session.getTransaction().commit();

        final Query queryWhere = session.createQuery("from " + "ClientEntity" + " where client_id=" + maxid);
        System.out.println("executing: " + queryWhere.getQueryString());
        Optional<ClientEntity> entity = queryWhere.uniqueResultOptional();
        Assertions.assertTrue(entity.isPresent());
        Assertions.assertEquals(lastName, entity.get().getLastName());
    }    @ParameterizedTest
    @CsvSource({"Иванов1", "Петров1", "Сидоров1"})
    void addClient2 (String lastName) throws SQLException {
        Session session = getSession();
        String sql = "SELECT MAX(client_id) FROM client";
        final Query queryWhereId = getSession().createSQLQuery(sql);
        int maxid = (int) queryWhereId.uniqueResult() + 1;

        ClientEntity client = new ClientEntity();
        client.setClientId((short) maxid);
        client.setFirstName("");
        client.setApartment("");
        client.setDistrict("");
        client.setHouse("");
        client.setLastName(lastName);
        client.setStreet("");
        client.setPhoneNumber("");


        session.beginTransaction();
        session.persist(client);
        session.getTransaction().commit();

        final Query queryWhere = session.createQuery("from " + "ClientEntity" + " where client_id=" + maxid);
        System.out.println("executing: " + queryWhere.getQueryString());
        Optional<ClientEntity> entity = queryWhere.uniqueResultOptional();
        Assertions.assertTrue(entity.isPresent());
        Assertions.assertEquals(lastName, entity.get().getLastName());
    }
}
