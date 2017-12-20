package io.github.jonasrk;

// define class for bookings

import java.util.ArrayList;
import java.util.Date;

class Booking {
    public String weekday, room, lecture_id, lecture_name;
    public Date start_time, end_time;
    public ArrayList<String> curricula = new ArrayList<String>();

    public Booking(String id, String weekday, String room, Date start_time, Date end_time, String lecture_name) {
        this.lecture_id = id;
        this.weekday = weekday;
        this.room = room;
        this.start_time = start_time;
        this.end_time = end_time;
        this.lecture_name = lecture_name;
    }

    private Boolean time_in_week_has_conflict(Booking other){
        if (other.weekday.equals(this.weekday) == false){
            return false;
        } else if (other.end_time.before(this.start_time) | other.end_time.equals(this.start_time)){
            return false;
        } else if (other.start_time.after(this.end_time) | other.start_time.equals(this.end_time)){
            return false;
        } else {
            return true;
        }
    }

    public Boolean has_room_conflict(Booking other){
        if (other.room.equals(this.room)){
            return time_in_week_has_conflict(other);
        } else {
            return false;
        }
    }

    public Boolean has_curriculum_conflict(Booking other){
        ArrayList<String> intersection = new ArrayList<String>(this.curricula);
        intersection.retainAll(other.curricula);
        if (intersection.size() > 0 & this.lecture_id != other.lecture_id){
            return time_in_week_has_conflict(other);
        } else {
            return false;
        }
    }

}