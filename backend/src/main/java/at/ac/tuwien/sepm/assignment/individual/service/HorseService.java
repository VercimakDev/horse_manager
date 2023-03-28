package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.time.LocalDate;
import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Lists all horses stored in the system.
   *
   * @return list of all stored horses
   */
  Stream<HorseListDto> allHorses();


  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if the update data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException;


  /**
   * Get the horse with given ID, with more detail information.
   * This includes the owner of the horse, and its parents.
   * The parents of the parents are not included.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException;

  /**
   * Creates a horse with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to create
   * @return the created horse
   * @throws ValidationException if the data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException if the update given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseDetailDto create(HorseDetailDto horse) throws ValidationException, ConflictException;

  /**
   * Filters the horses to find parents with given data {@code input, sex}
   *
   * @param input the input to filter for names of parents
   * @param sex the sex to filter for parents
   * @return the filtered horses
   * @throws NotFoundException if no Horses are found for the given input and sex
   */
  Stream<HorseListDto> filter(String input, Sex sex) throws NotFoundException;
  /**
   * Deletes the horse with the given id {@code id}
   *
   * @param id the input to filter for the to be deleted horse
   * @return the id of deleted horse
   * @throws NotFoundException if no Horse is found for this id
   */
  long delete(long id) throws NotFoundException, ValidationException;

  /**
   * Filters the horses to find horses with given data {@code horse}
   *
   * @param horse the searched for horseinput
   * @return the filtered horses
   * @throws NotFoundException if no Horses are found for the given horse
   */
    Stream<HorseListDto> search(HorseSearchDto horse);

  /**
   * Looks at all the fields of the given horseSearchDto and checks them for being null {@code horse}
   *
   * @param horse the horse that schould be checked
   * @return true if all fields are null, false otherwise
   * @throws FatalException if the fields can not be checked
   */
  boolean allFieldsNull(HorseSearchDto horse) throws FatalException;
}
