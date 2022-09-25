package com.example.expensify.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collation = "exportData")
public class ExportTemplate {


    @JsonProperty("Merchant")
    private String Merchant;

    @JsonProperty("Amount")
    private String Amount;

    @JsonProperty("Category")
    private String Category;

    @JsonProperty("ReportNumber")
    private String ReportNumber;

    @JsonProperty("ExpenseNumber")
    private String ExpenseNumber;
}
