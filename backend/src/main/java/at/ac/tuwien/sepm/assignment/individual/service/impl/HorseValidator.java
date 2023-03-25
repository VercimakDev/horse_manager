package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;

import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public void validateForUpdate(HorseDetailDto horse) throws ValidationException, ConflictException {
        LOG.trace("validateForUpdate({})", horse);
        List<String> validationErrors = new ArrayList<>();

        if (horse.id() == null) {
            validationErrors.add("No ID given");
        }

        if (horse.description() != null) {
            if (horse.description().isBlank()) {
                validationErrors.add("Horse description is given but blank");
            }
            if (horse.description().length() > 4095) {
                validationErrors.add("Horse description too long: longer than 4095 characters");
            }
        }

        // TODO this is not complete…

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of horse for update failed", validationErrors);
        }
    }

    public void validateForCreation(HorseDetailDto horse) throws ValidationException, ConflictException {
        LOG.trace("validateForCreation({})", horse);
        List<String> validationErrors = new ArrayList<>();
        if (horse.name() == null) {
            validationErrors.add("No name given");
        } else {
            if (horse.name().isBlank()) {
                validationErrors.add("Horse name is given but blank");
            }
        }
        if (horse.description() != null) {
            if (horse.description().isBlank()) {
                validationErrors.add("Horse description is given but blank");
            }
            if (horse.description().length() > 4095) {
                validationErrors.add("Horse description too long: longer than 4095 characters");
            }
        }
        if (horse.dateOfBirth() == null) {
            validationErrors.add("No birthdate given");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sdf.parse(horse.dateOfBirth().toString());
            } catch (Exception e) {
                validationErrors.add(e.getMessage());
            }
        }
        if (horse.sex() == null) {
            validationErrors.add("No sex given");
        } else {
            if (horse.sex() != Sex.FEMALE && horse.sex() != Sex.MALE) {
                validationErrors.add("The given sex is not valid");
            }
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of horse for creation failed", validationErrors);
        }
    }

    public void validateId(Long id) throws ValidationException{
        LOG.trace("validateId({})", id);
        List<String> validationErrors = new ArrayList<>();
        if (id == null) {
            validationErrors.add("No ID given");
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of horse for creation failed", validationErrors);
        }
    }

}
