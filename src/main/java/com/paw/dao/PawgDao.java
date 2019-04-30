package com.paw.dao;

import com.paw.model.Document;
import org.springframework.stereotype.Component;
import com.paw.vectorspace.VectorSpaceModel;
import java.io.FileNotFoundException;
import java.util.*;

@Component
public class PawgDao {

    /**
     * This method is used for searching documents based on the given query.It
     * ranks the retrieved documents.
     *
     * @param userQuery takes input query from the user
     * @return ranked Doc IDs
     */
    public Integer[] rankSearch(String userQuery) throws FileNotFoundException {
        HashMap<Integer, Double> unsortedResult = new HashMap<Integer, Double>();
        LinkedHashMap<Integer, Double> sortedResult = new LinkedHashMap<>();
        LinkedHashMap<Integer, String> content = new LinkedHashMap<>();
        ArrayList<Document> docList;
        ArrayList<String> cleanQuery = new ArrayList<>();
        double queryLength = 0;

        String[] query = userQuery.split("[ '.,?!:-;/$%&+()\\d\\=\\*\\\"-]+");
        for (String term : query) {
            if (!VectorSpaceModel.docStopWords.contains(term)) {
                cleanQuery.add(term);
            }
        }
        ArrayList<String> stem = VectorSpaceModel.stemmedWords(cleanQuery);

        for (String term : stem) {
            int index = VectorSpaceModel.termList.indexOf(term);
            if (index < 0) {
                continue;
            }
            docList = VectorSpaceModel.docLists.get(index);
            //calculates query term frequency*inverted index frequency
            double qtfidf = (1 + Math.log10(1)) * Math.log10(VectorSpaceModel.myDocs.size() * 1.0 / docList.size());

            queryLength = Math.pow(qtfidf, 2);//

            Document doc;
            for (int i = 0; i < docList.size(); i++) {
                doc = docList.get(i);
                //calculates score
                double score = doc.tw * qtfidf;

                if (!unsortedResult.containsKey(doc.docId)) {
                    unsortedResult.put(doc.docId, score);
                } else {
                    score += unsortedResult.get(doc.docId);
                    unsortedResult.put(doc.docId, score);
                }
            }

        }

        //normalizing the lengths using cosine similartiy concept
        queryLength = Math.sqrt(queryLength);
        double[] cosineSimilarity = new double[VectorSpaceModel.myDocs.size()];

        //calculating cosine similarity
        for (Integer key : unsortedResult.keySet()) {
            cosineSimilarity[key] = unsortedResult.get(key) / (VectorSpaceModel.docLength[key] * queryLength);
        }

        for (int q = 0; q < cosineSimilarity.length; q++) {
            unsortedResult.put(q, cosineSimilarity[q]);

        }

        //sorting the documents by higher rank
        unsortedResult.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedResult.put(x.getKey() + 1, x.getValue()));

        //storing docIDs into array
        Integer did[] = new Integer[sortedResult.keySet().size()];
        int i = 0;
        for (Integer ss : sortedResult.keySet()) {
            did[i] = ss;
            i++;

        }

        return did;

    }


}
