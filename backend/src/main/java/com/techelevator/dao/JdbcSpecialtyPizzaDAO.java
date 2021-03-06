package com.techelevator.dao;

import com.techelevator.model.Choice;
import com.techelevator.model.SpecialtyPizza;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcSpecialtyPizzaDAO implements SpecialtyPizzaDAO {

    private JdbcTemplate jdbcTemplate;
    private ChoiceDAO choiceDAO;

    public JdbcSpecialtyPizzaDAO(JdbcTemplate jdbcTemplate, ChoiceDAO choiceDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.choiceDAO = choiceDAO;
    }

    @Override
    public List<SpecialtyPizza> getAll() {
        List<SpecialtyPizza> result = new ArrayList<>();
        String sql = "SELECT specialty_id, name, description, is_available, picture FROM specialty_pizza ORDER BY specialty_id;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            List<Choice> premiumToppings = new ArrayList<>();
            List<Choice> regularToppings = new ArrayList<>();
            SpecialtyPizza specialtyPizza = new SpecialtyPizza();
            specialtyPizza.setSpecialtyId(rowSet.getInt("specialty_id"));
            specialtyPizza.setAvailable(rowSet.getBoolean("is_available"));
            specialtyPizza.setName(rowSet.getString("name"));
            specialtyPizza.setDescription(rowSet.getString("description"));
            specialtyPizza.setPicture(rowSet.getString("picture"));

            String sql1 = "SELECT c.choice_id, c.category_id, c.name, c.is_available, s.specialty_id, s.choice_id  " +
                    "FROM choices AS c " +
                    "JOIN choices_specialty_pizza s ON c.choice_id = s.choice_id " +
                    "WHERE s.specialty_id = ? " +
                    "ORDER BY c.choice_id;";
            SqlRowSet choiceRowSet = jdbcTemplate.queryForRowSet(sql1, specialtyPizza.getSpecialtyId());
            while (choiceRowSet.next()) {
                if (choiceRowSet.getInt("category_id") == 2) {
                    specialtyPizza.setCrust(mapRowToChoice(choiceRowSet));
                }
                if (choiceRowSet.getInt("category_id") == 3) {
                    specialtyPizza.setSauce(mapRowToChoice(choiceRowSet));
                }
                if (choiceRowSet.getInt("category_id") == 4) {
                    regularToppings.add(mapRowToChoice(choiceRowSet));
                }
                if (choiceRowSet.getInt("category_id") == 5) {
                    premiumToppings.add(mapRowToChoice(choiceRowSet));
                }
            }
            specialtyPizza.setRegularToppings(regularToppings);
            specialtyPizza.setPremiumToppings(premiumToppings);
            result.add(specialtyPizza);
        }
        return result;
    }

    @Override
    public SpecialtyPizza getSpecialtyPizzaById(int specialtyId) {
        SpecialtyPizza specialtyPizza = new SpecialtyPizza();
        String sql = "SELECT specialty_id, name, description, is_available FROM specialty_pizza WHERE specialty_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, specialtyId);
        if (rowSet.next()) {
            List<Choice> premiumToppings = new ArrayList<>();
            List<Choice> regularToppings = new ArrayList<>();

            specialtyPizza.setSpecialtyId(rowSet.getInt("specialty_id"));
            specialtyPizza.setAvailable(rowSet.getBoolean("is_available"));
            specialtyPizza.setName(rowSet.getString("name"));
            specialtyPizza.setDescription(rowSet.getString("description"));

            String sql1 = "SELECT c.choice_id, c.category_id, c.name, c.is_available, s.specialty_id, s.choice_id  " +
                    "FROM choices AS c " +
                    "JOIN choices_specialty_pizza s ON c.choice_id = s.choice_id " +
                    "WHERE s.specialty_id = ? " +
                    "ORDER BY c.choice_id;";
            SqlRowSet choiceRowSet = jdbcTemplate.queryForRowSet(sql1, specialtyPizza.getSpecialtyId());
            while (choiceRowSet.next()) {
                if (choiceRowSet.getInt("category_id") == 2) {
                    specialtyPizza.setCrust(mapRowToChoice(choiceRowSet));
                }
                if (choiceRowSet.getInt("category_id") == 3) {
                    specialtyPizza.setSauce(mapRowToChoice(choiceRowSet));
                }
                if (choiceRowSet.getInt("category_id") == 4) {
                    regularToppings.add(mapRowToChoice(choiceRowSet));
                }
                if (choiceRowSet.getInt("category_id") == 5) {
                    premiumToppings.add(mapRowToChoice(choiceRowSet));
                }
            }
            specialtyPizza.setRegularToppings(regularToppings);
            specialtyPizza.setPremiumToppings(premiumToppings);
        }
        return specialtyPizza;
    }

    @Override
    public SpecialtyPizza getCustomPizzaById(int pizzaId) {
        SpecialtyPizza customPizza = new SpecialtyPizza();
        customPizza.setName("Custom");
        customPizza.setSpecialtyId(pizzaId);

        String sql = "SELECT choice_id FROM choices_custom_pizza WHERE pizza_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, pizzaId);
        List<Choice> choices = new ArrayList<>();
        while (rowSet.next()) {
            choices.add(choiceDAO.getChoiceById(rowSet.getInt("choice_id")));
        }

        List<Choice> premiumToppings = new ArrayList<>();
        List<Choice> regularToppings = new ArrayList<>();
        for (Choice choice : choices) {
            if (choice.getCategoryId() == 2) {
                customPizza.setCrust(choice);
            } else if (choice.getCategoryId() == 3) {
                customPizza.setSauce(choice);
            } else if (choice.getCategoryId() == 4) {
                regularToppings.add(choice);
            } else {
                premiumToppings.add(choice);
            }
        }
        customPizza.setRegularToppings(regularToppings);
        customPizza.setPremiumToppings(premiumToppings);

        return customPizza;
    }

    @Override
    public void addSpecial(SpecialtyPizza specialtyPizza) {
        String sql = "INSERT INTO specialty_pizza (name, description, is_available, picture) " +
                "VALUES (?, ?, ?, ?) RETURNING specialty_id;";

        String str = specialtyPizza.getPicture().replace("\\", "/");
        int specialtyId = jdbcTemplate.queryForObject(sql, Integer.class, specialtyPizza.getName(),
                specialtyPizza.getDescription(), specialtyPizza.isAvailable(), str);
        addChoiceSpecial(specialtyPizza.getCrust(), specialtyId);
        addChoiceSpecial(specialtyPizza.getSauce(), specialtyId);
        addToppingsToChoiceSpecial(specialtyPizza.getRegularToppings(), specialtyId);
        addToppingsToChoiceSpecial(specialtyPizza.getPremiumToppings(), specialtyId);
    }

    @Override
    public void deleteSpecial(int specialtyId) {
        String sql = "DELETE FROM choices_specialty_pizza WHERE specialty_id = ?; " +
                "DELETE FROM specialty_pizza WHERE specialty_id = ?;";
        jdbcTemplate.update(sql, specialtyId, specialtyId);
    }

    @Override
    public void updateSpecial(SpecialtyPizza specialtyPizza) {
        String sql = "UPDATE specialty_pizza SET name = ?, description = ?, is_available = ?" +
                " WHERE specialty_id = ? ;";
        jdbcTemplate.update(sql, specialtyPizza.getName(), specialtyPizza.getDescription(),
                specialtyPizza.isAvailable(), specialtyPizza.getSpecialtyId());
    }

    private void addChoiceSpecial(Choice choice, int specialtyId) {
        String sql = "INSERT INTO choices_specialty_pizza (specialty_id, choice_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, specialtyId, choice.getChoiceId());
    }

    private void addToppingsToChoiceSpecial(List<Choice> toppings, int specialtyId) {
        for (Choice choice : toppings) {
            addChoiceSpecial(choice, specialtyId);
        }
    }

    private Choice mapRowToChoice(SqlRowSet rowSet) {
        Choice choice = new Choice();
        choice.setName(rowSet.getString("name"));
        choice.setChoiceId(rowSet.getInt("choice_id"));
        choice.setCategoryId(rowSet.getInt("category_id"));
        choice.setAvailable(rowSet.getBoolean("is_available"));
        return choice;
    }

}
