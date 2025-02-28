package com.nayak.Notes_APP.service;

import com.nayak.Notes_APP.entity.Notes;
import com.nayak.Notes_APP.repo.NotesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotesServiceImpl implements NotesService{

    @Autowired
    NotesRepo notesRepo;

    @Override
    public Notes saveNotes(Notes notes) {
        if (notes == null || notes.getFileId() == null || notes.getLanguage() == null) {
            throw new IllegalArgumentException("Note URL and language must not be null");
        }

        return notesRepo.save(notes);
    }

    @Override
    public List<Notes> getNotesByLang(String lang) {
        return notesRepo.findByLanguage(lang);
    }

}
