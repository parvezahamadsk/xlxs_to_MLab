package com.example.bucket.controller;

import com.example.bucket.service.AmazonClient;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
@RestController
@RequestMapping("/storage/")
public class BucketController {

    private AmazonClient amazonClient;
    private Scanner scanner;
    MongoCollection<Document> imageUrls;

    @Autowired
    BucketController(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
        List<Document> seedData = new ArrayList<Document>();
        String url = this.amazonClient.uploadFile(file);

        CRUD(url);
        return url;
    }
    @PostMapping("/crudOperations")
    public void CRUD(String url)
    {
        scanner = new Scanner( System.in );
        System.out.println("Enter your Choice of CRUD");
        int operation = scanner.nextInt();
        MongoClientURI uri  = new MongoClientURI("mongodb://pdtest:pd1234@ds141082.mlab.com:41082/pd_test");
        MongoClient client = new MongoClient(uri);
        MongoDatabase db = client.getDatabase(uri.getDatabase());
        imageUrls = db.getCollection("test_data");

        switch(operation)
        {
            case 1: //insert +C
            {
                List<Document> seedData = new ArrayList<Document>();
                seedData.add(new Document( "upload",url ) );
                System.out.println(url);

                imageUrls.insertMany( seedData );
                break;
            }

            case 2:  //modify  +U
            {
                Document updateQuery = new Document("upload","https://s3.ap-south-1.amazonaws.com/cadenza-images/testing-product/1534241768938-WhatsApp_Image_2018-02-08_at_11.28.53_AM.jpeg");
                imageUrls.updateOne(updateQuery, new Document("$set", new Document("upload", "www.google.co.in")));
                break;
            }

            case 3:  //retrieval +R
            {

                MongoCollection<Document> collection = db.getCollection("test_data");

                List<Document> documents = (List<Document>) collection.find().into(
                        new ArrayList<Document>());

                for(Document document : documents){
                    System.out.println(document);
                }
                break;
            }

            case 4:    //drop +D
            {
                MongoCollection<Document> collection = db.getCollection("test_data");
                collection.deleteOne(new Document("_id", new ObjectId("5b72b0d941a9dc1cf4984fa8")));
                break;
            }
        }

    }

    @DeleteMapping("/deleteFile")
    public String deleteFile(@RequestPart(value = "url") String fileUrl) {
        return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
    }


}
