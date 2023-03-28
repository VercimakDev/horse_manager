package at.ac.tuwien.sepm.assignment.individual.mapper;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public HorseMapper() {
  }

  /**
   * Convert a horse entity object to a {@link HorseListDto}.
   * The given map of owners needs to contain the owner of {@code horse}.
   *
   * @param horse  the horse to convert
   * @param owners a map of horse owners by their id, which needs to contain the owner referenced by {@code horse}
   * @return the converted {@link HorseListDto}
   */
  public HorseListDto entityToListDto(Horse horse, Map<Long, OwnerDto> owners, List<Horse> horses) {
    LOG.trace("entityToDto({})", horse);
    if (horse == null) {
      return null;
    }

    return new HorseListDto(
        horse.getId(),
        horse.getName(),
        horse.getDescription(),
        horse.getDateOfBirth(),
        horse.getSex(),
        getOwner(horse, owners),
        getFather(horse, horses, owners),
        getMother(horse, horses, owners)
    );
  }

  /**
   * Convert a horse entity object to a {@link HorseListDto}.
   * The given map of owners needs to contain the owner of {@code horse}.
   *
   * @param horse  the horse to convert
   * @param owners a map of horse owners by their id, which needs to contain the owner referenced by {@code horse}
   * @return the converted {@link HorseListDto}
   */
  public HorseDetailDto entityToDetailDto(
      Horse horse,
      Map<Long, OwnerDto> owners, List<Horse> horses) {
    LOG.trace("entityToDto({})", horse);
    if (horse == null) {
      return null;
    }


    return new HorseDetailDto(
        horse.getId(),
        horse.getName(),
        horse.getDescription(),
        horse.getDateOfBirth(),
        horse.getSex(),
        getOwner(horse, owners),
        getFather(horse, horses, owners),
        getMother(horse, horses, owners)

    );
  }

  public HorseDetailDto entityToDetailDtoLimited(
      Horse horse,
      Map<Long, OwnerDto> owners, List<Horse> horses,
      int generations) {
    LOG.trace("entityToDtoLimited({} {})", horse, generations);
    if (horse == null || generations == 0) {
      return null;
    }
    if (generations == 1) {
      return new HorseDetailDto(
          horse.getId(),
          horse.getName(),
          horse.getDescription(),
          horse.getDateOfBirth(),
          horse.getSex(),
          getOwner(horse, owners),
          null,
          null

      );
    }

    return new HorseDetailDto(
        horse.getId(),
        horse.getName(),
        horse.getDescription(),
        horse.getDateOfBirth(),
        horse.getSex(),
        getOwner(horse, owners),
        getFather(horse, horses, owners),
        getMother(horse, horses, owners)

    );
  }

  private OwnerDto getOwner(Horse horse, Map<Long, OwnerDto> owners) {
    OwnerDto owner = null;
    var ownerId = horse.getOwnerId();
    if (ownerId != null) {
      if (owners != null) {
        if (!owners.containsKey(ownerId)) {
          throw new FatalException("Given owner map does not contain owner of this Horse (%d)".formatted(horse.getId()));
        }
        owner = owners.get(ownerId);
      }
    }
    return owner;
  }

  private HorseDetailDto getFather(Horse horse, List<Horse> horses, Map<Long, OwnerDto> owners) {
    HorseDetailDto parent = null;
    var parentId = horse.getFatherId();
    if (parentId != null) {
      boolean found = false;
      Horse father = null;
      for (Horse h : horses) {
        if (h.getId().equals(parentId)) {
          found = true;
          father = h;
        }
      }
      if (!found) {
        throw new FatalException("Given horses list does not contain father of this Horse (%d)".formatted(horse.getId()));
      }
      parent = entityToDetailDto(father, owners, horses);
      // parent = father.toDto(getOwner(father, owners), getFather(father, horses, owners), getMother(father, horses, owners));
    }
    return parent;
  }

  private HorseDetailDto getMother(Horse horse, List<Horse> horses, Map<Long, OwnerDto> owners) {
    HorseDetailDto parent = null;
    var parentId = horse.getMotherId();
    if (parentId != null) {
      boolean found = false;
      Horse mother = null;
      for (Horse h : horses) {
        if (h.getId().equals(parentId)) {
          found = true;
          mother = h;
        }
      }
      if (!found) {
        throw new FatalException("Given horses list does not contain mother of this Horse (%d)".formatted(horse.getId()));
      }
      parent = entityToDetailDto(mother, owners, horses);
    }
    return parent;
  }

  private HorseDetailDto getFatherLimited(Horse horse, List<Horse> horses, Map<Long, OwnerDto> owners, int generations) {
    if (generations == 0) {
      return null;
    }
    HorseDetailDto parent = null;
    var parentId = horse.getFatherId();
    if (parentId != null) {
      boolean found = false;
      Horse father = null;
      for (Horse h : horses) {
        if (h.getId().equals(parentId)) {
          found = true;
          father = h;
        }
      }
      if (!found) {
        throw new FatalException("Given horses list does not contain father of this Horse (%d)".formatted(horse.getId()));
      }
      parent = entityToDetailDtoLimited(father, owners, horses, generations - 1);
      // parent = father.toDto(getOwner(father, owners), getFather(father, horses, owners), getMother(father, horses, owners));
    }
    return parent;
  }

  private HorseDetailDto getMotherLimited(Horse horse, List<Horse> horses, Map<Long, OwnerDto> owners, int generations) {
    if (generations == 0) {
      return null;
    }
    HorseDetailDto parent = null;
    var parentId = horse.getFatherId();
    if (parentId != null) {
      boolean found = false;
      Horse mother = null;
      for (Horse h : horses) {
        if (h.getId().equals(parentId)) {
          found = true;
          mother = h;
        }
      }
      if (!found) {
        throw new FatalException("Given horses list does not contain mother of this Horse (%d)".formatted(horse.getId()));
      }
      parent = entityToDetailDtoLimited(mother, owners, horses, generations - 1);
      // parent = father.toDto(getOwner(father, owners), getFather(father, horses, owners), getMother(father, horses, owners));
    }
    return parent;
  }


}
