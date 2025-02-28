package com.nayak.Notes_APP.service;

import com.nayak.Notes_APP.entity.Notes;

import java.util.List;

public interface NotesService{
    List<Notes> getNotesByLang(String lang);

    Notes saveNotes(Notes notes);
}
