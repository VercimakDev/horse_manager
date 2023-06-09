package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";

  private final HorseService service;

  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    try {
      if (service.allFieldsNull(searchParameters)) {
        LOG.info("Executing GET ALL HORSES " + BASE_PATH);
        return service.allHorses();
      } else {
        LOG.info("Executing GET filter Horses for input: " + searchParameters);

        return service.search(searchParameters);
      }
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse could not be created", e);
      LOG.error("An NotFoundException was thrown: " + e.getMessage());
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse could because of a Conflict:", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @PostMapping
  public HorseDetailDto createHorse(@RequestBody HorseDetailDto horse) {
    LOG.info("POST trying to create Horse with the Name: " + horse.name());
    try {
      LOG.info("createHorse Father:" + horse.fatherId());
      return service.create(horse);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      LOG.error("Horse could not be created because of a Validation exception:" + status, e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Horse could because of a Conflict:", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @GetMapping("/{input}/{sex}")
  public Stream<HorseListDto> getParentCandidates(@PathVariable String input, @PathVariable Sex sex) {
    LOG.info("GET trying to filter Horses for input: " + input);
    try {
      return service.filter(input, sex);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Parentcandidates could not be found:", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @PutMapping("{id}")
  public HorseDetailDto update(@PathVariable long id, @RequestBody HorseDetailDto toUpdate) throws ValidationException, ConflictException {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      return service.update(toUpdate.withId(id));
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @DeleteMapping("{id}")
  public long delete(@PathVariable long id) {
    LOG.info("DELETE /{}", id);
    try {
      return service.delete(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Horse to update could not be Validatet", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @GetMapping("{id}/familytree/{generations}")
  public HorseDetailDto getTree(@PathVariable long id, @PathVariable int generations) {
    LOG.info("GET GENERATIONS " + BASE_PATH + "/{}" + "/{}", id, generations);
    try {
      return service.getById(id, generations);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }


  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
