package com.paw.controller;

import com.paw.dao.PawgDao;
import com.paw.model.Paws;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import com.paw.repository.PawgRepository;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * HTTP Request methods are created in this class.
 */
@RestController
@RequestMapping("/pawg")
public class SearchController {

    private final PawgDao pawgDao;


    @Autowired
    public SearchController(PawgDao pawgDao) {
        this.pawgDao = pawgDao;
    }

    @Autowired
    private PawgRepository repository;

    /**
     * HTTP GET Method
     * @return all the documents from database.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Page<Paws> getAllPaws() {
        return repository.findAll(new PageRequest(0, 100));
    }

    /**
     * HTTP GET Method to retrieve docs by id.
     * @param id input document id
     * @return returns document from the database.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Paws getPawById(@PathVariable("id") ObjectId id) {
        return repository.findBy_id(id);
    }

    /**
     * HTTP POST Method to retrieve docs by user's text query
     * @param queryText input users text query
     * @return it returns relevant and top 10 ranked documents with images.
     * @throws FileNotFoundException
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ArrayList<Paws> rankQuery(@Valid @RequestBody String queryText) throws FileNotFoundException {
        ArrayList<Paws> paws = new ArrayList<>();
        Integer[] docss = pawgDao.rankSearch(queryText);
        int count =0;
        for (Integer ss : docss) {
            paws.add(repository.findBydocIDAllIgnoreCase(ss));
            count++;
            if(count==10)
                break;
        }

        return paws;
    }


}
