package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {
    /**
     * Get all horses stored in the persistent data store.
     *
     * @return a list of all stored horses
     */
    List<Horse> getAll();


    /**
     * Update the horse with the ID given in {@code horse}
     * with the data given in {@code horse}
     * in the persistent data store.
     *
     * @param horse the horse to update
     * @return the updated horse
     * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
     */
    Horse update(HorseDetailDto horse) throws NotFoundException;

    /**
     * Get a horse by its ID from the persistent data store.
     *
     * @param id the ID of the horse to get
     * @return the horse
     * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
     */
    Horse getById(long id) throws NotFoundException;

    /**
     * Filter the horses for the given input and sex {@code input, sex}
     * in the persistent data store.
     *
     * @param input the input to filter for names of parents
     * @param sex the sex to filter for parents
     * @return the created horse
     * @throws NotFoundException if no Horses are found for the given input and sex
     */
    List<Horse> filter(String input, Sex sex) throws NotFoundException;

    /**
     * Creates a horse with the data given in {@code horse}
     * in the persistent data store.
     *
     * @param horse the horse to create
     * @return the created horse
     * @throws ValidationException if the data given for the horse is in itself incorrect (description too long, no name, …)
     * @throws ConflictException if the update given for the horse is in conflict the data currently in the system (owner does not exist, …)
     */
    Horse create(HorseDetailDto horse) throws ValidationException, ConflictException;
    /**
     * Deletes the horse with the given id {@code id}
     *
     * @param id the input to filter for the to be deleted horse
     * @return the id of deleted horse
     * @throws NotFoundException if no Horse is found for this id
     */
    long delete(long id) throws NotFoundException;

    /**
     * Search for all horses with the parameters given by horse {@code horse}
     * @param horse the horseparameters to search for
     * @param ownerMatchingIDs the ids of matching owners for given input
     * @return a list of all found horses
     */
    List<Horse> search(HorseSearchDto horse, List<OwnerDto> ownerMatchingIDs);
}
