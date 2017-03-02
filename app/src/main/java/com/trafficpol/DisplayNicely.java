package com.trafficpol;

/**
 * Created by Emilia on 3/29/2016.
 */
public class DisplayNicely {

    public DisplayNicely() {
    }

    public static String displayMonth(String number_of_month){
        String monthString;
        switch (number_of_month) {
            case "01":  monthString = "January";
                break;
            case "02":  monthString = "February";
                break;
            case "03":  monthString = "March";
                break;
            case "04":  monthString = "April";
                break;
            case "05":  monthString = "May";
                break;
            case "06":  monthString = "June";
                break;
            case "07":  monthString = "July";
                break;
            case "08":  monthString = "August";
                break;
            case "09":  monthString = "September";
                break;
            case "10": monthString = "October";
                break;
            case "11": monthString = "November";
                break;
            case "12": monthString = "December";
                break;
            default: monthString = "Invalid month";
                break;
        }
        return(monthString);
    }
}
