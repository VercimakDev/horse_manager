package at.ac.tuwien.sepm.assignment.individual.entity;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Represents a horse in the persistent data store.
 */
public class Horse {
  private Long id;
  private String name;
  private String description;
  private LocalDate dateOfBirth;
  private Sex sex;
  private Long ownerId;
  private Long fatherId;
  private Long motherId;

  public Long getFatherId() {
    return fatherId;
  }


  public Long getMotherId() {
    return motherId;
  }


  public Long getId() {
    return id;
  }

  public Horse setId(Long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public Horse setName(String name) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public Horse setDescription(String description) {
    this.description = description;
    return this;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public Horse setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public Sex getSex() {
    return sex;
  }

  public Horse setSex(Sex sex) {
    this.sex = sex;
    return this;
  }


  public Long getOwnerId() {
    return ownerId;
  }

  public Horse setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
    return this;
  }

  @Override
  public String toString() {
    return "Horse{"
        + "id=" + id
        + ", name='" + name + '\''
        + ", description='" + description + '\''
        + ", dateOfBirth=" + dateOfBirth
        + ", sex=" + sex
        + ", ownerId=" + ownerId
        + '}';
  }

  public Horse setFather(Long father) {
    this.fatherId = father;
    return this;
  }

  public Horse setMother(Long mother) {
    this.motherId = mother;
    return this;
  }

  public HorseDetailDto toDto(OwnerDto owner, HorseDetailDto father, HorseDetailDto mother) {
    return new HorseDetailDto(
            id,
            name,
            description,
            dateOfBirth,
            sex,
            owner,
            father,
            mother);
  }
}
