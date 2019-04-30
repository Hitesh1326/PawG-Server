package com.paw.repository;

import com.paw.model.Paws;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Set;

/**
 * This interface class is used for retrieving documents from mongo server database
 */
public interface PawgRepository extends MongoRepository<Paws,String> {
     Paws findBy_id(ObjectId _id);
     Paws findBydocIDAllIgnoreCase(Integer DocID);


}
