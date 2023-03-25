package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
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
import java.util.Collections;
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
        validator.validateForCreation(horse);
        var horses = dao.getAll();
        var createdHorse = dao.create(horse);
        LOG.info("Create Service Father:" + horse.fatherId());
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
