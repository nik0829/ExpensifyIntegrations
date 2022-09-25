package com.example.expensify.service;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.example.expensify.Utils.CsvUtil;
import com.example.expensify.model.ExportTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ExpensifyIntegrationsService {

    @Autowired
    GeneratePayload generatePayload;

    @Autowired
    Environment environment;

    public String generateReport(){

        String filename = "";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            List<NameValuePair> form = new ArrayList<>();
            form.add(new BasicNameValuePair("requestJobDescription", generatePayload.getReportExporterPayload()));
            form.add(new BasicNameValuePair("template", generatePayload.getReportExporterTemplate()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);

            HttpPost httpPost = new HttpPost(environment.getProperty("expensify.url"));
            httpPost.setEntity(entity);

            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity responseEntity = response.getEntity();
                    return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(httpPost, responseHandler);
            filename = responseBody;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public List<ExportTemplate> getCSV() throws Exception {

        List <Map< String, String >> list = new ArrayList < > ();
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String filename = generateReport();
            List<NameValuePair> form = new ArrayList<>();
            form.add(new BasicNameValuePair("requestJobDescription", generatePayload.getDownloaderPayload(filename)));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);

            HttpPost httpPost = new HttpPost(environment.getProperty("expensify.url"));
            httpPost.setEntity(entity);

            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity responseEntity = response.getEntity();
                    return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(httpPost, responseHandler);

            try (Reader in = new StringReader(responseBody);) {
                CsvUtil csv = new CsvUtil(true, ',', in );
                List< String > fieldNames = null;
                if (csv.hasNext()) fieldNames = new ArrayList< >(csv.next());
                while (csv.hasNext()) {
                    List < String > x = csv.next();
                    Map < String, String > obj = new LinkedHashMap< >();
                    for (int i = 0; i < fieldNames.size(); i++) {
                        obj.put(fieldNames.get(i), x.get(i));
                    }
                    list.add(obj);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        List<ExportTemplate> exportTemplateList = new ArrayList<>();
        for(Map<String, String> data : list){
            exportTemplateList.add(mapper.convertValue(data,ExportTemplate.class));
        }
        return exportTemplateList.isEmpty() ? Collections.emptyList() : exportTemplateList;
    }

}
