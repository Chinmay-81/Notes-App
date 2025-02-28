package com.nayak.Notes_APP.repo;

import com.nayak.Notes_APP.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotesRepo extends JpaRepository<Notes,Integer> {
    List<Notes> findByLanguage(String language);

}
