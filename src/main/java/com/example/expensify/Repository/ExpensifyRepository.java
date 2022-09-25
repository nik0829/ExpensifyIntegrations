package com.example.expensify.Repository;

import com.example.expensify.model.ExportTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExpensifyRepository extends MongoRepository<ExportTemplate,String> {
}
