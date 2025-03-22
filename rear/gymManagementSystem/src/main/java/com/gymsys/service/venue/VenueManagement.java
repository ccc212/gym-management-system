package com.gymsys.service.venue;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
class Venue {
    private String id;
    private String name;
    private double pricePerHour;
    private boolean isAvailable;
    // 其他属性
}

class VenueManagement {
    private List<Venue> venues = new ArrayList<>();


    // 添加场地
    public void addVenue(Venue venue) {
        venues.add(venue);
    }

    // 删除场地
    public void removeVenue(String venueId) {
        venues.removeIf(venue -> venue.getId().equals(venueId));
    }


    // 场地使用（有开始计时，结束计时，一卡通付费）
    public double useVenue(String venueId, String cardNumber, LocalDateTime startTime, LocalDateTime endTime) {
        Venue venue = findVenueById(venueId);
        if (venue != null) {
            double hours = (endTime.toEpochSecond(java.time.ZoneOffset.UTC) - startTime.toEpochSecond(java.time.ZoneOffset.UTC)) / 3600.0;
            double cost = hours * venue.getPricePerHour();
            // 可以添加付费逻辑
            return cost;
        }
        return 0;
    }



    // 场地收费标准查询
    public double getVenuePrice(String venueId) {
        Venue venue = findVenueById(venueId);
        if (venue != null) {
            return venue.getPricePerHour();
        }
        return 0;
    }

    // 场馆公告（简单示例，可根据需求扩展）
    public String getAnnouncement() {
        return "欢迎使用场馆，请遵守相关规定。";
    }

    private Venue findVenueById(String venueId) {
        for (Venue venue : venues) {
            if (venue.getId().equals(venueId)) {
                return venue;
            }
        }
        return null;
    }
}

@Data
class Reservation {
    private String cardNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Reservation(String cardNumber, LocalDateTime startTime, LocalDateTime endTime) {
        this.cardNumber = cardNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}