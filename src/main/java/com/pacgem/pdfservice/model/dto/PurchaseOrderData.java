package com.pacgem.pdfservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderData {
    private String orderNumber;
    private String vendor;
//    private LocalDate orderDate;
    private String orderDate;
//    private List<Item> items;
    private String items;
    private double totalAmount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String name;
        private int quantity;
        private double price;
    }
}