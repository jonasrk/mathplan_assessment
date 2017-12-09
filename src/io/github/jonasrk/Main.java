package io.github.jonasrk;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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

            private Boolean time_of_day_has_conflict(Booking other){
                if (other.end_time.before(this.start_time) | other.end_time.before(this.start_time)){
                    return false;
                } else if (other.start_time.after(this.end_time) | other.start_time.equals(this.end_time)){
                    return false;
                } else {
                    return true;
                }
            }

            public Boolean has_room_conflict(Booking other){
                if (other.weekday.equals(this.weekday) & other.room.equals(this.room)){
                    return time_of_day_has_conflict(other);
                } else {
                    return false;
                }
            }

            public Boolean has_curriculum_conflict(Booking other){
                ArrayList<String> intersection = new ArrayList<String>(this.curricula);
                intersection.retainAll(other.curricula);
                if (intersection.size() > 0 & this.lecture_id != other.lecture_id & other.weekday.equals(this.weekday)){
                    return time_of_day_has_conflict(other);
                } else {
                    return false;
                }
            }

        }

        // create list of all bookings

        List<Booking> bookings = new ArrayList<Booking>();

        NodeList nodeList = document.getElementsByTagName("booking");

        int number_of_bookings = nodeList.getLength();

        for (int i = 0; i < number_of_bookings; i++) {
            Node current_booking = nodeList.item(i);
            String id = ((Element) current_booking.getParentNode().getParentNode()).getElementsByTagName("id").item(0).getTextContent();
            String current_room = ((Element) current_booking.getChildNodes()).getElementsByTagName("room").item(0).getTextContent();
            String current_weekday = ((Element) current_booking.getChildNodes()).getElementsByTagName("weekday").item(0).getTextContent();

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

            String current_start_time = ((Element) current_booking.getChildNodes()).getElementsByTagName("startTime").item(0).getTextContent();
            Date current_start_date = format.parse(current_start_time);


            String current_end_time = ((Element) current_booking.getChildNodes()).getElementsByTagName("endTime").item(0).getTextContent();
            Date current_end_date = format.parse(current_end_time);

            bookings.add(new Booking(id, current_weekday, current_room, current_start_date, current_end_date));

        }

        // add curricula to bookings

        NodeList curricula = document.getElementsByTagName("curriculum");

        int number_of_curricula = curricula.getLength();

        for (int i = 0; i < number_of_curricula; i++) {
            Node current_curriculum = curricula.item(i);
            String name = ((Element) current_curriculum.getChildNodes()).getElementsByTagName("name").item(0).getTextContent();

            NodeList lectures = ((Element) current_curriculum.getChildNodes()).getElementsByTagName("lecture");
            int length = lectures.getLength();

            for (int j = 0; j < length; j ++) {

                String lecture_id = lectures.item(j).getTextContent();

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
