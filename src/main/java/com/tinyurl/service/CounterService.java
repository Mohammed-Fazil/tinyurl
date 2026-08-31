package com.tinyurl.service;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.tinyurl.entity.Counter;

@Service
public class CounterService {

	private final MongoOperations mongoOperations;

	public CounterService(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public long getNextSequence() {

		Query query = new Query(Criteria.where("_id").is("url"));

		Update update = new Update().inc("sequence", 1);

		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);

		Counter counter = mongoOperations.findAndModify(query, update, options, Counter.class);

		return counter.getSequence();
	}
}