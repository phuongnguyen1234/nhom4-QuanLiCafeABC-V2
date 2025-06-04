package com.backend.utils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    // sql date to LocalDate, format dd/MM/yyyy
    public static String convertSQLDateToLocalDate(Date sqlDate) {
        return sqlDate.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static Date convertLocalDateToSQLDate(LocalDate localDate) {
        return Date.valueOf(localDate);
    }
}
