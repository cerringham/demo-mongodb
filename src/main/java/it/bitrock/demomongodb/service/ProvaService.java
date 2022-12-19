package it.bitrock.demomongodb.service;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import it.bitrock.demomongodb.model.Movie;
import it.bitrock.demomongodb.model.Prova;
import it.bitrock.demomongodb.repository.MovieRepository;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Service
public class ProvaService {

    @Autowired
    MovieRepository movieRepository;

    private MongoClient init(){
        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "mongodb+srv://root:Yun4W8lv8TdKVG5D@cluster0.qnmving.mongodb.net/?retryWrites=true&w=majority";
        return MongoClients.create(uri);
    }

    private MongoClient initImplement(){
        ConnectionString connectionString = new ConnectionString("mongodb+srv://root:Yun4W8lv8TdKVG5D@cluster0.qnmving.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder ->
                        builder.maxConnectionIdleTime(60000, TimeUnit.MILLISECONDS))
                .applyToSslSettings(builder -> builder.enabled(true))
                .build();
        return MongoClients.create(settings);
    }

    private MongoCollection<Prova> getMongoCollection(){
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        MongoDatabase database = init().getDatabase("fede").withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Prova> collection = database.getCollection("prova", Prova.class);
        return collection;
    }

    @Deprecated
    public ResponseEntity<?> findAll(){
        FindIterable<Prova> iterable = getMongoCollection().find(); // (1)
        MongoCursor<Prova> cursor = iterable.iterator(); // (2)
        List<Prova> prove = new ArrayList<>();
        try {
            while(cursor.hasNext()) {
                prove.add(cursor.next());
            }
            return ResponseEntity.ok(prove);
        } finally {
            cursor.close();
        }
    }

}