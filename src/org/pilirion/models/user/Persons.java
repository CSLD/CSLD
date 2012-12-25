package org.pilirion.models.user;

import org.pilirion.utils.Strings;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 8.7.12
 * Time: 9:34
 */
public class Persons {
    private Connection db;

    public Persons(Connection db){
        this.db = db;
    }

    public Person getPersonById(int id){
        String sql = "select * from person where id = " + id;
        List<Person> allPersons = getPersonsFromDb(sql);
        if(allPersons != null && allPersons.size() > 0){
            return allPersons.get(0);
        }
        return null;
    }

    public List<Person> getPersonsFromDb(String sql){
        List<Person> allPersons = new ArrayList<Person>();
        try {
            Statement stmt = db.createStatement();
            ResultSet persons = stmt.executeQuery(sql);
            Person person;
            String firstName, lastName, nickName, email, image, address, description;
            Date birthDate;
            int id;
            while(persons.next()){
                id = persons.getInt("id");
                firstName = persons.getString("first_name");
                lastName = persons.getString("last_name");
                nickName = persons.getString("nickname");
                birthDate = persons.getDate("birth_date", new GregorianCalendar());
                email = persons.getString("email");
                image = persons.getString("image");
                address = persons.getString("address");
                description = persons.getString("description");

                person = new Person(id, firstName, lastName, birthDate, nickName, email, image,
                        address, description);
                allPersons.add(person);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
        return allPersons;
    }

    public Person getPersonByMail(String mail){
        String sql = "select * from person where email = " + mail;
        List<Person> allPersons = getPersonsFromDb(sql);
        if(allPersons != null && allPersons.size() > 0){
            return allPersons.get(0);
        }
        return null;
    }

    public int insertPerson(Person person){
        String birthDate = (person.getBirthDate() != null) ? "'" + person.getBirthDate().toString() + "'" : null;
        String sql = "insert into person (first_name, last_name, nickname, birth_date, email, image, address, description) values ('" +
                person.getFirstName() + "','"+person.getLastName()+"','"+person.getNickName()+"',"+
                birthDate+",'"+person.getEmail()+"','"+person.getImage()+"','"+person.getAddress()+"'," +
                "'"+person.getDescription()+"') returning id";
        try {
            Statement stmt = db.createStatement();
            ResultSet persons = stmt.executeQuery(sql);
            while(persons.next()){
                return persons.getInt(1);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            return -1;
        }
        return -1;
    }

    public boolean editPerson(Person person){
        if(person.getId() == -1){
            return false;
        }
        String sql = "update person set  ";
        if(person.getBirthDate() != null && !person.getBirthDate().equals("")){
            sql += "birth_date = '"+person.getBirthDate()+"',";
        }
        if(person.getFirstName() != null && !person.getFirstName().equals("")){
            sql += "first_name = '"+person.getFirstName()+"',";
        }
        if(person.getLastName() != null && !person.getLastName().equals("")){
            sql += "last_name = '"+person.getLastName()+"',";
        }
        if(person.getNickName() != null && !person.getNickName().equals("")){
            sql += "nickname = '"+person.getNickName()+"',";
        }
        if(person.getEmail() != null && !person.getEmail().equals("")){
            sql += "email = '"+person.getEmail()+"',";
        }
        if(person.getImage() != null && !person.getImage().equals("")) {
            sql += "image = '" + person.getImage()+ "',";
        }
        if(person.getAddress() != null && !person.getAddress().equals("")){
            sql += "address = '" + person.getAddress() + "',";
        }
        if(person.getDescription() != null && !person.getDescription().equals("")){
            sql += "description = '" + person.getDescription() + "',";
        }
        sql = Strings.removeLast(sql);
        sql += " where id = " + person.getId();
        System.out.println(sql);
        try {
            Statement stmt = db.createStatement();
            stmt.execute(sql);
            return true;
        } catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public List<Person> getPersonByName(String authorName, String authorLastName) {
        String sql = "select * from person where first_name = '" + authorName + "' and last_name = '" + authorLastName + "'";
        return getPersonsFromDb(sql);
    }
}
