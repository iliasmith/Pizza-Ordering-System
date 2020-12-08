package com.techelevator.controller;

import com.techelevator.dao.ChoiceDAO;
import com.techelevator.model.Choice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ChoicesController {

    private ChoiceDAO choiceDAO;

    public ChoicesController(ChoiceDAO choiceDAO) {
        this.choiceDAO = choiceDAO;
    }

    @GetMapping("/choices")
    public List<Choice> getAll() {
        return choiceDAO.getAllChoices();
    }

//    @GetMapping("/choices/{id}")
//    public List<Choice> getByCategoryId(@PathVariable int id) {
//        return choiceDAO.getChoiceByCategoryId(id);
//    }

    @PostMapping("/choices")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void addChoice(@RequestBody Choice choice) {
        choiceDAO.addChoice(choice);
    }

}