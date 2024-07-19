package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entity.Entity;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T extends Entity, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    void delete(ID id);
    List<T> findAll();
    Long count();
}
