package com.example.expensify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class GeneratePayload {

    @Autowired
    Environment environment;

    public String getReportExporterPayload() {
        String urlParameters = "{\n" +
                "        \"type\":\"file\",\n" +
                "        \"credentials\":{\n" +
                "            \"partnerUserID\": \"<replace_id>\",\n" +
                "          \"partnerUserSecret\":\"<replace_secret>\"\n" +
                "        },\n" +
                "        \"onReceive\":{\n" +
                "            \"immediateResponse\":[\"returnRandomFileName\"]\n" +
                "        },\n" +
                "        \"inputSettings\":{\n" +
                "            \"type\":\"combinedReportData\",\n" +
                "           \"filters\":{\n" +
                "                \"startDate\":\"<replace_start_date>\",\n" +
                "                \"endDate\":\"<replace_end_date>\",\n" +
                "                \"markedAsExported\":\"Expensify Export\" }\n" +
                "        },\n" +
                "        \"outputSettings\":{\n" +
                "            \"fileExtension\":\"csv\"\n" +
                "        }\n" +
                "    }";

        String generateExportPayload = urlParameters.replace("<replace_id>",environment.getProperty("expensify.partnerUserID"))
                                        .replace("<replace_secret>",environment.getProperty("expensify.partnerUserSecret"))
                                        .replace("<replace_start_date>",java.time.LocalDate.now().minusDays(1).toString())
                                        .replace("<replace_end_date>",java.time.LocalDate.now().toString());

        return generateExportPayload;
    }

    public String getReportExporterTemplate(){
        String exportTemplate = "<#if addHeader == true>\n" +
                "  Merchant,Amount,Category,ReportNumber,ExpenseNumber<#lt>\n" +
                "</#if>\n" +
                "<#assign reportNumber = 1>\n" +
                "<#assign expenseNumber = 1>\n" +
                "<#list reports as report>\n" +
                "    <#list report.transactionList as expense>\n" +
                "        ${expense.merchant},<#t>\n" +
                "        <#-- note: expense.amount prints the original amount only -->\n" +
                "        ${expense.amount},<#t>\n" +
                "        ${expense.category},<#t>\n" +
                "        ${reportNumber},<#t>\n" +
                "        ${expenseNumber}<#lt>\n" +
                "        <#assign expenseNumber = expenseNumber + 1>\n" +
                "    </#list>\n" +
                "    <#assign reportNumber = reportNumber + 1>\n" +
                "</#list>";
        return  exportTemplate;
    }

    public String getDownloaderPayload(String filename) {
        String urlParameters = "{\n" +
                "        \"type\":\"download\",\n" +
                "        \"credentials\":{\n" +
                "             \"partnerUserID\":\"<replace_id>\",\n" +
                "          \"partnerUserSecret\":\"<replace_secret>\"\n" +
                "        },\n" +
                "        \"fileName\":\"<filename>\",\n" +
                "        \"fileSystem\":\"integrationServer\"\n" +
                "    }";
        String generateDownloadPayload = urlParameters.replace("<replace_id>",environment.getProperty("expensify.partnerUserID"))
                .replace("<replace_secret>",environment.getProperty("expensify.partnerUserSecret"))
                .replace("<filename>",filename);

        return generateDownloadPayload;
    }
}
