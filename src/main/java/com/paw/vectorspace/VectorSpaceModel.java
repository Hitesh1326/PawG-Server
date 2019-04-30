package com.paw.vectorspace;


import com.paw.model.Document;
import com.paw.parser.DocumentParser;
import java.util.*;
import java.util.stream.Collectors;

public class VectorSpaceModel {

    public static HashMap<Integer, String> myDocs;
    public static ArrayList<String> termList;
    public static ArrayList<ArrayList<Document>> docLists;
    public static double[] docLength;
    public static Set<String> docStopWords;


    public VectorSpaceModel(HashMap<Integer, String> contentMap, Set<String> stopWords) {
        myDocs = contentMap;
        termList = new ArrayList<String>();
        docLists = new ArrayList<ArrayList<Document>>();
        ArrayList<Document> docList;
        docStopWords = stopWords;

        for (int i = 0; i < myDocs.size(); i++) {
            String[] tokens = myDocs.get(i).split("[ '.,?!:-;/$%&+()\\d\\=\\*\\\"-]+");
            String token;
            ArrayList<String> cleanWords = new ArrayList<>();

            //removing stopwords
            for (int j = 0; j < tokens.length; j++) {
                token = tokens[j];
                if (!docStopWords.contains(token)) {

                    cleanWords.add(token);
                }
            }
            //stemming words
            ArrayList<String> stem = stemmedWords(cleanWords);
            for (String word : stem) {

                boolean match = false;
                if (!termList.contains(word)) {
                    termList.add(word);
                    docList = new ArrayList<Document>();
                    Document doc = new Document(i, 1);
                    docList.add(doc);
                    docLists.add(docList);
                } else {
                    int index = termList.indexOf(word);
                    docList = docLists.get(index);

                    for (Document did : docList) {
                        if (did.docId == i) {

                            did.tw++;
                            match = true;
                            break;
                        }
                    }
                    if (!match) {
                        Document doc = new Document(i, 1);
                        docList.add(doc);
                    }
                }


            }//end with stem for loop
        }//end with myDocs for loop

        //Calculates term frequency and inverted document frequency.
        int N = myDocs.size();
        docLength = new double[N];

        for (int i = 0; i < termList.size(); i++) {
            docList = docLists.get(i);
            int df = docList.size();
            Document doc;
            for (int j = 0; j < docList.size(); j++) {
                doc = docList.get(j);
                //mulitplies tf*idf
                double tfidf = (1 + Math.log10(doc.tw)) * Math.log10(N * 1.0 / df);
                docLength[doc.docId] += Math.pow(tfidf, 2);
                doc.tw = tfidf;
                docList.set(j, doc);
            }
        }
        for (int i = 0; i < N; i++) {
            docLength[i] = Math.sqrt(docLength[i]);
        }

    }

    /**
     * This method is used for searching documents based on the given query.It
     * ranks the retrieved documents and stores into hash map.
     *
     * @param userQuery takes input query from the user
     * @return ranked Doc IDs
     */
    public HashMap<Integer, Double> rankSearch(String userQuery) {
        HashMap<Integer, Double> unsortedResult = new HashMap<Integer, Double>();
        LinkedHashMap<Integer,Double> sortedResult = new LinkedHashMap<>();

        ArrayList<Document> docList;
        ArrayList<String> cleanQuery = new ArrayList<>();
        double queryLength = 0;

        String[] query = userQuery.split("[ '.,?!:-;/$%&+()\\d\\=\\*\\\"-]+");
        for (String term : query) {
            if (!docStopWords.contains(term)) {
                cleanQuery.add(term);

            }
        }
        ArrayList<String> stem = stemmedWords(cleanQuery);

        for (String term : stem) {
            int index = termList.indexOf(term);
            if (index < 0) {
                continue;
            }
            docList = docLists.get(index);
            //calculates query term frequency*inverted index frequency
            double qtfidf = (1 + Math.log10(1)) * Math.log10(myDocs.size() * 1.0 / docList.size());

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
        double[] cosineSimilarity = new double[myDocs.size()];

        //calculating cosine similarity
        for (Integer key : unsortedResult.keySet()) {
            cosineSimilarity[key] = unsortedResult.get(key) / (docLength[key] * queryLength);
        }
        for (int q = 0; q < cosineSimilarity.length; q++) {
            unsortedResult.put(q, cosineSimilarity[q]);
        }

        //sorting the documents by higher rank
        unsortedResult.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedResult.put(x.getKey(),x.getValue()));


        return sortedResult;
    }


    public String toString() {
        String outString = new String();
        ArrayList<Document> docList;
        for (int i = 0; i < termList.size(); i++) {
            outString += String.format("%-15s", termList.get(i));

            docList = docLists.get(i);
            for (int j = 0; j < docList.size(); j++) {
                outString += docList.get(j) + "\t";
            }
            outString += "\n";
        }
        return outString;
    }


    /**
     * This method stemms the words of given document.
     *
     * @param words input array list of words
     * @return array list of stemm words
     */
    public static ArrayList<String> stemmedWords(ArrayList<String> words) {
        ArrayList<String> stemms = new ArrayList<>();

        Stemmer s = new Stemmer();
        for (String token : words) {
            s.add(token.toCharArray(), token.length());
            s.stem();
            stemms.add(s.toString());
        }
        return stemms;
    }

    public static void main(String [] args){
        DocumentParser parser = new DocumentParser("classpath:train.csv","classpath:stopwords.txt");
        VectorSpaceModel vsm = new VectorSpaceModel(parser.contentMap, parser.stopWords);
        System.out.println(vsm);
    }


}
