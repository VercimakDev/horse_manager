package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , description = ?"
      + "  , date_of_birth = ?"
      + "  , sex = ?"
      + "  , owner_id = ?"
      + "  , father_id = ?"
      + "  , mother_id = ?"
      + " WHERE id = ?";
  private static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME + "(name,description,date_of_birth,sex,owner_id,father_id,mother_id) VALUES(";
  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
  private final JdbcTemplate jdbcTemplate;

  public HorseJdbcDao(
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcTemplate.query(SQL_SELECT_ALL, this::mapRow);
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }
    LOG.info("Fatherid of the given horse: " + horses.get(0).getFatherId());
    return horses.get(0);
  }

  @Override
  public List<Horse> filter(String input, Sex sex) throws NotFoundException {
    LOG.trace("filter({})", input, sex);
    List<Horse> horses =
        jdbcTemplate.query(SQL_SELECT_ALL + " WHERE name ILIKE '%" + input + "%' AND sex ILIKE '" + sex.toString() + "' LIMIT 5;", this::mapRow);
    LOG.info(horses.toString());
    return horses;
  }

  @Override
  public Horse create(HorseDetailDto horse) {
    LOG.trace("create({})", horse);
    int created = jdbcTemplate.update(SQL_CREATE + "?,?,?,?,?,?,?);",
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex().toString(),
        horse.ownerId(),
        horse.fatherId(),
        horse.motherId());
    LOG.info("created horse with name:" + horse.name() + " and father:" + horse.fatherId());
    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setDescription(horse.description())
        .setDateOfBirth(horse.dateOfBirth())
        .setSex(horse.sex())
        .setOwnerId(horse.ownerId())
        .setFather(horse.fatherId())
        .setMother(horse.motherId())
        ;
  }

  @Override
  public long delete(long id) throws NotFoundException {
    int deleted = jdbcTemplate.update(SQL_DELETE, id);
    if (deleted == 0) {
      throw new NotFoundException("Could not update horse with ID " + id + ", because it does not exist");
    }
    return id;
  }

  @Override
  public List<Horse> search(HorseSearchDto horse, List<OwnerDto> ownerMatchingIDs) {
    LOG.trace("search({})", horse);
    String preparedStatement = SQL_SELECT_ALL + " WHERE";
    int ands = 0;
    if (horse.name() != null && !horse.name().isBlank()) {
      preparedStatement += " name ILIKE '%" + horse.name() + "%'";
      ands++;
    }
    if (horse.description() != null && !horse.description().isBlank()) {
      if (addAnds(ands)) {
        preparedStatement += " AND ";
      } else {
        ands++;
      }
      preparedStatement += " description ILIKE '%" + horse.description() + "%'";
    }
    if (horse.bornBefore() != null) {
      if (addAnds(ands)) {
        preparedStatement += " AND ";
      } else {
        ands++;
      }
      preparedStatement += " date_of_birth < '" + horse.bornBefore().toString() + "'";
    }
    if (horse.sex() != null) {
      if (addAnds(ands)) {
        preparedStatement += " AND ";
      } else {
        ands++;
      }
      preparedStatement += " sex ILIKE '" + horse.sex().toString() + "%'";
    }
    if (ownerMatchingIDs != null && ownerMatchingIDs.size() != 0) {
      if (addAnds(ands)) {
        preparedStatement += " AND ";
      } else {
        ands++;
      }
      StringBuilder sb = new StringBuilder();
      sb.append('(');
      for (OwnerDto owner : ownerMatchingIDs) {
        if (ownerMatchingIDs.get(ownerMatchingIDs.size() - 1) == owner) {
          sb.append("owner_id = " + owner.id() + ")");
        } else {
          sb.append("owner_id = " + owner.id() + " OR ");
        }
      }
      preparedStatement += sb.toString();
    }
    LOG.info("Getting query for this statement: " + preparedStatement);
    List<Horse> horses = jdbcTemplate.query(preparedStatement, this::mapRow);
    LOG.info(horses.toString());
    return horses;
  }

  public boolean addAnds(int ands) {
    return ands > 0;
  }


  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex().toString(),
        horse.ownerId(),
        horse.fatherId(),
        horse.motherId(),
        horse.id());
    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setDescription(horse.description())
        .setDateOfBirth(horse.dateOfBirth())
        .setSex(horse.sex())
        .setOwnerId(horse.ownerId())
        ;
  }


  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setDescription(result.getString("description"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setSex(Sex.valueOf(result.getString("sex")))
        .setOwnerId(result.getObject("owner_id", Long.class))
        .setFather(result.getObject("father_id", Long.class))
        .setMother(result.getObject("mother_id", Long.class))
        ;
  }
}
