package com.omsworld.familycare.SharedContacts;


import java.util.Comparator;


public class SearchModel {
    public String id;
    public String name;
    public String phone;
    public String comment;
    public String added_date;
    public String added_by;

    public String url;

    public  Comparator<SearchModel> ascending = new Comparator<SearchModel>() {

        public int compare(SearchModel s1, SearchModel s2) {
            String StudentName1 = s1.name.toUpperCase();
            String StudentName2 = s2.name.toUpperCase();
             return StudentName1.compareTo(StudentName2);
        }};

    public  Comparator<SearchModel> descending = new Comparator<SearchModel>() {

        public int compare(SearchModel s1, SearchModel s2) {
            String StudentName1 = s1.name.toUpperCase();
            String StudentName2 = s2.name.toUpperCase();
            return StudentName2.compareTo(StudentName1);
        }};


}
