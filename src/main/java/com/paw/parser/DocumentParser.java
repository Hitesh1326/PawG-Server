package com.paw.parser;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.paw.encoding.Constants;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is used for parsing the data csv file format and stop words text file.
 */
public class DocumentParser {
    public static HashMap<Integer, String> contentMap;
    public HashMap<Integer, String> contentTestMap;//not in use
    public static HashMap<Integer, String> docsFileNameReferenceMap;
    public Set<String> stopWords;

    //parsing constructor
    public DocumentParser(String filepath, String stopWordFilePath) {
        File train = null;
        File stopword = null;
        try {
            train = ResourceUtils.getFile(filepath);
            stopword = ResourceUtils.getFile(stopWordFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (stopWordFilePath != null) {
            createStopWordList(stopword);
        }
        parseDocuments(train);
    }

    /**
     * This method is used for adding stop words into tree set.
     * @param stopWordFile file for stopWordFile.
     */
    private void createStopWordList(File stopWordFile) {
        Path path = stopWordFile.toPath();
        /**
         * TreeSet reduces time cost for the basic operations (add, remove and contains).
         */
        stopWords = new TreeSet<>();
        try (Scanner document = new Scanner(path, Constants.ENCODING.name())) {
            while (document.hasNextLine()) {
                stopWords.add(document.nextLine());
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Parses csv file and stores into hash map.
     * @param file input for reading
     */
    public void parseDocuments(File file) {

        contentMap = new HashMap<Integer, String>();
        contentTestMap = new HashMap<Integer, String>();
        docsFileNameReferenceMap = new HashMap<Integer, String>();
        int index = 0;

        //creating mapper object
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        //iterator to read values from csv file
        File csvFile = file;
        MappingIterator<String[]> it = null;
        try {
            it = mapper.readerFor(String[].class).readValues(csvFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (it.hasNext())
            it.next();
        while (it.hasNext() && index < 50) {
            String[] row = it.next();

            contentMap.put(index, row[1].toLowerCase());
            docsFileNameReferenceMap.put(index, row[0]);
            index++;

        }
    }


}
