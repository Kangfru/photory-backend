package com.ot.repository.photo_tikcet.custom;

import com.ot.model.ticket.PhotoTicketResponse;
import com.ot.repository.photo_tikcet.entity.PhotoTicket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

@RequiredArgsConstructor
@Slf4j
public class PhotoTicketRepositoryCustomImpl implements PhotoTicketRepositoryCustom {

    private final MongoTemplate mongoTemplate;
    @Override
    public List<PhotoTicketResponse> findPopularPhotoTicketsByCountContentsId() {
        AggregationOperation lookupOperation = Aggregation.lookup("file", "fileId", "_id", "image");
        AggregationOperation unwindOperation = Aggregation.unwind("image", true);
        AggregationOperation memberLookupOperation = Aggregation.lookup("members", "memberSeq", "_id", "member");
        AggregationOperation memberUnwindOperation = Aggregation.unwind("member", true);
        AggregationOperation sortOperation = Aggregation.sort(Sort.Direction.ASC, "contentsId");
        AggregationOperation groupOperation = Aggregation.group("contentsId").push("$$ROOT").as("docs");
        AggregationOperation projectOperation = Aggregation.project().and(ArrayOperators.Slice.sliceArrayOf("docs").offset(0).itemCount(12)).as("docs");

        Aggregation aggregation = Aggregation.newAggregation(
                lookupOperation,
                unwindOperation, // 배열 풀기
                memberLookupOperation,
                memberUnwindOperation,
                sortOperation,
                groupOperation,
                projectOperation,
                unwind("docs")
        );
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "photoTickets", Document.class);

        // Convert each 'docs' field in the documents into a PhotoTicket object
        return results.getMappedResults().stream()
                .map(document -> {
                    Document doc = (Document) document.get("docs");
                    return mongoTemplate.getConverter().read(PhotoTicketResponse.class, doc);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PhotoTicket> findByIdWithImage(String photoTicketId) {

        AggregationOperation matchOperation = Aggregation.match(Criteria.where("_id").is(new ObjectId(photoTicketId)));
        AggregationOperation lookupOperation = Aggregation.lookup("file", "fileId", "_id", "image");
        AggregationOperation unwindOperation = Aggregation.unwind("image", true);

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                lookupOperation,
                unwindOperation
        );

        AggregationResults<PhotoTicket> results = mongoTemplate.aggregate(aggregation, "photoTickets", PhotoTicket.class);

        PhotoTicket photoTicket = results.getUniqueMappedResult();
        return Optional.ofNullable(photoTicket);
    }

    @Override
    public List<PhotoTicket> findByMemberSeq(String memberSeq) {

        AggregationOperation matchOperation = Aggregation.match(Criteria.where("memberSeq").is(new ObjectId(memberSeq)));
        AggregationOperation lookupOperation = Aggregation.lookup("file", "fileId", "_id", "image");
        AggregationOperation unwindOperation = Aggregation.unwind("image", true);

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                lookupOperation,
                unwindOperation
        );

        AggregationResults<PhotoTicket> results = mongoTemplate.aggregate(aggregation, "photoTickets", PhotoTicket.class);
        return results.getMappedResults();
    }
}
