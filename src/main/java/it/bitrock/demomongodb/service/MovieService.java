package it.bitrock.demomongodb.service;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import it.bitrock.demomongodb.model.Movie;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Service
public class MovieService {

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

    public ResponseEntity<?> findAllMovie(){
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        MongoDatabase database = init().getDatabase("sample_mflix").withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Movie> collection = database.getCollection("movies", Movie.class);
        FindIterable<Movie> iterable = collection.find(); // (1)
        MongoCursor<Movie> cursor = iterable.iterator(); // (2)
        try {
            while(cursor.hasNext()) {
                return ResponseEntity.ok(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public ResponseEntity<?> findByTitle(String title){
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        MongoDatabase database = init().getDatabase("sample_mflix").withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Movie> collection = database.getCollection("movies", Movie.class);
        Movie movie = collection.find(eq("title", title)).first();
        return ResponseEntity.ok(movie);
    }

}
