package io.github.jonasrk;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ParseException {

        File file = new File("timetable.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);


        // define class for bookings

        class Booking {
            public String weekday, room, lecture_id;
            public Date start_time, end_time;
            public ArrayList<String> curricula = new ArrayList<String>();

            public Booking(String id, String weekday, String room, Date start_time, Date end_time) {
                this.lecture_id = id;
                this.weekday = weekday;
                this.room = room;
                this.start_time = start_time;
                this.end_time = end_time;
            }

            public Boolean has_room_conflict(Booking other){
                if (other.weekday.equals(this.weekday) & other.room.equals(this.room)){
                    if (other.end_time.before(this.start_time) | other.end_time.before(this.start_time)){
                        return false;
                    } else if (other.start_time.after(this.end_time) | other.start_time.equals(this.end_time)){
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }

            public Boolean has_curriculum_conflict(Booking other){
                ArrayList<String> intersection = new ArrayList<String>(this.curricula);
                intersection.retainAll(other.curricula);
                if (intersection.size() > 0 & this.lecture_id != other.lecture_id & other.weekday.equals(this.weekday)){
                    if (other.end_time.before(this.start_time) | other.end_time.before(this.start_time)){
                        return false;
                    } else if (other.start_time.after(this.end_time) | other.start_time.equals(this.end_time)){
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }

        }

        // create list of all bookings

        List<Booking> bookings = new ArrayList<Booking>();

        int number_of_bookings = document.getElementsByTagName("booking").getLength();

        for (int i = 0; i < number_of_bookings; i++) {
            Node current_booking = document.getElementsByTagName("booking").item(i);
            String id = current_booking.getParentNode().getParentNode().getChildNodes().item(1).getTextContent();
            String current_room = current_booking.getChildNodes().item(1).getTextContent();
            String current_weekday = current_booking.getChildNodes().item(3).getTextContent();

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

            String current_start_time = current_booking.getChildNodes().item(5).getTextContent();
            Date current_start_date = format.parse(current_start_time);


            String current_end_time = current_booking.getChildNodes().item(7).getTextContent();
            Date current_end_date = format.parse(current_end_time);

            bookings.add(new Booking(id, current_weekday, current_room, current_start_date, current_end_date));

        }

        // add curricula to bookings

        int number_of_curricula = document.getElementsByTagName("curriculum").getLength();

        for (int i = 0; i < number_of_curricula; i++) {
            Node current_curriculum = document.getElementsByTagName("curriculum").item(i);
            String name = current_curriculum.getChildNodes().item(1).getTextContent();

            int length = current_curriculum.getChildNodes().getLength();

            for (int j = 3; j < length; j += 2) {

                String lecture_id = current_curriculum.getChildNodes().item(j).getTextContent();

                bookings.forEach(x -> {
                    if (x.lecture_id.equals(lecture_id)) {
                        x.curricula.add(name);
                    }
                });

            }



        }

        // compare all lectures in a room with one another and check for conflicts

        for (int i = 0; i < bookings.size(); i++) {
            for (int j = i + 1; j < bookings.size(); j++) {
                if (bookings.get(i).has_room_conflict(bookings.get(j))) {
                    System.out.println("Room Conflict:");
                    System.out.println(bookings.get(i).lecture_id);
                    System.out.println(bookings.get(j).lecture_id);
                }
                if (bookings.get(i).has_curriculum_conflict(bookings.get(j))) {
                    System.out.println("Curriculum Conflict:");
                    System.out.println(bookings.get(i).lecture_id);
                    System.out.println(bookings.get(j).lecture_id);
                }
            }
        }
    }
}
