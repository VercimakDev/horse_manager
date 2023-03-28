package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.*;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HorseServiceImpl implements HorseService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final HorseDao dao;
    private final HorseMapper mapper;
    private final HorseValidator validator;
    private final OwnerService ownerService;


    public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, OwnerService ownerService) {
        this.dao = dao;
        this.mapper = mapper;
        this.validator = validator;
        this.ownerService = ownerService;
    }

    @Override
    public Stream<HorseListDto> allHorses() {
        LOG.trace("allHorses()");
        var horses = dao.getAll();
        var ownerIds = horses.stream()
                .map(Horse::getOwnerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        Map<Long, OwnerDto> ownerMap;
        try {
            ownerMap = ownerService.getAllById(ownerIds);
        } catch (NotFoundException e) {
            throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
        }
        return horses.stream()
                .map(horse -> mapper.entityToListDto(horse, ownerMap, horses));
    }


    @Override
    public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
        LOG.trace("update({})", horse);
        validator.validateForUpdate(horse);
        var horses = dao.getAll();
        var updatedHorse = dao.update(horse);
        return mapper.entityToDetailDto(
                updatedHorse,
                ownerMapForSingleId(updatedHorse.getOwnerId()), horses);
    }


    @Override
    public HorseDetailDto getById(long id) throws NotFoundException {
        LOG.trace("details({})", id);
        Horse horse = dao.getById(id);
        var horses = dao.getAll();
        return mapper.entityToDetailDto(
                horse,
                ownerMapForSingleId(horse.getOwnerId()),horses);
    }

    @Override
    public HorseDetailDto create(HorseDetailDto horse) throws ValidationException, ConflictException {
        LOG.trace("create({})", horse);
        validator.validateForCreation(horse);
        var horses = dao.getAll();
        var createdHorse = dao.create(horse);
        return mapper.entityToDetailDto(
                createdHorse,
                ownerMapForSingleId(createdHorse.getOwnerId()), horses);
    }

    @Override
    public Stream<HorseListDto> filter(String input, Sex sex) throws NotFoundException {
        var horses = dao.filter(input, sex);
        var ownerIds = horses.stream()
                .map(Horse::getOwnerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        Map<Long, OwnerDto> ownerMap;
        try {
            ownerMap = ownerService.getAllById(ownerIds);
        } catch (NotFoundException e) {
            throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
        }
        return horses.stream()
                .map(horse -> mapper.entityToListDto(horse, ownerMap, horses));
    }
    @Override
    public long delete(long id) throws NotFoundException, ValidationException{
        validator.validateId(id);
        return dao.delete(id);
    }

    @Override
    public Stream<HorseListDto> search(HorseSearchDto horse) {
        OwnerSearchDto ownerSearch = null;
        List<OwnerDto> ownerMatchingIDs = null;
        if(horse.ownerName() != null){
            ownerSearch = new OwnerSearchDto(horse.ownerName(),null);
            ownerMatchingIDs = ownerService.search(ownerSearch).toList();
        }
        var horses = dao.search(horse, ownerMatchingIDs);
        var allHorses = dao.getAll();
        var ownerIds = ownerService.getAll()
                .filter(Objects::nonNull)
                .map(OwnerDto::id)
                .collect(Collectors.toUnmodifiableSet());
        Map<Long, OwnerDto> ownerMap;
        try {
            LOG.info("This are the owner_ids:" + ownerIds.toString());
            ownerMap = ownerService.getAllById(ownerIds);
        } catch (NotFoundException e) {
            throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
        }
        return horses.stream()
                .map(h -> mapper.entityToListDto(h, ownerMap, allHorses));
    }
    @Override
    public boolean allFieldsNull(HorseSearchDto horse) throws FatalException {
        Class<?> recordClass = horse.getClass();
        Field[] fields = recordClass.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(horse);

                if (value != null) {
                    if (value instanceof String && !((String) value).isEmpty()) {
                        return false; // Field is not null or empty, return false
                    }
                    if(!(value instanceof String)){
                        return false;
                    }
                }
            } catch (IllegalAccessException e) {
                throw new FatalException("Could not check the fields for NULL or EMPTY");
            }
        }

        return true; // All fields are null or empty, return true
    }


    private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
        try {
            return ownerId == null
                    ? null
                    : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
        } catch (NotFoundException e) {
            throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
        }
    }

}
